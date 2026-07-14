import os
import json
import argparse
import re
import time
from pathlib import Path
from typing import Any, Dict, List, Optional
from openai import OpenAI
from dotenv import load_dotenv

# Load env variables from root .env
load_dotenv(dotenv_path=Path(__file__).resolve().parent.parent / ".env")

# Import deterministic quality checker from codebase
from quality_checker import run_all_checks

EVALUATOR_SYSTEM_PROMPT = """You are an expert educational quality auditor.
Your job is to evaluate a generated quiz against a source document using four criteria.
For each criterion, assign a score from 1 to 10 (10 being perfect) and provide a concise explanation.

CRITERIA:
1. document_alignment: Do the questions test concepts present in the source text? Are they factually correct according to the text? (1-10)
2. exam_prep_help: Do the questions test important knowledge that would help a student prepare for an exam on the subject? (1-10)
3. balanced_difficulty: Is the difficulty level balanced? Avoid questions that are trivial or excessively detailed. (1-10)
4. format_compliance: Does the quiz follow the required structure? Each question must have exactly 4 options. Avoid questions with multiple correct options (unless they are clearly marked), and avoid invalid formats. (1-10)

Respond strictly in the following JSON format (no other text, no markdown code block formatting except valid json):
{
  "scores": {
    "document_alignment": <1-10>,
    "exam_prep_help": <1-10>,
    "balanced_difficulty": <1-10>,
    "format_compliance": <1-10>
  },
  "explanations": {
    "document_alignment": "...",
    "exam_prep_help": "...",
    "balanced_difficulty": "...",
    "format_compliance": "..."
  }
}
"""

COMPARATOR_SYSTEM_PROMPT = """You are the Lead AI Quiz Architect preparing a final model selection report for Course Teaching Assistants (TAs).
You will be given a list of configuration runs. Each run contains performance and quality metrics:
- The configuration name (model name + prompt style)
- Generation Latency (seconds)
- Deterministic Quality Issues (count of structural violations found by a static checker)
- Response Character Length
- Parsing Success (True/False)
- Subjective Evaluator Scores & Explanations
- The generated quiz questions

Your task is to analyze all configurations and output a rigorous report justifying the model choice for the TAs.
Provide:
1. A summary table comparing all runs across:
   - Latency (seconds)
   - Deterministic Quality Issues
   - Total Quality Score (Evaluator sum / 40)
   - Parsing Success
   - Output Character Length
2. A detailed critical review of each configuration's efficiency and output quality.
3. A formal trade-off analysis (e.g. speed vs. quality, cost/API efficiency).
4. A definitive, evidence-based recommendation for the TAs explaining why the selected model+prompt is optimal for production deployment.

Output your report in a readable, professional markdown format. Include the comparison table.
"""

def parse_json_robust(content: str) -> Any:
    # Try to extract JSON from code fences first
    json_match = re.search(r"```(?:json)?\s*\n?(.*?)\n?```", content, re.DOTALL)
    if json_match:
        content = json_match.group(1).strip()
    return json.loads(content)

def get_client_for_provider(provider: str) -> OpenAI:
    if provider == "openrouter":
        api_key = os.getenv("OPENROUTER_API_KEY")
        if not api_key:
            raise ValueError("OPENROUTER_API_KEY is not set in .env")
        return OpenAI(base_url="https://openrouter.ai/api/v1", api_key=api_key)
    elif provider == "deepseek":
        api_key = os.getenv("DEEPSEEK_API_KEY")
        if not api_key:
            raise ValueError("DEEPSEEK_API_KEY is not set in .env")
        return OpenAI(base_url="https://api.deepseek.com", api_key=api_key)
    else:
        raise ValueError(f"Unknown provider: {provider}")

def get_evaluator_client() -> tuple[OpenAI, str]:
    if os.getenv("DEEPSEEK_API_KEY"):
        return OpenAI(base_url="https://api.deepseek.com", api_key=os.getenv("DEEPSEEK_API_KEY")), "deepseek-chat"
    elif os.getenv("OPENROUTER_API_KEY"):
        return OpenAI(base_url="https://openrouter.ai/api/v1", api_key=os.getenv("OPENROUTER_API_KEY")), "openai/gpt-4o-mini"
    else:
        raise ValueError("Neither DEEPSEEK_API_KEY nor OPENROUTER_API_KEY is configured in .env.")

def main():
    parser = argparse.ArgumentParser(description="Evaluate multiple LLM configurations for quiz generation quality.")
    parser.add_argument("--document", type=str, default="llm-service/evaluation_doc.txt", help="Path to input document")
    parser.add_argument("--configs", type=str, default="llm-service/evaluation_configs.json", help="Path to configs JSON file")
    parser.add_argument("--output", type=str, default="llm-service/evaluation_report.md", help="Path to output report file")
    parser.add_argument("--count", type=int, default=5, help="Number of questions to generate per run")
    parser.add_argument("--save-json", type=str, default=None, help="Path to save raw results in JSON format")
    args = parser.parse_args()

    # Resolve paths
    doc_path = Path(args.document)
    configs_path = Path(args.configs)
    output_path = Path(args.output)

    if not doc_path.exists():
        print(f"Error: Document file not found: {doc_path}")
        return
    if not configs_path.exists():
        print(f"Error: Configs file not found: {configs_path}")
        return

    # Load source text
    print(f"Loading document from: {doc_path}")
    source_text = doc_path.read_text(encoding="utf-8")

    # Load configs
    print(f"Loading configurations from: {configs_path}")
    with open(configs_path, "r", encoding="utf-8") as f:
        configs = json.load(f)

    # Initialize evaluator
    try:
        eval_client, eval_model = get_evaluator_client()
        print(f"Evaluator initialized using provider/model: {eval_model}")
    except ValueError as e:
        print(f"Error: {e}")
        return

    # Create directory for generated quizzes
    quizzes_dir = doc_path.parent / "evaluation_quizzes"
    quizzes_dir.mkdir(parents=True, exist_ok=True)

    results = []

    for i, config in enumerate(configs):
        name = config.get("name", f"config_{i}")
        provider = config.get("provider")
        model = config.get("model")
        prompt_template = config.get("prompt")

        print(f"\n==================================================")
        print(f"RUNNING CONFIGURATION {i+1}/{len(configs)}: {name}")
        print(f"Provider: {provider} | Model: {model}")
        print(f"==================================================")

        # Metrics to collect
        latency = 0.0
        response_char_len = 0
        parsing_success = False
        auto_issues_count = 0
        auto_issues_details = []
        quiz_cards = []
        gen_error = None

        # 1. Generate Quiz (with time measurement)
        start_time = time.time()
        try:
            client = get_client_for_provider(provider)
            system_prompt = prompt_template.format(count=args.count)
            
            print(f"Calling generator model '{model}'...")
            response = client.chat.completions.create(
                model=model,
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": f"Source document:\n\n{source_text}"}
                ],
                temperature=0.3
            )
            
            latency = time.time() - start_time
            raw_content = response.choices[0].message.content
            response_char_len = len(raw_content) if raw_content else 0
            print(f"Successfully received generator response in {latency:.2f}s. Parsing...")
            
            try:
                # Parse cards
                parsed = parse_json_robust(raw_content)
                if isinstance(parsed, dict) and "items" in parsed:
                    quiz_cards = parsed["items"]
                elif isinstance(parsed, list):
                    quiz_cards = parsed
                else:
                    raise ValueError(f"Expected list or 'items' key, got {type(parsed)}")
                
                parsing_success = True
                print(f"Generated {len(quiz_cards)} quiz questions.")
                
                # Deterministic structural quality checks
                check_report = run_all_checks(quiz_cards)
                auto_issues_count = check_report["total_issues"]
                auto_issues_details = [issue["detail"] for issue in check_report["issues"]]
                print(f"Deterministic checks: {auto_issues_count} issue(s) detected.")
                
                # Save generated quiz
                quiz_file = quizzes_dir / f"{name}_quiz.json"
                with open(quiz_file, "w", encoding="utf-8") as qf:
                    json.dump(quiz_cards, qf, indent=2, ensure_ascii=False)
                print(f"Saved generated quiz to: {quiz_file}")
                
            except Exception as pe:
                gen_error = f"JSON parsing failed: {pe}. Raw output: {raw_content[:300]}..."
                print(f"Warning: {gen_error}")
                
        except Exception as ge:
            latency = time.time() - start_time
            gen_error = f"API call failed: {ge}"
            print(f"Warning: {gen_error}")

        # 2. Evaluate Quiz
        scores = {}
        explanations = {}
        
        if gen_error:
            # Assign lowest scores due to generation failure
            scores = {
                "document_alignment": 1,
                "exam_prep_help": 1,
                "balanced_difficulty": 1,
                "format_compliance": 1
            }
            explanations = {
                "document_alignment": "Skipped due to generation error.",
                "exam_prep_help": "Skipped due to generation error.",
                "balanced_difficulty": "Skipped due to generation error.",
                "format_compliance": gen_error
            }
        else:
            print(f"Evaluating generated quiz...")
            eval_user_content = (
                f"Source document:\n{source_text[:3000]}\n\n"
                f"Generated Quiz:\n{json.dumps(quiz_cards, indent=2, ensure_ascii=False)}"
            )
            
            try:
                eval_response = eval_client.chat.completions.create(
                    model=eval_model,
                    messages=[
                        {"role": "system", "content": EVALUATOR_SYSTEM_PROMPT},
                        {"role": "user", "content": eval_user_content}
                    ],
                    temperature=0.1,
                    response_format={"type": "json_object"}
                )
                
                eval_raw = eval_response.choices[0].message.content
                eval_data = parse_json_robust(eval_raw)
                
                scores = eval_data.get("scores", {})
                explanations = eval_data.get("explanations", {})
                
                # Validation
                required_criteria = ["document_alignment", "exam_prep_help", "balanced_difficulty", "format_compliance"]
                for rc in required_criteria:
                    if rc not in scores:
                        scores[rc] = 1
                    if rc not in explanations:
                        explanations[rc] = "No explanation provided by evaluator."
                        
            except Exception as ee:
                print(f"Warning: Evaluation failed: {ee}")
                scores = {
                    "document_alignment": 1,
                    "exam_prep_help": 1,
                    "balanced_difficulty": 1,
                    "format_compliance": 1
                }
                explanations = {
                    "document_alignment": "Evaluation execution failed.",
                    "exam_prep_help": "Evaluation execution failed.",
                    "balanced_difficulty": "Evaluation execution failed.",
                    "format_compliance": f"Evaluation error: {ee}"
                }

        total_score = sum(scores.values())
        print(f"Scores for '{name}':")
        for k, v in scores.items():
            print(f"  - {k}: {v}/10")
        print(f"  Total Score: {total_score}/40")
        print(f"  Latency: {latency:.2f}s | Deterministic Issues: {auto_issues_count}")
        
        results.append({
            "name": name,
            "provider": provider,
            "model": model,
            "prompt": prompt_template,
            "latency_seconds": round(latency, 2),
            "response_char_len": response_char_len,
            "parsing_success": parsing_success,
            "deterministic_issues_count": auto_issues_count,
            "deterministic_issues_details": auto_issues_details,
            "scores": scores,
            "explanations": explanations,
            "total_score": total_score,
            "quiz": quiz_cards
        })

    # 3. Overall Comparison (Lead Agent)
    print(f"\n==================================================")
    print(f"RUNNING COMPARATOR AGENT...")
    print(f"==================================================")
    
    # Prepare comparison context
    comparison_data = []
    for r in results:
        comparison_data.append({
            "name": r["name"],
            "provider": r["provider"],
            "model": r["model"],
            "latency_seconds": r["latency_seconds"],
            "response_char_len": r["response_char_len"],
            "parsing_success": r["parsing_success"],
            "deterministic_issues_count": r["deterministic_issues_count"],
            "deterministic_issues_details": r["deterministic_issues_details"],
            "scores": r["scores"],
            "explanations": r["explanations"],
            "total_score": r["total_score"],
            "quiz": r["quiz"]
        })
        
    comparator_user_content = f"Configurations and evaluation results:\n\n{json.dumps(comparison_data, indent=2, ensure_ascii=False)}"
    
    try:
        comp_response = eval_client.chat.completions.create(
            model=eval_model,
            messages=[
                {"role": "system", "content": COMPARATOR_SYSTEM_PROMPT},
                {"role": "user", "content": comparator_user_content}
            ],
            temperature=0.2
        )
        report_md = comp_response.choices[0].message.content
    except Exception as ce:
        report_md = f"# Model Comparison Report\n\nError running comparator agent: {ce}\n\n## Scores Summary\n\n"
        for r in results:
            report_md += f"- **{r['name']}**: {r['total_score']}/40 (Latency: {r['latency_seconds']}s)\n"

    # Write report file
    with open(output_path, "w", encoding="utf-8") as rf:
        rf.write(report_md)
    print(f"\nReport successfully saved to: {output_path}")
    
    # Save raw JSON results if requested
    if args.save_json:
        with open(args.save_json, "w", encoding="utf-8") as jf:
            json.dump(results, jf, indent=2, ensure_ascii=False)
        print(f"Raw results successfully saved to: {args.save_json}")
    
    # Print the report to stdout
    print("\n=== FINAL COMPARISON REPORT ===")
    print(report_md)

if __name__ == "__main__":
    main()
