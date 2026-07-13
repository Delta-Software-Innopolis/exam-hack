<script setup lang="ts">
import { ref, watch, useTemplateRef, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import BasicButton from '@/components/basic/BasicButton.vue';
import { updateCards, createCards, deleteCards, deletePack } from '@/core'
import type { Card, QuizItem } from '@/types';
import { useNewQuizzesStore } from '@/stores/new-quizzes';
import QuizQuestionsList from '@/components/quiz/QuizQuestionsList.vue';
import ModalQuestionEdit from '@/components/modals/ModalQuestionEdit.vue';
import TrashButton from '@/components/buttons/TrashButton.vue';
import PlayButton from '@/components/buttons/PlayButton.vue';
import UnknownView from './UnknownView.vue';
import ModalQuestionAdd from '@/components/modals/ModalQuestionAdd.vue';
import PlusButton from '@/components/buttons/PlusButton.vue';

const route = useRoute();
const router = useRouter();
const quizzesStore = useNewQuizzesStore();
const quiz = ref<QuizItem>(quizzesStore.getMyQuizInfo(route.params.quizId));
const knownQuiz = computed(()=>quiz.value.id !== -1);

const hasUnsavedChanges = ref(false)
const deletedCards = ref<number[]>([])
const isSaving = ref(false)

const activeQuestion = ref<Card>();


function notImplemented() {
    alert('Thank you for trying!\nThis will be implemented later 🫡')
}

async function onDeleteQuiz() {
    if (!quiz.value) return

    const confirmed = confirm(
        `Delete quiz "${quiz.value.name}"?\n\nThis action cannot be undone.`
    )

    if (!confirmed) return

    const ok = await deletePack(quiz.value.id)

    if (!ok) {
        alert("Couldn't delete quiz")
        return
    }

    await quizzesStore.fetchMyQuizzes()

    router.push("/quizzes")
}

const modalEdit = useTemplateRef('modal-edit');
const modalAdd = useTemplateRef('modal-add');


function onStartEditQuestion(q_idx: number) {
    let q = quiz?.value?.cards.at(q_idx);
    modalEdit.value?.open(q);
}


function onDeleteQuestion(q: Card) {
    if (quiz.value === undefined) { return; }
    const cardIdx = quiz.value.cards.indexOf(q);
    if (cardIdx === -1) { return; }
    const card = quiz.value.cards.at(cardIdx);
    if (card === undefined) { return; }

    if (card.id > 0) {
        deletedCards.value.push(card.id)
    }

    activeQuestion.value = undefined;
    quiz.value.cards.splice(cardIdx, 1)
    hasUnsavedChanges.value = true
    modalEdit?.value?.close();
}


function onAddQuestion(q: Card) {
    if (quiz.value === undefined) { return; }
    quiz.value.cards.push(q);
    hasUnsavedChanges.value = true;
    modalAdd?.value?.close();
}

watch(
    () => quiz.value,
    () => {
        if (!isSaving.value) {
            hasUnsavedChanges.value = true
        }
    },
    { deep: true }
)

async function submitChanges() {
    if (quiz.value === undefined) { return }
    isSaving.value = true

    const updatedCards = quiz.value.cards.filter((card: Card) => card.id > 0)
    const newCards = quiz.value.cards.filter((card: Card) => card.id < 0)

    let ok = true

    if (updatedCards.length > 0)
        ok &&= await updateCards(updatedCards)

    if (newCards.length > 0)
        ok &&= await createCards(quiz.value.id, newCards)

    if (deletedCards.value.length > 0)
        ok &&= await deleteCards(deletedCards.value)

    if (!ok) {
        isSaving.value = false
        alert("Couldn't save changes")
        return
    }

    deletedCards.value = []
    hasUnsavedChanges.value = false

    await quizzesStore.fetchMyQuizzes()
    isSaving.value = false
}
</script>

<template>
    <div class="main-container" v-if="knownQuiz">
        <ModalQuestionEdit ref="modal-edit" @click-delete="onDeleteQuestion"/>
        <ModalQuestionAdd ref="modal-add" @click-add="onAddQuestion" />
        <div class="left-side">
            <div class="title">
                <h1>{{ quiz.name || 'Unknown Quiz' }}</h1>
                <span class="author">
                    by <a href="#">{{ quiz.author.name || 'You'}}</a>
                </span>
            </div>
            <div class="description">
                {{ quiz.description || "This quiz has no description..." }}
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
                        <PlayButton v-if="knownQuiz" variant="primary" 
                            @click="router.push(`/quizzes/${route.params.quizId}/solving`)"
                        >
                            Attempt
                        </PlayButton>
                    </div>
                    <div class="bottom-buttons">
                        <TrashButton v-if="knownQuiz" variant="red" 
                            @click="onDeleteQuiz"
                        >
                            Delete
                        </TrashButton>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-side">
            <div class="top-action-bar">
                <h2>Questions</h2>
                <div class="edit-quiz-buttons">
                    <PlusButton variant="secondary" @click="modalAdd?.open()">Add</PlusButton>
                    <BasicButton v-if="hasUnsavedChanges" variant="primary" @click="submitChanges">Submit changes</BasicButton>
                </div>
            </div>
            <QuizQuestionsList :cards="quiz.cards" variant="edit" 
                @click-question-item="onStartEditQuestion"
            />
        </div>
    </div>
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

.edit-quiz-buttons {
    display: flex;
    gap: 12px;
    align-items: center;
}

</style>
