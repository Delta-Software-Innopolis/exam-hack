<script setup lang="ts">
import type QuizItem from '@/types';
import { ref, onBeforeMount, watch, onUnmounted, useTemplateRef, type Ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useQuizzesStore } from '@/stores/quizzes';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import EditQuestion from '@/components/newBasic/EditQuestion.vue';
import type { Card } from '@/types';
import BasicInput from '@/components/newBasic/BasicInput.vue';
import CrossSVG from '@/assets/Cross.svg'
import CheckSVG from '@/assets/Check.svg'

const route = useRoute()
const router = useRouter()
const quizzesStore = useQuizzesStore()
const quiz = quizzesStore.getQuizById(Number(route.params.quizId) as number)


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

const overlayClass = ref({'hidden-overlay': true})
const showOverlay = ref(false)
const activeQuestion: Ref<Card> = ref(structuredClone(NEW_QUESTION))
const newQuestion = ref(true)
const overlayRef = useTemplateRef('overlay')


function closeOverlay() {
    overlayClass.value['hidden-overlay'] = true;
    setTimeout(()=>{ showOverlay.value = false; }, 180)
}


function onmousedown(event: MouseEvent) {
    if (overlayRef.value && event.target === overlayRef.value) {
        closeOverlay()
    }
}
function openOverlay() {
    overlayClass.value['hidden-overlay'] = false;
    showOverlay.value = true;
}


function onStartAddNewQuestion() {
    if (!newQuestion.value) {
        newQuestion.value = true
        activeQuestion.value = structuredClone(NEW_QUESTION)
    }
    activeQuestion.value.id = quiz.value.cards.length + 1
    openOverlay()
}

function onStartEditQuestion(q_id: number) {
    let q = quiz.value.cards[q_id]
    if (q) {
        newQuestion.value = false
        activeQuestion.value = q 
        openOverlay()
    }
}

function chooseCorrectOption(i: number) {
    activeQuestion.value.correct = [i]
}


onMounted(()=>{
    window.addEventListener('mousedown', onmousedown)
})

onUnmounted(()=>{
    window.removeEventListener('mousedown', onmousedown)
})

function onAddQuestion() {
    quiz.value.cards.push(activeQuestion.value)
    activeQuestion.value = structuredClone(NEW_QUESTION)
    closeOverlay()
}

function onDeleteQuestion() {
    quiz.value.cards.splice(activeQuestion.value.id-1, 1)
    for (const [i, card] of quiz.value.cards.entries()) { card.id = i+1 }  // fix the ids
    activeQuestion.value = structuredClone(NEW_QUESTION)
    closeOverlay()
}


</script>

<template>
    <div class="main-container">
        <div ref="overlay" class="overlay" v-if="showOverlay" :class="overlayClass">

            <div class="question-edit-window" v-if="activeQuestion">
                <div class="top-line">
                    <h3 v-if="newQuestion">Add {{ activeQuestion.id }}th Question</h3>
                    <h3 v-else>Edit Question {{ activeQuestion.id }}</h3>
                    <CrossSVG @click="closeOverlay"/>
                </div>
                <BasicInput v-model="activeQuestion.question"/>
                <h4>Options</h4>
                <div class="options-wrapper">
                    <div class="option-item" v-for="i in activeQuestion.options.length">
                        <input v-model="activeQuestion.options[i-1]"></input>
                        <CheckSVG v-if="activeQuestion.correct[0] == i-1" class="option-check"/>
                        <CrossSVG v-else class="option-cross" @click="chooseCorrectOption(i-1)"/>
                    </div>
                </div>
                <BasicButton v-if="newQuestion" @click="onAddQuestion">Add Question</BasicButton>
                <BasicButton v-else class="red-button" @click="onDeleteQuestion">Delete Question</BasicButton>
            </div>

        </div>
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
                        <BasicButton @click="router.push(`/quizzes/${quiz.id}/solving`)">Attempt</BasicButton>
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
                <BasicButton variant="secondary" @click="onStartAddNewQuestion()">Add</BasicButton>
            </div>
            <div class="questions-wrapper">
                <EditQuestion @click="onStartEditQuestion(q.id-1)" :index="q.id" :question="q.question" v-for="q in quiz.cards"/>
            </div>
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

.question-edit-window {
    background-color: var(--white);
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    border-radius: 16px;
    min-width: 26em;

    --icon-width: 12px;
    --icon-height: 12px;
}

.question-edit-window .top-line {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: top;
}

.question-edit-window input {
    border-radius: 8px !important;
}

.question-edit-window h3, .question-edit-window h4 {
    font-weight: bold;
    font-size: 16px;
}

.question-edit-window h4 {
    color: var(--secondary)
}

.options-wrapper {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 8px;
}

.options-wrapper svg {
    border: 1px solid black;
    border-radius: 4px;
    background-color: var(--white);
    cursor: pointer;
}

.option-item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    background-color: var(--background-blueish);
    padding: 8px;
    border-radius: 8px;
}

.option-item input {
    width: 100%;
    background: none;
    border: none;
    padding-left: 4px;
}

.option-item input:focus{
    outline: none;
    border: none;
}

.option-cross {
    --icon-stroke: var(--raddish);
    padding: 5px;
    --icon-stroke-width: 2.5px;
    --icon-width: 24px;
    --icon-height: 24px;
}

.option-check {
    --icon-stroke: var(--primary);
    padding: 1px;
    --icon-stroke-width: 2.2px;
    --icon-width: 24px;
    --icon-height: 24px;
}

.question-edit-window .top-line svg {
    cursor: pointer;
}

.overlay {
  z-index: 999;
  box-sizing: border-box;
  position: fixed;
  top: 0;
  left: 0;
  padding: 0;
  background-color: rgba(0,0,0,0.25);
  margin: 0;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  opacity: 1;
  animation: appear 0.2s
}

.hidden-overlay {
    animation: disappear 0.2s;
}

@keyframes disappear {
    0% { opacity: 1; }
    100% { opacity: 0;}
}

@keyframes appear {
    0% { opacity: 0; }
    100% { opacity: 1;}
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


.questions-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8px;
    background-color: var(--white);
    padding: 16px;
    border-radius: 16px;
    height: 100%;
    overflow-y: auto;
}

</style>