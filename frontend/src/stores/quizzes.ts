import { computed, ref, type ComputedRef } from 'vue'
import { defineStore } from 'pinia'
import type QuizItem from '@/types'
import { useUserStore } from './user'
import MOCK_QUIZZES from './mock-quizzes'


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


export const useQuizzesStore = defineStore('quizzes', () => {
  const quizzes = ref(MOCK_QUIZZES)
  const headerInfo = ref("")

  async function fetchQuizzes() {
    const userStore = useUserStore()
    try {
      const token = userStore.getAccessToken()
      const address = import.meta.env.DEV ? "http://localhost:8001": import.meta.env.VITE_CORE_URL_DEV 
      console.log(address)
      console.log(import.meta.env.DEV)
      const response = await fetch(`${address}/core/packs`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (!response.ok) {
        throw new Error(`Ошибка сети: ${response.status}`)
      }

      const data = await response.json()
      if (data.packs.length < 1) {
        return
      } 
      quizzes.value = data.packs
    } catch (error) {
      console.error('Не удалось загрузить квизы:', error)
    }
  }


  function getQuizById(id: number): ComputedRef<QuizItem> {
    return computed(() => {
      const result = quizzes.value.find((quiz) => quiz.id === id)
      return result ? result : UNKNOWN_QUIZ
    })
  }

  function setQuizzes(nextQuizes: QuizItem[]) {
    quizzes.value = nextQuizes
  }

  function getCardQuestionById(quizId: number, cardId: number) {
    return computed(() => {
      const quiz = quizzes.value.find((item) => item.id === quizId)

      return quiz?.cards.find((card) => card.id === cardId)?.question
    })
  }

  function setCardQuestion(quizId: number, cardId: number, question: string) {
    const quiz = quizzes.value.find((item) => item.id === quizId)

    if (!quiz) {
      return
    }

    const card = quiz.cards.find((item) => item.id === cardId)

    if (card) {
      card.question = question
    }
  }

  return {
    quizzes,
    getQuizById,
    setQuizzes,
    getCardQuestionById,
    setCardQuestion,
    headerInfo,
    fetchQuizzes
  }
},
  // {
  //   persist: true,
  // }
)
