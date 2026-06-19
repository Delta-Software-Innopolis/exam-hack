<script setup lang="ts">
import type QuizItem from "@/types"
import { ref } from "vue";
import QuizComponent from "@/components/QuizComponent.vue";
const quizes = ref<QuizItem[]>([
    {
        id: 1,
        name: "name1",
        creation_date: new Date(),
        updation_date: new Date(),
        forked_from: 2,
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {        
        id: 2,
        name: "name2",
        creation_date: new Date(),
        updation_date: new Date(),
        forked_from: 3,
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 3,
        name: "name3",
        creation_date: new Date(),
        updation_date: new Date(),
        description: "some description",
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 4,
        name: "name4",
        creation_date: new Date(),
        updation_date: new Date(),
        description: "Some long description bla bla bla some text that can be usless but maybe notalkdjfla, idk, actually",
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 5,
        name: "name5",
        creation_date: new Date(),
        updation_date: new Date(),
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 6,
        name: "name6",
        creation_date: new Date(),
        updation_date: new Date(),
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 7,
        name: "name7",
        creation_date: new Date(),
        updation_date: new Date(),
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
    {
        id: 8,
        name: "name8",
                creation_date: new Date(),
        updation_date: new Date(),
        cards: [{
          question: "How old is me?",
          options: ["option1", "option2", "option3", "option4"],
          correct: 2,
          hint: "some hint",
          explanation: "ww",
        }]
    },
        ])
</script>

<template>
  <div class="main-container">
    <h1>Your Quizzes</h1>
    <div class="Quiz-Container">
      <QuizComponent v-for="quiz in quizes" 
        :key="quiz.id" 
        :name="quiz.name"
        :author="quiz.author"
        :description="quiz.description">
      </QuizComponent>
    </div>
  </div>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  gap: 64px
}
.main-container h1 {
  align-self: center;
  font-weight: bold;
  font-size: 40px;
  margin: 10px;
}
.Quiz-Container {
    display: flex;
    flex-wrap: wrap;
    gap: 2rem 2.5rem;
    justify-content: center;
}

.Quiz-Container > * {
    flex: 0 0 calc((100% - 9rem) / 4);
    box-sizing: border-box;
}


</style>