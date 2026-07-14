import json
import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

def main():
    base_dir = Path(__file__).resolve().parent
    results_path = base_dir / "repeatability_results.json"
    
    if not results_path.exists():
        print(f"Error: results file not found at {results_path}")
        return
        
    with open(results_path, "r", encoding="utf-8") as f:
        data = json.load(f)
        
    aggregated = data.get("aggregated", {})
    
    models = list(aggregated.keys())
    scores = [aggregated[m]["avg_score"] for m in models]
    score_errors = [aggregated[m]["std_score"] for m in models]
    latencies = [aggregated[m]["avg_latency"] for m in models]
    latency_errors = [aggregated[m]["std_latency"] for m in models]
    
    x = np.arange(len(models))
    width = 0.35
    
    # Create matplotlib plot
    fig, ax1 = plt.subplots(figsize=(10, 6))
    
    # Color palette
    color_quality = "#1f77b4" # Slate blue
    color_latency = "#ff7f0e" # Muted orange
    
    # Plot quality score (left axis)
    rects1 = ax1.bar(x - width/2, scores, width, yerr=score_errors, 
                     label='Avg Quality Score (out of 40)', color=color_quality, alpha=0.85, capsize=5)
    ax1.set_xlabel('Model Configuration', fontweight='bold', labelpad=15)
    ax1.set_ylabel('Quality Score (Higher is Better)', color=color_quality, fontweight='bold')
    ax1.tick_params(axis='y', labelcolor=color_quality)
    ax1.set_ylim(0, 45)
    
    # Instantiate a second axis sharing the same x-axis
    ax2 = ax1.twinx()
    rects2 = ax2.bar(x + width/2, latencies, width, yerr=latency_errors,
                     label='Avg Latency (seconds)', color=color_latency, alpha=0.85, capsize=5)
    ax2.set_ylabel('Latency (Seconds, Lower is Better)', color=color_latency, fontweight='bold')
    ax2.tick_params(axis='y', labelcolor=color_latency)
    ax2.set_ylim(0, max(latencies) * 1.2)
    
    # Title and ticks
    plt.title('AI Quiz Generation: Model Performance Trade-off', fontsize=14, fontweight='bold', pad=20)
    ax1.set_xticks(x)
    ax1.set_xticklabels(models, rotation=15, ha='right')
    
    # Grid lines (only for left axis to avoid clutter)
    ax1.grid(True, axis='y', linestyle='--', alpha=0.3)
    
    # Remove top spines
    ax1.spines['top'].set_visible(False)
    ax2.spines['top'].set_visible(False)
    
    # Add data labels inside bars
    for rect in rects1:
        height = rect.get_height()
        ax1.annotate(f'{height:.1f}',
                    xy=(rect.get_x() + rect.get_width() / 2, height),
                    xytext=(0, 3),  # 3 points vertical offset
                    textcoords="offset points",
                    ha='center', va='bottom', fontsize=9, fontweight='bold')
                    
    for rect in rects2:
        height = rect.get_height()
        ax2.annotate(f'{height:.1f}s',
                    xy=(rect.get_x() + rect.get_width() / 2, height),
                    xytext=(0, 3),  # 3 points vertical offset
                    textcoords="offset points",
                    ha='center', va='bottom', fontsize=9, fontweight='bold')

    plt.tight_layout()
    
    # Save the output image
    output_path = base_dir / "model_comparison_chart.png"
    plt.savefig(output_path, dpi=300)
    print(f"Chart successfully saved to: {output_path}")

if __name__ == "__main__":
    main()
