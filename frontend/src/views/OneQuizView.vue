<script setup lang="ts">
import type QuizItem from '@/types';
import { ref, onBeforeMount, watch, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useQuizzesStore } from '@/stores/quizzes';
import BasicButton from '@/components/basic/BasicButton.vue';
const route = useRoute();
const quizzesStore = useQuizzesStore()
const quiz = quizzesStore.getQuizById(Number(route.params.quizId) as number)
onBeforeMount(() => {
    console.log(quiz)
    quizzesStore.headerInfo = quiz.value ? quiz.value.name : ""
})
function startSolve() {

}
</script>

<template>
    <BasicButton :variant="'green'" @click="">Solve quiz</BasicButton>
    <div>Questions:</div>
    <div class="card-container">
        <div v-for="card in quiz.cards">{{ card.question }}</div>
    </div>
</template>