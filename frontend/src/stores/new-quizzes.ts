import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'

import type { QuizItem } from '@/types'

import { MOCK_QUIZZES, MOCK_PREFIX } from './mock-quizzes'
import useNetworkManager from '@/network'
import type { Q } from 'vue-router/dist/index-BQLwgiyK.js'


export const UNKNOWN_QUIZ: QuizItem = {
    id: -1,
    name: '',
    creation_date: new Date(),
    updation_date: new Date(),
    forked_from: 0,
    author: { id: -1, name: '' },
    description: '',
    cards: []
}


export const useNewQuizzesStore = defineStore('newQuizzes', () =>
{
    const quizzes: Ref<QuizItem[]> = ref([])
    const hubQuizzes: Ref<QuizItem[]> = ref([])
    const currentSharedQuiz: Ref<QuizItem | undefined> = ref(undefined)

    function getHubQuizInfo(quizId: string | string[] | undefined) {
        console.log(quizId)
        let quiz = undefined
        if (typeof quizId === 'number') {
            console.log(quizId)
            for (let q of hubQuizzes.value) {
                if (q.id == quizId) {
                    quiz = q
                    break
                }
            }
            return quiz
        }
        else if (typeof quizId !== 'string') {
            console.log('not string')
            return undefined
        } else if (quizId.startsWith(MOCK_PREFIX)) {
            console.log('mock prefix')
            return undefined
        } else {
            let pureQuizId = Number(quizId)
            console.log(pureQuizId)
            for (let q of hubQuizzes.value) {
                if (q.id == pureQuizId) {
                    quiz = q
                    break
                }
            }
        }
        if (quiz === undefined) { quiz = UNKNOWN_QUIZ }
        return quiz
    }

    function getMyQuizInfo(quizId: string | string[] | undefined) {
        let quiz = undefined
        if (typeof quizId !== 'string') {
            quiz = undefined;
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

    async function fetchMyQuizzes(): Promise<void> {
        const nm = useNetworkManager()
        try {
            const response = await nm.fetch_core('/core/packs', { method: 'GET' })
            if (!response.ok) {
                console.error(response)
                throw response.status
            }
            const payload = await response.json()
            quizzes.value = payload.packs as QuizItem[]
            console.log("PAYLOAD:", payload)
            console.log("QUIZ:", quizzes.value)
        } 
        catch (error) {
            console.error('Could not fetch Quizzes', error)
        }
    }

    async function fetchQuizByInviteId(id: string): Promise<void> {
        const nm = useNetworkManager()
        try {
            console.log(`PATH - /core/share/${id}`)
            const response = await nm.fetch_core(`/core/share/${id}`, { method: 'GET' })
            console.log(response)
            if (!response.ok) {
                console.error(response)
                throw response.status
            }
            const payload = await response.json() as QuizItem
            console.log("PAYLOAD:", payload)
            currentSharedQuiz.value = payload
        } 
        catch (error) {
            console.error('Could not fetch Quizzes', error)
        }
    }

    function getAllMyQuizzesInfo(): QuizItem[] {
        if (quizzes.value.length == 0) {
            return MOCK_QUIZZES
        }
        return quizzes.value 
    }

    return {
        quizzes,
        fetchMyQuizzes,
        getMyQuizInfo,
        getAllMyQuizzesInfo,
        hubQuizzes,
        getHubQuizInfo,
        fetchQuizByInviteId,
        currentSharedQuiz
    }
},
{
    persist: true
}
)
