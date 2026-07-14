# Model Repeatability and Selection Report

**Prepared for:** Course Teaching Assistants  
**Date:** [Current Date]  
**Prepared by:** Lead AI Quiz Architect

---

## 1. Aggregated Performance Summary

| Configuration | Avg Quality Score | Avg Latency (s) | Avg Deterministic Issues | Parse Success Rate |
|---|---|---|---|---|
| **minimax_m3** | 33.67 ± 3.06 | 7.39 ± 0.45 | 0.67 ± 0.58 | 100% |
| **nemotron_free** | 37.00 ± 0.00 | 10.75 ± 3.66 | 2.00 ± 0.00 | 100% |
| **gemini_3_flash_preview** | 37.00 ± 0.00 | 4.68 ± 0.37 | 1.67 ± 1.15 | 100% |
| **deepseek_standard** | 37.00 ± 0.00 | 4.33 ± 0.39 | 4.00 ± 1.00 | 100% |
| **deepseek_strict** | 37.00 ± 0.00 | 4.32 ± 0.22 | 1.00 ± 0.00 | 100% |

---

## 2. Repeatability & Consistency Analysis

### 2.1 Stable Models (Low Variance)

**deepseek_strict** emerges as the most consistent performer across all metrics:
- **Quality Score:** Perfect 37.00 across all 3 trials (σ = 0.00)
- **Latency:** 4.32 ± 0.22 seconds — remarkably stable
- **Deterministic Issues:** Exactly 1 issue per trial (σ = 0.00) — completely predictable
- **Parse Success:** 100% across all runs

**nemotron_free** shows perfect quality consistency (σ = 0.00) but suffers from:
- **High latency variance:** 10.75 ± 3.66 seconds (range: 6.54s to 13.2s)
- This indicates unpredictable API response times, likely due to its "free" tier status

**gemini_3_flash_preview** demonstrates excellent stability:
- Perfect quality scores (σ = 0.00)
- Low latency variance: 4.68 ± 0.37 seconds
- Moderate issue variance (σ = 1.15) — issues fluctuate between 1-3 per trial

### 2.2 High Variance Models

**minimax_m3** is the least consistent:
- **Quality Score:** 33.67 ± 3.06 — the only model with quality variance
- **Latency:** 7.39 ± 0.45 — moderate variance
- **Issues:** 0.67 ± 0.58 — inconsistent issue counts
- **Critical Concern:** Trial 1 scored 31 (format compliance failure with multiple correct answers), Trial 2 scored 33, Trial 3 scored 37. This inconsistency in format compliance is unacceptable for production.

**deepseek_standard** shows concerning patterns:
- **Issues:** 4.00 ± 1.00 — highest average and variance
- **Pattern detected:** Correct answer is consistently the longest option (ratio > 1.95 in trials 2 and 3)
- This creates a "gaming" vulnerability where students could identify correct answers by length alone

### 2.3 Failure/Warning Analysis

| Configuration | Failures | Warnings |
|---|---|---|
| minimax_m3 | Trial 1: Format compliance failure (multiple correct answers without clear marking) | Trial 1: High word overlap between options |
| deepseek_standard | None critical | Trials 2 & 3: Systematic correct-answer-length bias detected |
| All others | None | Minor word overlap issues only |

---

## 3. Trade-off Analysis: Speed vs. Quality vs. Stability

### Speed Leaders
| Rank | Model | Avg Latency | Quality | Stability |
|---|---|---|---|---|
| 1 | deepseek_strict | 4.32s | 37.0 | Excellent |
| 2 | deepseek_standard | 4.33s | 37.0 | Good (but issue-prone) |
| 3 | gemini_3_flash_preview | 4.68s | 37.0 | Excellent |

### Quality Leaders (all tied at 37.0)
- **deepseek_strict**, **deepseek_standard**, **gemini_3_flash_preview**, **nemotron_free**
- **minimax_m3** is eliminated due to inconsistent quality (33.67 avg)

### Stability Leaders
| Rank | Model | Score σ | Latency σ | Issues σ |
|---|---|---|---|---|
| 1 | deepseek_strict | 0.00 | 0.22 | 0.00 |
| 2 | gemini_3_flash_preview | 0.00 | 0.37 | 1.15 |
| 3 | deepseek_standard | 0.00 | 0.39 | 1.00 |

### Trade-off Matrix

| Configuration | Speed (1-5) | Quality (1-5) | Stability (1-5) | Overall |
|---|---|---|---|---|
| deepseek_strict | ★★★★★ | ★★★★★ | ★★★★★ | **15/15** |
| gemini_3_flash_preview | ★★★★★ | ★★★★★ | ★★★★☆ | 14/15 |
| deepseek_standard | ★★★★★ | ★★★★★ | ★★★☆☆ | 13/15 |
| nemotron_free | ★★☆☆☆ | ★★★★★ | ★★★☆☆ | 10/15 |
| minimax_m3 | ★★★☆☆ | ★★★★☆ | ★★☆☆☆ | 9/15 |

---

## 4. Final Recommendation

### Production Choice: **deepseek_strict**

**Justification:**

1. **Perfect Quality Consistency:** 37.00/37.00 across all 3 trials with zero variance. Every quiz generated meets all evaluation criteria (document alignment, exam prep value, balanced difficulty, format compliance).

2. **Superior Stability:** 
   - Lowest latency variance (σ = 0.22s) ensures predictable response times
   - Zero variance in deterministic issues (exactly 1 per trial) — completely predictable behavior
   - 100% parse success rate across all trials

3. **Optimal Speed:** At 4.32 seconds average, it's the fastest model alongside deepseek_standard, making it suitable for real-time or batch processing.

4. **No Systematic Biases:** Unlike deepseek_standard (which showed correct-answer-length bias), deepseek_strict produces options of approximately equal length, preventing students from "gaming" the system.

5. **Format Compliance:** The strict prompt engineering ensures:
   - Exactly one correct answer per question (no ambiguous multiple-correct issues)
   - No "Select all that apply" or negative phrasing
   - Consistent option length and structure

6. **Cost-Effective:** Using DeepSeek's API (deepseek-chat) provides enterprise-grade reliability without the latency variance seen in free-tier models like nemotron_free.

### Runner-Up: **gemini_3_flash_preview**

If deepseek_strict becomes unavailable, gemini_3_flash_preview is the recommended backup:
- Slightly higher latency (4.68s vs 4.32s)
- Slightly higher issue variance (σ = 1.15 vs 0.00)
- Still maintains perfect quality scores

### Eliminated Configurations

| Configuration | Reason for Elimination |
|---|---|
| **minimax_m3** | Inconsistent quality (σ = 3.06), format compliance failures |
| **nemotron_free** | Unacceptably high latency variance (σ = 3.66s), unpredictable response times |
| **deepseek_standard** | Systematic correct-answer-length bias, highest issue count (avg 4.0) |

---

## 5. Implementation Notes for TAs

- **Prompt Template:** The strict prompt (with explicit format rules) is critical to deepseek_strict's success. Do not modify the prompt without re-running validation trials.
- **Monitoring:** Track deterministic_issues_count in production. If it exceeds 2 per quiz, flag for review.
- **Fallback:** Configure gemini_3_flash_preview as automatic fallback if deepseek_strict latency exceeds 10 seconds or returns parse errors.
- **Scaling:** At ~4.3 seconds per quiz, expect throughput of ~14 quizzes per minute per API key. Scale horizontally with multiple keys if needed.

**Verdict:** Deploy **deepseek_strict** to production immediately. This configuration offers the best combination of speed, quality, and repeatability, with zero quality variance across all validation trials.