import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type QuizItem from '@/types'
import { useUserStore } from './user'

export const useQuizzesStore = defineStore('quizzes', () => {
  const quizes = ref<QuizItem[]>([
    {
        id: 1,
        name: "name1",
        creation_date: new Date(),
        updation_date: new Date(),
        author: {id: 1, name: "You"},
        forked_from: 2,
        cards: [
        {
          id:1,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        }
      ]
    },
    {        
        id: 2,
        name: "name2",
        creation_date: new Date(),
        updation_date: new Date(),
        author: {id: 1, name: "You"},
        forked_from: 3,
        cards: [{
          id:2,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [3],
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 3,
        name: "name3",
        creation_date: new Date(),
        updation_date: new Date(),
        author: {id: 1, name: "You"},
        description: "some description",
        cards: [{
          id: 3,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        },
        {
          id: 4,
          question: "How old is me?",
          options: ["option22", "option21l", "option38", "option44"],
          correct: [3],
          hint: "some hint",
          explanation: "ww",
        },
        {
          id: 5,
          question: "How old is me?",
          options: ["option22", "option21l", "option38", "option44"],
          correct: [3],
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 4,
        name: "name4",
        creation_date: new Date(),
        author: {id: 1, name: "You"},
        updation_date: new Date(),
        description: "Some long description bla bla bla some text that can be usless but maybe notalkdjfla, idk, actually",
        cards: [{
          id: 4,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 5,
        name: "name5",
        creation_date: new Date(),
        author: {id: 1, name: "You"},
        updation_date: new Date(),
        cards: [{
          id: 5,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 6,
        name: "name6",
        creation_date: new Date(),
        author: {id: 1, name: "You"},
        updation_date: new Date(),
        cards: [{
          id: 6,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 7,
        name: "name7",
        creation_date: new Date(),
        updation_date: new Date(),
        author: {id: 1, name: "You"},
        cards: [{
          id: 7,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 8,
        name: "name8",
        creation_date: new Date(),
        updation_date: new Date(),
        author: {id: 1, name: "You"},
        cards: [{
          id: 8,
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: [2],
          hint: "some hint",
          explanation: "ww",
        }]
    },
        ])
  const headerInfo = ref("")
  async function fetchQuizzes() {
    const userStore = useUserStore()
    try {
      const token = userStore.access_token

      const response = await fetch('http://localhost:8001/core/packs', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (!response.ok) {
        throw new Error(`Ошибка сети: ${response.status}`)
      }

      const data = await response.json()
      
      quizes.value = data.packs
    } catch (error) {
      console.error('Не удалось загрузить квизы:', error)
    }
  }
  function getQuizById(id: number) {
    return computed(() => {
      const result = quizes.value.find((quiz) => quiz.id === id)
      return result ? result :     
      {
        id: -1,
        name: "",
        creation_date: new Date(),
        updation_date: new Date(),
        forked_from: -1,
        cards: [{
          id:-1,
          question: "",
          options: [],
          correct: -1,
          hint: "",
          explanation: "",
        }]
    }
    })
  }

  function setQuizes(nextQuizes: QuizItem[]) {
    quizes.value = nextQuizes
  }

  function getCardQuestionById(quizId: number, cardId: number) {
    return computed(() => {
      const quiz = quizes.value.find((item) => item.id === quizId)

      return quiz?.cards.find((card) => card.id === cardId)?.question
    })
  }

  function setCardQuestion(quizId: number, cardId: number, question: string) {
    const quiz = quizes.value.find((item) => item.id === quizId)

    if (!quiz) {
      return
    }

    const card = quiz.cards.find((item) => item.id === cardId)

    if (card) {
      card.question = question
    }
  }

  return {
    quizes,
    getQuizById,
    setQuizes,
    getCardQuestionById,
    setCardQuestion,
    headerInfo,
    fetchQuizzes
  }
},
  {
    persist: true,
  })
