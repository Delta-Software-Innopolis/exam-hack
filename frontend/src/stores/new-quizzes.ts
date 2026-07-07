import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'

import type { QuizItem } from '@/types'

import { MOCK_QUIZZES, MOCK_PREFIX } from './mock-quizzes'
import useNetworkManager from '@/network'


export const UNKNOWN_QUIZ: QuizItem = {
    id: -1,
    name: '',
    creation_date: new Date(),
    updation_date: new Date(),
    forked_from: 0,
    author: { id: -1, username: '' },
    description: '',
    cards: []
}


export const useNewQuizzesStore = defineStore('newQuizzes', () =>
{
    const quizzes: Ref<QuizItem[]> = ref([])

    function getQuizInfo(quizId: string | string[] | undefined) {
        let quiz = undefined
        if (typeof quizId !== 'string') {
            return undefined
        } else if (quizId.startsWith(MOCK_PREFIX)) {
            let pureQuizId = Number(quizId.slice(MOCK_PREFIX.length-1))
            quiz = MOCK_QUIZZES.at(pureQuizId)
        } else {
            let pureQuizId = Number(quizId)
            for (let q of quizzes.value) {
                if (q.id == pureQuizId) {
                    quiz = q
                    break
                }
            }
        }
        if (quiz === undefined) { quiz = UNKNOWN_QUIZ }
        return quiz
    }

    async function fetchQuizzes(): Promise<void> {
        const nm = useNetworkManager()
        try {
            const response = await nm.fetch_core('/core/packs', { method: 'GET' })
            if (!response.ok) {
                console.error(response)
                throw response.status
            }
            const payload = await response.json()
            if (payload.packs.length < 1) {
                return
            } 
            quizzes.value = payload.packs as QuizItem[]
        } 
        catch (error) {
            console.error('Could not fetch Quizzes', error)
        }
    }

    function getAllQuizzesInfo(): QuizItem[] {
        if (quizzes.value.length == 0) {
            return MOCK_QUIZZES
        }
        return quizzes.value 
    }

    return {
        quizzes,
        fetchQuizzes,
        getQuizInfo,
        getAllQuizzesInfo
    }
},
{
    persist: true
}
)