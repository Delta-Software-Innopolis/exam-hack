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
        let quiz_id = await __createPack(title, description)
        await __createCards(quiz_id, questions)
        return true
    } catch (err) {
        try {
            let response = await nm.validate_token()  // this refreshes token
            let quiz_id = await __createPack(title, description)
            await __createCards(quiz_id, questions)
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
): Promise<number> {
    const nm = useNetworkManager()
    const tokenStore = useTokenStore()
    try {
        const response = await nm.fetch_core('/core/pack', {
            method: 'POST',
            body: JSON.stringify({ name: title }),
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
