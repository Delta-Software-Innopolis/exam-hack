<script setup lang="ts">
import { ref, watch, useTemplateRef, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import BasicButton from '@/components/basic/BasicButton.vue';
import type { Card, QuizItem } from '@/types';
import { updateCards, createCards, deleteCards } from '@/core'
import { useNewQuizzesStore } from '@/stores/new-quizzes';
import QuizQuestionsList from '@/components/quiz/QuizQuestionsList.vue';
import PlayButton from '@/components/buttons/PlayButton.vue';
import UnknownView from './UnknownView.vue';
import PlusSVG from '@/assets/Plus.svg';
import { addToCollectionFromLink } from '@/core.ts';

const route = useRoute();
const router = useRouter();
const quizCode = route.params.code as string;
const isLoading = ref(true);
const isError = ref(false);

const quizzesStore = useNewQuizzesStore();
const quiz = computed(() => quizzesStore.currentSharedQuiz);

const hasUnsavedChanges = ref(false);
const deletedCards = ref<number[]>([]);
const isSaving = ref(false);
const copied = ref(false);

const activeQuestion = ref<Card>();

const addToCollection = async () => {
    await addToCollectionFromLink(quizCode)
    console.log(quizzesStore.quizzes);
    router.push('/quizzes')
}

onMounted(async () => {
  try {
    await quizzesStore.fetchQuizByInviteId(quizCode);
    console.log("quiz.value:", quiz.value);
  } catch (error) {
    isError.value = true;
    console.error("Error", error);
  } finally {
    isLoading.value = false;
  }
})
</script>

<template>
    <div class="main-container" v-if="!isLoading">
        <div class="left-side">
            <div class="title">
                <h1>{{ quiz?.name || 'Unknown Quiz' }}</h1>
                <span class="author">
                    by <a href="#">{{ quiz?.author.name || 'You'}}</a>
                </span>
            </div>
            <div class="description">
                {{ quiz?.description || "This quiz has no description..." }}
            </div>
            <div class="stats-n-actions">
               <div class="actions">
                    <div class="top-buttons">
                        <PlayButton variant="primary" 
                            @click="router.push(`/quizzes/${quizCode}/solving?shared=true`)"
                        >
                            Attempt
                        </PlayButton>

                        <BasicButton @click="addToCollection">
                            Add to collection
                        </BasicButton>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-side">
            <div class="top-action-bar">
                <h2>Questions</h2>
            </div>
            <QuizQuestionsList :cards="quiz!.cards" variant="view" />
        </div>
    </div>
    <div v-else-if="!isError"></div>
    <UnknownView v-else />
</template>

<style scoped>
.main-container {
    padding: 64px;
    gap: 32px;
    display: flex;
    flex-wrap: wrap;
}

button a {
    text-decoration: none;
    color: inherit;
}

.top-action-bar {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
}

.left-side {
    flex: 0 0 40%;
    min-width: 300px;
    display: flex;
    flex-direction: column;
    gap: 32px;
    height: 100%;
}

.right-side {
    flex: 1 1 50%;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 16px;
    height: 100%;
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
    box-sizing: border-box;
    text-wrap-mode: nowrap;
}

.top-buttons, .bottom-buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
    box-sizing: border-box;
}

.red-button {
    background-color: var(--raddish);
}

.red-button:hover {
    background-color: var(--raddish-dimm);
}
</style>
