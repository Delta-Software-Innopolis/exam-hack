import { useUserStore } from "./stores/user";
import type { Card } from "./types";

type CardType = "multiple_choice" | "single_answer";

interface GenerateCardsResponse {
  cards: any[];
}

export async function generateCards(
  document: File,
  name: string,
  cardType: CardType,
  count = 5
): Promise<GenerateCardsResponse> {
  const userStore = useUserStore();

  const formData = new FormData();
  formData.append("name", name);
  formData.append("files", document);
  formData.append("card_type", cardType);
  formData.append("count", count.toString());

  console.log("Form inspection")
  for (const [key, value] of formData.entries()) {
    console.log(key, value);
  }

  const baseUrl = import.meta.env.DEV ? "http://localhost:8001": import.meta.env.VITE_CORE_URL_DEV 

  const response = await fetch(`${baseUrl}/core/pack/generate`, {
    method: "POST",
    headers: { 'Authorization': `Bearer ${userStore.getAccessToken()}` },
    body: formData,
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;

    try {
      const error = await response.json();
      message = error.error ?? error.details ?? message;
    } catch {
      // Ignore if the response isn't JSON
    }

    throw new Error(message);
  }

  let output = await response.json() as GenerateCardsResponse
  for (let card of output.cards.values()) {
    delete card.pack_id
    delete card.pack
    delete card.id
    card.correct = []
    for (let [i, opt] of card.options.entries()) {
        card.options[i] = opt.content
        if (opt.is_right) {
            card.correct.push(i)
        }
    }
  }
  return output;
}
