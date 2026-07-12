<script setup lang="ts">
import { ref, watch, onUnmounted, useTemplateRef, type Ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import BasicButton from '@/components/basic/BasicButton.vue';
import EditQuestion from '@/components/basic/EditQuestion.vue';
import type { Card, CardType } from '@/types';
import BasicInput from '@/components/basic/BasicInput.vue';
import TrashSVG from '@/assets/Trash.svg';
import PlaySVG from '@/assets/Play.svg';
import { updateCards, createCards, deleteCards } from '@/core'
import { useNewQuizzesStore } from '@/stores/new-quizzes';
import QuizQuestionsList from '@/components/quiz-info/QuizQuestionsList.vue';
import ModalQuestionEdit from '@/components/quiz-info/ModalQuestionEdit.vue';
import TrashButton from '@/components/buttons/TrashButton.vue';
import PlayButton from '@/components/buttons/PlayButton.vue';

const route = useRoute()
const router = useRouter()
const quizzesStore = useNewQuizzesStore()
const quiz = ref(quizzesStore.getMyQuizInfo(route.params.quizId))

const hasUnsavedChanges = ref(false)
const deletedCards = ref<number[]>([])
const isSaving = ref(false)

const activeQuestion = ref<Card>();


function notImplemented() {
    alert('Thank you for trying!\nThis will be implemented later 🫡')
}

const NEW_QUESTION = { 
    id: -1,
    question: 'new question',
    options: [
        'opt 1',
        'opt 2',
        'opt 3',
        'opt 4',
    ],
    correct: [1],
    hint: 'some hint here',
    explanation: 'some explanation here',
}

const modalEdit = useTemplateRef('modal-edit')



function onStartEditQuestion(q_idx: number) {
    let q = quiz?.value?.cards.at(q_idx);
    modalEdit.value?.open(q);
}


// function onStartAddNewQuestion() {
//     if (!newQuestion.value) {
//         newQuestion.value = true
//         activeQuestion.value = structuredClone(NEW_QUESTION)
//     }
//     activeQuestionId.value = quiz.value ? quiz.value.cards.length + 1 : -1
// }


// function onAddQuestion() {
//     if (quiz.value)
//         quiz.value.cards.push(activeQuestion.value)
//     activeQuestion.value = structuredClone(NEW_QUESTION)
//     hasUnsavedChanges.value = true
// }


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
    // for (const [i, card] of quiz.value.cards.entries()) { card.id = i+1 }  // fix the ids
    hasUnsavedChanges.value = true
    modalEdit?.value?.close();
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
    <ModalQuestionEdit ref="modal-edit" @click-delete="onDeleteQuestion"/>

    <div class="main-container" v-if="quiz">
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
                        <PlayButton variant="primary" @click="router.push(`/quizzes/${route.params.quizId}/solving`)">
                            Attempt
                        </PlayButton>
                    </div>
                    <div class="bottom-buttons">
                        <TrashButton variant="red" @click="notImplemented"> Delete </TrashButton>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-side">
            <div class="top-action-bar">
                <h2>Questions</h2>
                <div class="edit-quiz-buttons">
                    <!-- <BasicButton variant="secondary" @click="onStartAddNewQuestion()">Add</BasicButton> -->
                    <BasicButton v-if="hasUnsavedChanges" variant="primary" @click="submitChanges">Submit changes</BasicButton>
                </div>
            </div>
            <QuizQuestionsList :cards="quiz.cards" variant="edit" 
                @click-question-item="onStartEditQuestion"
            />
        </div>
    </div>
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
