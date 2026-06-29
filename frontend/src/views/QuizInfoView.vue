<script setup lang="ts">
import type QuizItem from '@/types';
import { ref, onBeforeMount, watch, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useQuizzesStore } from '@/stores/quizzes';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import EditQuestion from '@/components/newBasic/EditQuestion.vue';

const route = useRoute()
const router = useRouter()
const quizzesStore = useQuizzesStore()
const quiz = quizzesStore.getQuizById(Number(route.params.quizId) as number)


function notImplemented() {
    alert('Thank you for trying!\nThis will be implemented later 🫡')
}

</script>

<template>

    <div class="main-container">
        <div class="left-side">
            <div class="title">
                <h1>{{ quiz.name }}</h1>
                <span class="author">
                    by <a href="#">{{ quiz.author.username }}</a>
                </span>
            </div>
            <div class="description">
                {{ quiz.description }}
            </div>
            <div class="stats-n-actions">
                <div class="stats">
                    <div class="commpletion">
                        <h4>Completion</h4>
                        <div class="progress-bar">
                            <span class="percentage">0%</span>
                            <div class="bar"></div>
                        </div>
                    </div>
                    <div class="statistics">
                        <h4>Statistics</h4>
                        <ul>
                            <li>Attempts: 0</li>
                            <li>R/W ratio: 0.5</li>
                            <li>Time practicing: 10m 32s</li>
                        </ul>
                    </div>
                </div>
                <div class="actions">
                    <div class="top-buttons">
                        <BasicButton>Attempt</BasicButton>
                        <BasicButton variant="secondary" @click="notImplemented">To favourites</BasicButton>
                    </div>
                    <div class="bottom-buttons">
                        <BasicButton class="red-button" @click="notImplemented">Delete</BasicButton>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-side">
            <div class="top-action-bar">
                <h2>Questions</h2>
                <BasicButton>Add</BasicButton>
            </div>
            <div class="questions-wrapper">
                <EditQuestion :index="q.id" :question="q.question" v-for="q in quiz.cards"/>
            </div>
        </div>
    </div>
</template>


<style scoped>
.main-container {
    padding: 64px;
    gap: 32px;
    display: flex;
    flex-direction: row;
}

.left-side, .right-side { 
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 32px;
}

.title {
    padding: 0;
    margin: 0;
}

.description {
    padding: 16px;
    border-radius: 16px;
    background-color: var(--white);
}

.stats-n-actions {
    display: flex;
    flex-direction: row;
    height: 100%;
    width: 100%;
    gap: 16px;
}

.stats {
    display: flex;
    flex-direction: column;
    background-color: white;
    padding: 16px;
    gap: 16px;
    border-radius: 16px;
    width: 100%;
}

.statistics li {
    line-height: 1.25;
}

.completion, .statistics {
    display: flex;
    flex-direction: column;
}

.progress-bar {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 8px;
}

.progress-bar .bar {
    width: 100%;
    height: 1em;
    background-color: var(--background-light);
    border-radius: 0.5em;
}

.actions {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    width: fit-content;
}

.top-buttons, .bottom-buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.red-button {
    background-color: var(--raddish);
}
.red-button:hover {
    background-color: var(--raddish-dimm);
}


.questions-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8px;
    background-color: var(--white);
    padding: 16px;
    border-radius: 16px;

}

</style>