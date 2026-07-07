# Agent-Based Quiz Generation

> **Experimental feature** — AI agent that generates quiz cards with automated quality evaluation and LLM-based refinement. The agent acts like a "quality inspector": it generates cards, checks them, and if anything looks suspicious, it fixes them before returning.

## Architecture Overview

The agent uses a **hybrid evaluation loop**:

```
  ┌─────────────────────────────────────────────────────────────┐
  │                     AGENT PIPELINE                          │
  │                                                             │
  │  Source Text                                                │
  │      │                                                      │
  │      ▼                                                      │
  │  ┌──────────┐    ┌──────────────┐    ┌───────────────┐     │
  │  │ GENERATE ├───►│   CHECK      ├───►│   EVALUATE    │     │
  │  │ (LLM)    │    │ (deterministic) │  │ (LLM-based)   │     │
  │  └──────────┘    └──────────────┘    └───────┬───────┘     │
  │      ▲                                       │             │
  │      │            ┌──────────┐               │             │
  │      └────────────┤  REFINE  │◄──────────────┘             │
  │                   │ (LLM)    │   if not passing             │
  │                   └──────────┘                              │
  └─────────────────────────────────────────────────────────────┘
```

### Why hybrid?

- **Deterministic checks** (Python code) are great at catching numerical patterns: length imbalance, option similarity, grammatical hints, structural patterns. These are things LLMs consistently miss.
- **LLM evaluation** is good at subjective quality: "are the options plausible?", "does the question make sense?", "is the wrong/right answer pattern too obvious?"
- **Refinement** uses the combined issues from both checkers to give the LLM concrete, specific feedback on what to fix.

## Components

### 1. `quality_checker.py` — Automated deterministic checks

All checks are zero-cost (no API calls) and focus on structural issues:

| Check | What it detects |
|-------|----------------|
| `check_length_balance` | Correct answer significantly longer/shorter than distractors (>60% or <40%) |
| `check_pattern_always_longest` | Correct answer is ALWAYS the longest option across all cards |
| `check_pattern_always_shortest` | Correct answer is ALWAYS the shortest option across all cards |
| `check_option_similarity` | Options that are substrings of each other or have >70% word overlap |
| `check_grammatical_hints` | Question ends with "an" but only correct answer starts with a vowel |
| `check_single_word_distractors` | Distractors are 1-2 words while correct answer is 3+ (suspicious) |

### 2. `agent_generator.py` — Agent coordinator

The main entry point is `generate_cards_agent()`:

```python
def generate_cards_agent(
    text: str,          # Source text to generate cards from
    card_type: str,     # "multiple_choice" or "single_answer"
    count: int,         # Number of cards to generate (1-20)
    max_iterations: int = 3,  # Max generate→refine cycles
) -> List[dict]:
```

**Pipeline steps:**

1. **GENERATE** (Step 1): Calls LLM with a system prompt from `prompts/generator_system.txt` that includes card type descriptions and JSON examples. Produces initial set of cards.

2. **CHECK** (Step 2): Runs all deterministic checks from `quality_checker.py`. Produces a report with issues per card and aggregate stats.

3. **EVALUATE** (Step 3): Calls LLM with `prompts/evaluator_system.txt` — asks it to assess subjective quality:
   - Are distractors plausible? (not too obviously wrong)
   - Is the correct answer truly correct based on the source text?
   - Are there any formatting or clarity issues?
   - Is the difficulty level appropriate?

4. **REFINE** (Step 4): If either check failed, calls LLM with `prompts/refiner_system.txt`, providing:
   - The original source text
   - The current (flawed) cards
   - A condensed issue summary from both automated and evaluation checks

5. **REPEAT**: Loops until max_iterations or both checks pass.

### 3. `prompts/` — Prompt templates

| Template | Purpose |
|----------|---------|
| `generator_system.txt` | System prompt for the initial card generation call |
| `evaluator_system.txt` | System prompt for the quality evaluation call |
| `refiner_system.txt` | System prompt for the card refinement call |

Prompts are kept as separate `.txt` files so they can be edited without changing Python code.

## API Endpoint

### `POST /generate-agent`

Same request format as `/generate`:

```json
{
  "text": "Your source text here...",
  "card_type": "multiple_choice",
  "count": 5
}
```

Response includes agent metadata:

```json
{
  "cards": [...],
  "agent_info": {
    "iterations_used": 2,
    "auto_passed": true,
    "eval_passed": true,
    "total_issues": 3
  }
}
```

## Token Cost

Each agent run costs **1 + N LLM calls** where N is the number of iterations:
- 1 initial generation call
- Per iteration: 1 evaluation call + 1 refinement call (if needed)

With `max_iterations=3`, worst case = 7 LLM calls. The `deepseek` provider is recommended — it's cheap (~$0.5/M tokens) and available on both OpenRouter and DeepSeek API.

### Cost optimization features

- **Issue filtering** (`_filter_issues`): Limits reported issues to top N per card, keeping refinement prompts focused and short
- **Source truncation**: Evaluator only receives first 2000 chars of source text
- **Early exit**: Breaks immediately if all checks pass, skipping unnecessary refine calls

## Testing

A test script is available:

```bash
cd llm-service
python test_agent.py
```

This runs:
1. `test_quality_checker()` — Tests deterministic checks with a known-bad card (correct answer too long, short distractors)
2. `test_agent_generation()` — Runs full agent pipeline with ML text sample

## Future Improvements

- **Per-card refinement** instead of regenerating the entire quiz (saves tokens)
- **Difficulty scoring** in evaluator to catch all-easy or all-hard quizzes
- **Factual accuracy check** using source text overlap (RAG-style)
- **Parallel evaluation** for faster iteration
- **Caching**: Skip re-generation if identical source text seen before
- **A/B comparison**: Generate two quizzes in parallel, pick the one with fewer issues