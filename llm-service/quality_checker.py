"""
Automated quality checks for quiz cards.
All checks are deterministic Python code - no LLM calls.
Focuses on metrics LLMs are bad at: lengths, patterns, structural issues.
"""

import statistics
from typing import Any, Dict, List, Optional


def _get_correct_answer_text(card: Dict[str, Any]) -> Optional[str]:
    """Extract the correct answer text from a card."""
    if card.get("options") and card.get("correct_indices") is not None:
        indices = card["correct_indices"]
        if indices:
            return card["options"][indices[0]]
    return card.get("correct_answer")


def check_length_balance(card: Dict[str, Any]) -> Optional[Dict[str, Any]]:
    """
    Check if the correct answer length significantly differs from distractors.
    Returns an issue dict if imbalance found, None if okay.
    """
    if not card.get("options") or not card.get("correct_indices"):
        return None

    options = card["options"]
    correct_indices = set(card["correct_indices"])

    lengths = [len(opt) for opt in options]
    correct_lengths = [lengths[i] for i in correct_indices]
    distractor_lengths = [lengths[i] for i in range(len(options)) if i not in correct_indices]

    if not distractor_lengths:
        return None

    avg_correct = sum(correct_lengths) / len(correct_lengths)
    avg_distractor = sum(distractor_lengths) / len(distractor_lengths)

    # Correct answer significantly longer or shorter than distractors?
    if avg_distractor > 0:
        ratio = avg_correct / avg_distractor
        # Flag if > 60% longer or > 40% shorter
        if ratio > 1.6 or ratio < 0.6:
            question_preview = card["question"][:60]
            return {
                "question": card["question"],
                "question_preview": question_preview,
                "type": "length_imbalance",
                "detail": (
                    f"Correct answer avg length ({avg_correct:.0f} chars) "
                    f"vs distractors avg ({avg_distractor:.0f} chars). "
                    f"Ratio: {ratio:.2f}"
                ),
                "ratio": round(ratio, 2),
            }

    return None


def check_pattern_always_longest(quiz: List[Dict[str, Any]]) -> Optional[Dict[str, Any]]:
    """
    Check if the correct answer is ALWAYS the longest option across all cards.
    """
    always_longest = True
    always_shortest = True

    for i, card in enumerate(quiz):
        if not card.get("options") or not card.get("correct_indices"):
            always_longest = False
            always_shortest = False
            continue

        options = card["options"]
        correct_indices = set(card["correct_indices"])
        lengths = [len(opt) for opt in options]

        max_len = max(lengths)
        min_len = min(lengths)

        # Check if any correct answer is not the longest (or tied for longest)
        correct_is_longest = any(lengths[idx] == max_len for idx in correct_indices)
        correct_is_shortest = any(lengths[idx] == min_len for idx in correct_indices)

        if not correct_is_longest:
            always_longest = False
        if not correct_is_shortest:
            always_shortest = False

    if always_longest and len(quiz) > 1:
        return {
            "type": "pattern_always_longest",
            "detail": "Correct answer is always the longest option across all questions - pattern detected",
        }

    if always_shortest and len(quiz) > 1:
        return {
            "type": "pattern_always_shortest",
            "detail": "Correct answer is always the shortest option across all questions - pattern detected",
        }

    return None


def check_option_similarity(card: Dict[str, Any]) -> Optional[Dict[str, Any]]:
    """
    Check if any two options are suspiciously similar (potential duplicates).
    Uses simple word overlap ratio.
    """
    if not card.get("options"):
        return None

    options = card["options"]

    for i in range(len(options)):
        for j in range(i + 1, len(options)):
            words_i = set(options[i].lower().split())
            words_j = set(options[j].lower().split())

            if not words_i or not words_j:
                continue

            # Check if one is a substring of the other (after stripping)
            stripped_i = options[i].strip().rstrip('.')
            stripped_j = options[j].strip().rstrip('.')

            if stripped_i in stripped_j or stripped_j in stripped_i:
                if len(stripped_i) > 3 and len(stripped_j) > 3:
                    return {
                        "question": card["question"],
                        "question_preview": card["question"][:60],
                        "type": "similar_options",
                        "detail": f"Options are very similar: '{options[i][:40]}' vs '{options[j][:40]}'",
                        "similar_pair": (i, j),
                    }

            # Check word overlap
            overlap = words_i & words_j
            min_words = min(len(words_i), len(words_j))
            if min_words > 0 and len(overlap) / min_words > 0.7:
                return {
                    "question": card["question"],
                    "question_preview": card["question"][:60],
                    "type": "similar_options",
                    "detail": f"High word overlap between options {i} and {j}: {overlap}",
                    "similar_pair": (i, j),
                }

    return None


def check_grammatical_hints(card: Dict[str, Any]) -> Optional[Dict[str, Any]]:
    """
    Check for grammatical hints that could reveal the correct answer.
    e.g., question uses "an" before a blank, only one option starts with a vowel.
    """
    if not card.get("options") or not card.get("correct_indices"):
        return None

    question = card["question"]
    options = card["options"]
    correct_indices = set(card["correct_indices"])

    # Check for "an" at the end of question - hints at vowel-starting answer
    words = question.lower().split()
    if words and words[-1] == "an":
        vowel_starts = [i for i, opt in enumerate(options) if opt.lower().startswith(('a', 'e', 'i', 'o', 'u'))]
        correct_vowel_starts = [i for i in vowel_starts if i in correct_indices]
        non_correct_vowel_starts = [i for i in vowel_starts if i not in correct_indices]

        if vowel_starts and not non_correct_vowel_starts and correct_vowel_starts:
            return {
                "question": card["question"],
                "question_preview": card["question"][:60],
                "type": "grammatical_hint",
                "detail": "Question ends with 'an' but only the correct answer starts with a vowel",
            }

    return None


def check_single_word_distractors(card: Dict[str, Any]) -> Optional[Dict[str, Any]]:
    """
    Check if distractors are suspiciously short compared to the correct answer.
    Sometimes LLMs make wrong answers like "42" or "None" while correct is a full sentence.
    """
    if not card.get("options") or not card.get("correct_indices"):
        return None

    options = card["options"]
    correct_indices = set(card["correct_indices"])

    for i, opt in enumerate(options):
        word_count = len(opt.split())
        if word_count <= 2 and i not in correct_indices:
            # Check if correct answer is much more detailed
            for j in correct_indices:
                correct_words = len(options[j].split())
                if correct_words >= word_count * 3 and correct_words > 3:
                    return {
                        "question": card["question"],
                        "question_preview": card["question"][:60],
                        "type": "short_distractor",
                        "detail": f"Distractor '{opt[:40]}' ({word_count} words) much shorter than correct answer ({correct_words} words)",
                    }

    return None


def run_all_checks(quiz: List[Dict[str, Any]]) -> Dict[str, Any]:
    """
    Run all automated checks on a quiz.
    Returns a comprehensive report.
    """
    all_issues = []
    per_card_issues = []

    for i, card in enumerate(quiz):
        card_issues = []

        # Per-card checks
        for check_fn in [check_length_balance, check_option_similarity, check_grammatical_hints, check_single_word_distractors]:
            issue = check_fn(card)
            if issue:
                card_issues.append(issue)
                all_issues.append(issue)

        per_card_issues.append({"card_index": i, "issues": card_issues})

    # Whole-quiz checks
    pattern_issue = check_pattern_always_longest(quiz)
    if pattern_issue:
        all_issues.append(pattern_issue)

    # Compute stats
    stats = _compute_stats(quiz)

    return {
        "passed": len(all_issues) == 0,
        "total_issues": len(all_issues),
        "issues": all_issues,
        "per_card": per_card_issues,
        "stats": stats,
    }


def _compute_stats(quiz: List[Dict[str, Any]]) -> Dict[str, Any]:
    """Compute aggregate statistics about the quiz for logging."""
    lens = []

    for card in quiz:
        if card.get("options"):
            for opt in card["options"]:
                lens.append(len(opt))

    if not lens:
        return {}

    return {
        "total_cards": len(quiz),
        "option_lengths": {
            "mean": round(statistics.mean(lens), 1) if lens else 0,
            "stdev": round(statistics.stdev(lens), 1) if len(lens) > 1 else 0,
            "min": min(lens),
            "max": max(lens),
        },
    }