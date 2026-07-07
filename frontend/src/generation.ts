import useNetworkManager from "./network";
import type { CardType, GenerateCardsResponse } from "./types";

export async function generateCards(
    documents: File[],
    name: string,
    cardType: CardType,
    count = 5,
): Promise<GenerateCardsResponse> {
    const nm = useNetworkManager()

    const formData = new FormData();
    formData.append("name", name);
    documents.forEach((doc) => {
        formData.append("files", doc);
    });
    formData.append("card_type", cardType);
    formData.append("count", count.toString());

    console.log("Form inspection");
    for (const [key, value] of formData.entries()) {
        console.log(key, value);
    }

    const response = await nm.fetch_core("/core/pack/generate", {
        method: "POST",
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

    let output = (await response.json()) as GenerateCardsResponse;
    for (let card of output.cards.values()) {
        delete card.pack_id;
        delete card.pack;
        delete card.id;
        card.correct = [];
        for (let [i, opt] of card.options.entries()) {
            card.options[i] = opt.content;
            if (opt.is_right) {
                card.correct.push(i);
            }
        }
    }
    return output;
}
