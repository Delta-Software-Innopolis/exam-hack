import * as Auth from '@/auth'
import type QuizItem from './types';
import { useUserStore } from './stores/user';


const CORE_URL = import.meta.env.DEV ? "http://localhost:8001": import.meta.env.VITE_CORE_URL_DEV 


export async function fetchPacks(): Promise<QuizItem[]> {
    const userStore = useUserStore()
    await fetch(`${CORE_URL}/core/packs/`, {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${userStore.getAccessToken()}` },
        body: JSON.stringify({
            
        })
    })
}
