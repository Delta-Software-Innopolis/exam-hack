<script setup lang="ts">
import { ref, watch, onUnmounted, useTemplateRef, type Ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import EditQuestion from '@/components/newBasic/EditQuestion.vue';
import type { Card, CardType, QuizItem, QuizHubItem } from '@/types';
import BasicInput from '@/components/newBasic/BasicInput.vue';
import CrossSVG from '@/assets/Cross.svg'
import CheckSVG from '@/assets/Check.svg'
import { useNewQuizzesStore } from '@/stores/new-quizzes';
import useNetworkManager, { HUB_URL} from '@/network';

const route = useRoute()
const router = useRouter()
const networkManager = useNetworkManager()
const quiz = ref<QuizHubItem|null>(null)

onMounted(async() => {
    quiz.value = await getQuiz()
    console.log(quiz.value)
})

async function getQuiz() {
    const quizId = route.params.quizId
    if (quizId === undefined) {
        console.error('Missing quiz id in route params')
        return
    }

    try {
        const response = await networkManager.unauth_fetch(
            new URL(`/hub/packs/${quizId}`, HUB_URL),
            {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        },
        )

        if (!response.ok) {
            throw new Error(`Failed to get quiz: ${response.status}`)
        }
        const result = await response.json()
        return result
    } catch (error) {
        console.error('Could not add quiz to collection:', error)
    }
}

async function addToCollection() {
    const quizId = route.params.quizId
    if (quizId === undefined) {
        console.error('Missing quiz id in route params')
        return
    }

    try {
        const response = await networkManager.fetch(
            new URL(`/hub/packs/${quizId}`, HUB_URL),
            {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        },
        )

        if (!response.ok) {
            throw new Error(`Failed to add quiz: ${response.status}`)
        }

        console.log('Quiz added to collection', quizId)
        router.push("/quizzes")
    } catch (error) {
        console.error('Could not add quiz to collection:', error)
    }
}

function notImplemented() {
    alert('Thank you for trying!\nThis will be implemented later 🫡')
}

const overlayClass = ref({'hidden-overlay': true})
const showOverlay = ref(false)
const activeQuestionId = ref(-1)
const overlayRef = useTemplateRef('overlay')


function closeOverlay() {
    overlayClass.value['hidden-overlay'] = true;
    setTimeout(()=>{ showOverlay.value = false; }, 180)
    activeQuestionId.value = -1
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


onMounted(()=>{
    window.addEventListener('mousedown', onmousedown)
})

onUnmounted(()=>{
    window.removeEventListener('mousedown', onmousedown)
})
</script>

<template>
    <div class="main-container" v-if="quiz">
        <div ref="overlay" class="overlay" v-if="showOverlay" :class="overlayClass">
        </div>

        <div class="left-side">
            <div class="title">
                <h1>{{ quiz.name || 'Unknown Quiz' }}</h1>
                <span class="author">
                    by <a href="#">{{ quiz.author.name || 'Someone'}}</a>
                </span>
            </div>
            <div class="description">
                {{ quiz?.description || "This quiz has no description..." }}
            </div>
            <div class="stats-n-actions">
                <div class="stats">
                    <ul class="tags-container">
                        <li v-for="(value, key) in quiz">{{key}}: {{ value }}</li>
                    </ul>
                </div>
                <div class="actions">
                    <div class="top-buttons">
                        <BasicButton variant="primary" @click="addToCollection()">Add to collection</BasicButton>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-side">
            <p>There should be information about rating</p>
            <p>Also ability to add the quiz to your own collection</p>
            <p>Also we should show forks of this quiz here</p>
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

.edit-quiz-buttons {
    display: flex;
    gap: 12px;
    align-items: center;
}

</style>
