import subprocess
import json
import time
import os
import sys
from pathlib import Path
from openai import OpenAI
from dotenv import load_dotenv

# Load env variables from root .env
load_dotenv(dotenv_path=Path(__file__).resolve().parent.parent / ".env")

COMPARATOR_REPEATABILITY_PROMPT = """You are the Lead AI Quiz Architect preparing a Model Repeatability and Selection Report for Course Teaching Assistants (TAs).
You will be given the raw data from 3 separate evaluation trials run for each configuration.
The data contains, for each configuration (model + prompt style):
- The trial run statistics (scores, latency, deterministic issue counts)
- The aggregated summary (average quality score, average latency, average deterministic issues, parse success rate)

Your task is to write a final, comprehensive justification report for the TAs.
You must provide:
1. A summary Markdown table displaying the aggregated stats:
   - Configuration Name
   - Avg Quality Score (with +/- stdev)
   - Avg Latency (seconds) (with +/- stdev)
   - Avg Deterministic Issues (with +/- stdev)
   - Parse Success Rate (%)
2. A detailed analysis of **repeatability/consistency**:
   - Which models are stable and produce consistent quality?
   - Which models show high variance in latency or quality?
   - Are there models that failed/warned in any of the runs?
3. A formal trade-off analysis summarizing speed vs. quality vs. stability.
4. Your final, evidence-based choice of model and prompt configuration for the production pipeline, fully formatted to convince course TAs.

Respond in a professional, rigorous markdown format.
"""

def get_evaluator_client() -> tuple[OpenAI, str]:
    if os.getenv("DEEPSEEK_API_KEY"):
        return OpenAI(base_url="https://api.deepseek.com", api_key=os.getenv("DEEPSEEK_API_KEY")), "deepseek-chat"
    elif os.getenv("OPENROUTER_API_KEY"):
        return OpenAI(base_url="https://openrouter.ai/api/v1", api_key=os.getenv("OPENROUTER_API_KEY")), "openai/gpt-4o-mini"
    else:
        raise ValueError("Neither DEEPSEEK_API_KEY nor OPENROUTER_API_KEY is configured in .env.")

def main():
    num_trials = 3
    base_dir = Path(__file__).resolve().parent
    python_exe = sys.executable if sys.executable else "python"
    
    print(f"Starting {num_trials} repeatability trials...")
    trial_data = []

    for trial in range(1, num_trials + 1):
        print(f"\n==========================================")
        print(f"TRIAL RUN {trial}/{num_trials}")
        print(f"==========================================")
        
        json_path = base_dir / f"trial_{trial}.json"
        report_path = base_dir / f"trial_report_{trial}.md"
        
        # Run evaluate_models.py as a subprocess
        cmd = [
            python_exe,
            str(base_dir / "evaluate_models.py"),
            "--count", "3",
            "--output", str(report_path),
            "--save-json", str(json_path)
        ]
        
        print(f"Executing: {' '.join(cmd)}")
        result = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8")
        if result.returncode != 0:
            print(f"Error executing trial {trial}: {result.stderr}")
            # Try running with generic 'python' if sys.executable failed or was wrong
            if python_exe != "python":
                print("Retrying with 'python' command...")
                cmd[0] = "python"
                result = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8")
                
            if result.returncode != 0:
                print(f"Fatal error executing trial {trial}: {result.stderr}")
                return
                
        # Load the raw results from JSON
        if json_path.exists():
            with open(json_path, "r", encoding="utf-8") as f:
                trial_results = json.load(f)
                trial_data.append(trial_results)
            print(f"Trial {trial} completed and raw results loaded.")
        else:
            print(f"Error: Trial {trial} did not output JSON file at {json_path}")
            return

    # Aggregate statistics
    configs_summary = {}
    
    # Initialize dictionary for configs
    for config_res in trial_data[0]:
        name = config_res["name"]
        configs_summary[name] = {
            "model": config_res["model"],
            "provider": config_res["provider"],
            "latencies": [],
            "issues": [],
            "scores": [],
            "parsing": []
        }

    for trial_idx, trial_res in enumerate(trial_data):
        for config_res in trial_res:
            name = config_res["name"]
            configs_summary[name]["latencies"].append(config_res["latency_seconds"])
            configs_summary[name]["issues"].append(config_res["deterministic_issues_count"])
            configs_summary[name]["scores"].append(config_res["total_score"])
            configs_summary[name]["parsing"].append(1 if config_res["parsing_success"] else 0)

    # Compute stats
    import statistics
    
    def mean_or_zero(data):
        return sum(data) / len(data) if data else 0
        
    def stdev_or_zero(data):
        return statistics.stdev(data) if len(data) > 1 else 0

    aggregated_stats = {}
    for name, data in configs_summary.items():
        avg_lat = mean_or_zero(data["latencies"])
        std_lat = stdev_or_zero(data["latencies"])
        avg_iss = mean_or_zero(data["issues"])
        std_iss = stdev_or_zero(data["issues"])
        avg_sco = mean_or_zero(data["scores"])
        std_sco = stdev_or_zero(data["scores"])
        parse_rate = mean_or_zero(data["parsing"]) * 100
        
        aggregated_stats[name] = {
            "model": data["model"],
            "provider": data["provider"],
            "avg_latency": round(avg_lat, 2),
            "std_latency": round(std_lat, 2),
            "avg_issues": round(avg_iss, 2),
            "std_issues": round(std_iss, 2),
            "avg_score": round(avg_sco, 2),
            "std_score": round(std_sco, 2),
            "parse_success_rate": round(parse_rate, 1)
        }

    # Save aggregated stats to file
    results_path = base_dir / "repeatability_results.json"
    with open(results_path, "w", encoding="utf-8") as rf:
        json.dump({
            "trials_raw": trial_data,
            "aggregated": aggregated_stats
        }, rf, indent=2, ensure_ascii=False)
    print(f"\nAggregated repeatability stats saved to: {results_path}")

    # Invoke lead comparator agent to generate final justification report
    try:
        eval_client, eval_model = get_evaluator_client()
        print(f"Comparator Agent initialized using {eval_model}")
    except ValueError as e:
        print(f"Error: {e}")
        return

    comparator_payload = {
        "trials_raw": trial_data,
        "aggregated": aggregated_stats
    }
    
    print("\nRunning Comparator Agent for final repeatability report...")
    try:
        comp_response = eval_client.chat.completions.create(
            model=eval_model,
            messages=[
                {"role": "system", "content": COMPARATOR_REPEATABILITY_PROMPT},
                {"role": "user", "content": f"Repeatability Data:\n\n{json.dumps(comparator_payload, indent=2, ensure_ascii=False)}"}
            ],
            temperature=0.2
        )
        report_md = comp_response.choices[0].message.content
    except Exception as e:
        report_md = f"# Model Selection & Repeatability Report\n\nError calling comparator agent: {e}"

    # Write report file
    report_output_path = base_dir / "repeatability_report.md"
    with open(report_output_path, "w", encoding="utf-8") as rf:
        rf.write(report_md)
    print(f"\nFinal Repeatability Report saved to: {report_output_path}")
    print("\n=== FINAL REPEATABILITY REPORT ===")
    print(report_md)

if __name__ == "__main__":
    main()
