"""Quick smoke test: runs quality_checker and tries an agent generation with sample text."""
import sys
sys.path.insert(0, ".")
sys.path.insert(0, "llm-service")

from quality_checker import run_all_checks
from agent_generator import generate_cards_agent


def test_quality_checker():
    """Test with a known-bad card (correct_answer way too long)."""
    cards = [
        {
            "type": "multiple_choice",
            "question": "What is the capital of France?",
            "options": [
                "Paris is the capital city",
                "London",
                "Berlin",
                "Madrid",
            ],
            "correct_indices": [0],
            "hint": "City of light",
        },
        {
            "type": "single_answer",
            "question": "What is the largest planet?",
            "correct_answer": "Jupiter is the fifth planet from the Sun and the largest in our solar system with a mass more than 300 times that of Earth",
            "hint": "A gas giant",
        },
    ]

    report = run_all_checks(cards)
    print("=== QUALITY CHECKER TEST ===")
    print(f"  Passed: {report['passed']}")
    print(f"  Total issues: {report['total_issues']}")
    for issue in report["issues"]:
        print(f"  - [{issue['type']}] {issue['detail']}")
    return report


SAMPLE_TEXT = """
Machine learning is a subset of artificial intelligence that enables systems to learn and improve
from experience without being explicitly programmed. It focuses on developing computer programs
that can access data and use it to learn for themselves.

There are three main types of machine learning: supervised learning, unsupervised learning,
and reinforcement learning.

Supervised learning involves training a model on labeled data, where the correct output is known.
The model learns to map inputs to outputs and can then make predictions on new, unseen data.
Common algorithms include linear regression, decision trees, and neural networks.

Unsupervised learning deals with unlabeled data. The model tries to find patterns and structure
in the data on its own. Clustering and association are typical unsupervised learning tasks.
K-means clustering and principal component analysis (PCA) are common algorithms.

Reinforcement learning is about taking actions in an environment to maximize cumulative reward.
The agent learns from the consequences of its actions rather than from explicit instruction.
It is commonly used in game playing, robotics, and navigation.

Feature engineering is the process of selecting and transforming raw data into features that
better represent the underlying problem to the predictive models. Good features make the
difference between a mediocre model and an excellent one.
"""


def test_agent_generation():
    """Run the full agent-based generation pipeline."""
    print("\n=== AGENT GENERATION TEST ===")
    print(f"Source text ({len(SAMPLE_TEXT)} chars): {SAMPLE_TEXT[:80]}...")

    try:
        cards = generate_cards_agent(
            text=SAMPLE_TEXT,
            card_type="multiple_choice",
            count=3,
            max_iterations=2,
        )
        print(f"\nGenerated {len(cards)} cards:")
        for i, card in enumerate(cards):
            print(f"\n--- Card {i + 1} ---")
            print(f"  Question: {card.get('question', '?')[:80]}")
            opts = card.get("options")
            if opts:
                for j, opt in enumerate(opts):
                    marker = "✓" if j in card.get("correct_indices", []) else " "
                    print(f"    [{marker}] {opt[:60]}")
            else:
                print(f"  Answer: {card.get('correct_answer', '?')[:60]}")
        return cards
    except Exception as exc:
        print(f"FAILED: {exc}")
        import traceback
        traceback.print_exc()
        return []


if __name__ == "__main__":
    test_quality_checker()
    test_agent_generation()