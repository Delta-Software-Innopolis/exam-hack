import type { Card } from "@/types";

import * as Auth from '@/auth'
import { useUserStore } from "./stores/user";
import { useRouter } from "vue-router";


const CORE_URL = import.meta.env.DEV ? "http://localhost:8001": import.meta.env.VITE_CORE_URL_DEV 


export async function fetchCreateQuiz(
    title: string,
    description: string,
    questions: Card[]
): Promise<boolean> {
    const userStore = useUserStore()

    try {
        userStore.getAccessToken()
        let quiz_id = await __createPack(title, description)
        await __createCards(quiz_id, questions)
        return true
    } catch (err) {
        try {
            let response = await Auth.refresh(userStore.getRefreshToken())
            userStore.saveAccessToken(response.access_token)
            userStore.saveRefreshToken(response.refresh_token)
            userStore.getAccessToken()
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
    const userStore = useUserStore()
    try {
        const response = await fetch(`${CORE_URL}/core/pack`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${userStore.getAccessToken()}` },
            body: JSON.stringify({ name: title })
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
    const userStore = useUserStore()
    try {
        const response = await fetch(`${CORE_URL}/core/cards/${quiz_id}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${userStore.getAccessToken()}` },
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
