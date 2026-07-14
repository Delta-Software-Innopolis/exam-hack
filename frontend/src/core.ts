import type { Card } from "@/types";
import { useTokenStore } from "./stores/token";
import useNetworkManager from "./network";




// TODO: REWRITE THIS WITH ATOMIC FETCH TO POST /core/pack

export async function fetchCreateQuiz(
    title: string,
    description: string,
    questions: Card[]
): Promise<boolean> {
    const nm = useNetworkManager()
    try {
        let quiz_id = await __createPack(title, description, questions);
        return true
    } catch (err) {
        try {
            let response = await nm.validate_token()  // this refreshes token
            let quiz_id = await __createPack(title, description, questions);
            return true
        } catch (err) {
            console.log(err)
            return false
        }
    }
}


/** Returns new pack id */ 
async function __createPack(
    title: string,
    description: string,
    cards: Card[]
): Promise<number> {
    const nm = useNetworkManager()
    const tokenStore = useTokenStore()
    try {
        const response = await nm.fetch_core('/core/pack', {
            method: 'POST',
            body: JSON.stringify({
                name: title,
                description: description,
                cards: cards
            }),
        })
        if (!response.ok) {
            console.error(response)
            throw new Error('something wrong with __createPack')
        }
        let body = await response.json()
        return body.id
    } catch (err) {
        console.error(err)
        throw new Error('something wrong with __createPack')
    }
}


async function __createCards(quiz_id: number, questions: Card[]): Promise<void> {
    const nm = useNetworkManager()
    const tokenStore = useTokenStore()
    try {
        const response = await nm.fetch_core(`/core/cards/${quiz_id}`, {
            method: 'POST',
            body: JSON.stringify({ 'cards': questions })
        })
        if (!response.ok) {
            console.error(response)
            throw new Error('something wrong with __createCards')
        }
    } catch (err) {
        console.error(err)
        throw new Error('something wrong with __createCards')
    }
}

export async function updateCards(cards: Card[]): Promise<boolean> {
    const nm = useNetworkManager()
    try {
        const response = await nm.fetch_core('/core/cards', {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                cards
            })
        })

        return response.ok
    } catch (err) {
        console.error(err)
        return false
    }
}

export async function createCards(packId: number, cards: Card[]): Promise<boolean> {
    const nm = useNetworkManager()

    try {
        const response = await nm.fetch_core(`/core/cards/${packId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                cards: cards.map(card => ({
                    question: card.question,
                    hint: card.hint,
                    options: card.options,
                    correct: card.correct
                }))
            })
        })

        return response.ok
    } catch (err) {
        console.error(err)
        return false
    }
}

export async function deleteCards(cardIds: number[]): Promise<boolean> {
    const nm = useNetworkManager()
    try {
        const response = await nm.fetch_core('/core/cards', {
            method: "DELETE",
            body: JSON.stringify({
                cards: cardIds
            })
        })

        return response.ok
    } catch (err) {
        console.error(err)
        return false
    }
}

export async function addToCollectionFromLink(code: string): Promise<boolean> {
    const nm = useNetworkManager()
    try {
        const response = await nm.fetch_core(`/core/pack/fork/${code}`, {
            method: "POST",
        })

        return response.ok
    } catch (err) {
        console.error(err)
        return false
    }
}

export async function deletePack(packId: number): Promise<boolean> {
    const nm = useNetworkManager()

    try {
        const response = await nm.fetch_core(`/core/pack/${packId}`, {
            method: "DELETE"
        })

        return response.ok
    } catch (err) {
        console.error(err)
        return false
    }
}
