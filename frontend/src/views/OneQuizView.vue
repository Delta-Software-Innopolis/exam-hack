<script setup lang="ts">
import type QuizItem from '@/types';
import { ref, onBeforeMount, watch, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useQuizzesStore } from '@/stores/quizzes';
import BasicButton from '@/components/basic/BasicButton.vue';
const route = useRoute()
const router = useRouter()
const quizzesStore = useQuizzesStore()
const quiz = quizzesStore.getQuizById(Number(route.params.quizId) as number)
onBeforeMount(() => {
    console.log(quiz)
    quizzesStore.headerInfo = quiz.value ? quiz.value.name : ""
})
onUnmounted(() => {
    quizzesStore.headerInfo = quizzesStore.headerInfo == quiz.value.name ? "" : quizzesStore.headerInfo
})
</script>

<template>
    <BasicButton :variant="'green'" @click="router.push({name: 'solving', params: {quizId: quiz.id}})">Solve</BasicButton>
    <div>Questions:</div>
    <div class="card-container">
        <div v-for="card in quiz.cards">{{ card.question }}</div>
    </div>
</template>