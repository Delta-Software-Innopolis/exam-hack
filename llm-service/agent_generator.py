"""
Agent-based quiz generator with automated quality evaluation and refinement.

Architecture:
  1. GENERATE: LLM produces initial set of cards from source text
  2. CHECK:  quality_checker.py runs automated deterministic checks
  3. EVALUATE: Second LLM call assesses subjective quality (plausibility, clarity, etc.)
  4. REFINE: If issues found, LLM refines cards to fix them
  5. REPEAT: Loop until quality threshold met or max_iterations reached

This hybrid approach uses deterministic code for what code is good at
(length balance, pattern detection) and LLM calls for what LLMs are good at
(text quality, plausibility, refinement).
"""

import os
from pathlib import Path
from typing import Any, Dict, List, Optional

from openai import OpenAI, APIError

from generator import (
    CardGenerationError,
    get_provider_config,
    card_type_description,
    card_type_examples,
)
from quality_checker import run_all_checks

# Load env
from dotenv import load_dotenv
load_dotenv(dotenv_path=Path(__file__).resolve().parent.parent / ".env")


PROMPTS_DIR = Path(__file__).resolve().parent / "prompts"

MAX_ITERATIONS = 3  # max generation -> refine cycles


def _get_client() -> OpenAI:
    """Get an OpenAI-compatible client configured for the current LLM provider."""
    config = get_provider_config()
    return OpenAI(
        base_url=config["base_url"],
        api_key=config["api_key"],
    )


def _load_prompt(name: str) -> str:
    """Load a prompt template from the prompts/ directory."""
    path = PROMPTS_DIR / name
    if not path.exists():
        raise FileNotFoundError(f"Prompt file not found: {path}")
    return path.read_text(encoding="utf-8")


def _format_generator_prompt(text: str, card_type: str, count: int) -> str:
    """Format the generator system prompt with type-specific instructions."""
    system_prompt = _load_prompt("generator_system.txt")
    desc = card_type_description(card_type)
    examples = card_type_examples(card_type)

    return system_prompt.format(
        count=count,
        card_type_description=desc,
        type_specific_instructions=examples,
    )


def _call_llm(
    client: OpenAI,
    system_prompt: str,
    user_message: str,
    model: Optional[str] = None,
    temperature: float = 0.3,
    max_tokens: int = 4096,
) -> str:
    """Make an LLM call with a system prompt and user message."""
    if model is None:
        model = os.getenv("LLM_MODEL", "deepseek-v4-flash")

    response = client.chat.completions.create(
        model=model,
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_message},
        ],
        temperature=temperature,
        max_tokens=max_tokens,
    )

    content = response.choices[0].message.content
    if not content:
        raise CardGenerationError("LLM returned empty response")
    return content


def _parse_json_cards(content: str) -> List[Dict[str, Any]]:
    """Parse LLM JSON output into a list of card dicts. Handles code fences."""
    import json
    import re

    # Try to extract JSON from code fences first
    json_match = re.search(r"```(?:json)?\s*\n?(.*?)\n?```", content, re.DOTALL)
    if json_match:
        content = json_match.group(1).strip()

    # Parse as JSON
    try:
        data = json.loads(content)
    except json.JSONDecodeError as exc:
        raise CardGenerationError(f"Failed to parse LLM output as JSON: {exc}") from exc

    # Normalize: expect either a list or {"items": [...]}
    if isinstance(data, list):
        cards = data
    elif isinstance(data, dict) and "items" in data:
        cards = data["items"]
    else:
        raise CardGenerationError(f"Unexpected JSON structure: expected list or {{'items': [...]}}, got {type(data)}")

    if not cards:
        raise CardGenerationError("LLM returned empty cards list")

    return cards


def _filter_issues(report: Dict[str, Any], max_card_issues: int = 3) -> Dict[str, Any]:
    """
    Limit the number of issues reported per card to avoid overwhelming the refiner.
    This keeps the LLM refinement focused and saves tokens.
    """
    filtered = dict(report)
    filtered["total_issues"] = len(report.get("issues", []))

    # Limit per-card issues
    filtered["per_card"] = []
    for pc in report.get("per_card", []):
        filtered["per_card"].append({
            "card_index": pc["card_index"],
            "issues": pc["issues"][:max_card_issues],
        })

    # Also limit the flat issues list (show first N issues)
    card_count = len(report.get("per_card", []))
    filtered["issues"] = report.get("issues", [])[:max_card_issues * card_count]

    return filtered


def generate_cards_agent(
    text: str,
    card_type: str,
    count: int,
    max_iterations: int = MAX_ITERATIONS,
) -> List[Dict[str, Any]]:
    """
    Agent-based generation with automated quality checks and LLM refinement.

    Returns a list of card dicts (same format as generator.py's output).
    """
    client = _get_client()
    model = os.getenv("LLM_MODEL", "deepseek-v4-flash")

    # Step 1: Generate initial cards
    # --------------------------------------------------------------------------
    system_prompt = _format_generator_prompt(text, card_type, count)
    user_prompt = f"Generate {count} {card_type} cards from this text:\n\n{text}"

    print(f"[Agent] Step 1: Generating {count} initial {card_type} cards...")
    raw = _call_llm(client, system_prompt, user_prompt, model=model)
    cards = _parse_json_cards(raw)
    print(f"[Agent] Generated {len(cards)} cards")

    # Step 2-5: Check -> Evaluate -> Refine loop
    # --------------------------------------------------------------------------
    for iteration in range(max_iterations):
        print(f"[Agent] Iteration {iteration + 1}/{max_iterations}: Running quality checks...")

        # Step 2: Automated checks
        report = run_all_checks(cards)
        auto_passed = report["passed"]

        # Step 3: LLM-based qualitative evaluation
        eval_prompt = _load_prompt("evaluator_system.txt")
        eval_user = _build_evaluation_input(cards, text)

        print(f"[Agent] Running qualitative evaluation (auto_issues={report['total_issues']})...")
        eval_raw = _call_llm(
            client, eval_prompt, eval_user, model=model,
            temperature=0.1, max_tokens=2048,
        )

        try:
            eval_result = _parse_json_evaluation(eval_raw)
            eval_passed = eval_result.get("passed", False)
        except CardGenerationError:
            print(f"[Agent] Failed to parse evaluation, defaulting to not passed")
            eval_passed = False
            eval_result = {"qualitative_issues": [], "overall_impression": "parse failed"}

        # Decision: pass if both checks clear
        if auto_passed and eval_passed:
            print(f"[Agent] All quality checks passed! Finalizing.")
            break

        if iteration == max_iterations - 1:
            print(f"[Agent] Max iterations reached. Returning best attempt.")
            break

        # Step 4: Refinement
        print(f"[Agent] Quality issues detected, refining...")
        refiner_prompt = _load_prompt("refiner_system.txt")

        filtered_report = _filter_issues(report)
        issues_summary = _build_issues_summary(filtered_report, eval_result)

        refiner_input = (
            f"Original source text:\n{text}\n\n"
            f"Current quiz (needs fixing):\n{_cards_to_json_string(cards)}\n\n"
            f"Issues to fix:\n{issues_summary}\n\n"
            f"Please fix ALL the issues above and return the complete fixed quiz."
        )

        try:
            refined_raw = _call_llm(
                client, refiner_prompt, refiner_input, model=model,
                temperature=0.3, max_tokens=4096,
            )
            new_cards = _parse_json_cards(refined_raw)

            # Validate structure
            if len(new_cards) != len(cards):
                print(f"[Agent] Warning: card count changed ({len(cards)} -> {len(new_cards)}), keeping originals")
            else:
                cards = new_cards
                print(f"[Agent] Refinement applied successfully")

        except (CardGenerationError, APIError) as exc:
            print(f"[Agent] Refinement failed: {exc}")
            continue

    return cards


def _build_evaluation_input(cards: List[Dict[str, Any]], source_text: str) -> str:
    """Build the evaluation user message containing cards to evaluate."""
    import json
    cards_json = json.dumps(cards, indent=2, ensure_ascii=False)
    return (
        f"Evaluate the quality of these quiz cards generated from the source text.\n\n"
        f"Source text (first 2000 chars):\n{source_text[:2000]}\n\n"
        f"Quiz cards:\n{cards_json}"
    )


def _build_issues_summary(
    auto_report: Dict[str, Any],
    eval_result: Dict[str, Any],
) -> str:
    """Build a condensed text summary of all issues for the refiner."""
    parts = []

    if auto_report.get("total_issues", 0) > 0:
        parts.append("=== AUTOMATED CHECKS (numerical issues) ===")
        for issue in auto_report.get("issues", []):
            parts.append(f"- [{issue['type']}] {issue.get('detail', '')}")

    qual_issues = eval_result.get("qualitative_issues", [])
    if qual_issues:
        parts.append("\n=== QUALITATIVE EVALUATION (text quality issues) ===")
        for qi in qual_issues:
            q = qi.get("question", "?")[:50]
            issue = qi.get("issue", "")
            parts.append(f"- Question: '{q}' -> {issue}")

    impression = eval_result.get("overall_impression")
    if impression:
        parts.append(f"\nOverall impression: {impression}")

    return "\n".join(parts) if parts else "No specific issues reported."


def _cards_to_json_string(cards: List[Dict[str, Any]]) -> str:
    """Serialize cards to JSON string for inclusion in prompts."""
    import json
    return json.dumps(cards, indent=2, ensure_ascii=False)


def _parse_json_evaluation(content: str) -> Dict[str, Any]:
    """Parse the JSON evaluation result from the LLM."""
    import json
    import re

    json_match = re.search(r"```(?:json)?\s*\n?(.*?)\n?```", content, re.DOTALL)
    if json_match:
        content = json_match.group(1).strip()

    try:
        data = json.loads(content)
    except json.JSONDecodeError as exc:
        raise CardGenerationError(f"Failed to parse evaluation JSON: {exc}") from exc

    if not isinstance(data, dict):
        raise CardGenerationError(f"Evaluation result is not a dict: {type(data)}")

    return data