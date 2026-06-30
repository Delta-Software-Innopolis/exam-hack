<script setup lang="ts">
import BasicFileUpload from '@/components/newBasic/BasicFileUpload.vue';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import BasicInput from '@/components/newBasic/BasicInput.vue';
import BasicTextArea from '@/components/newBasic/BasicTextArea.vue';
import EditQuestion from '@/components/newBasic/EditQuestion.vue';
import CrossSVG from '@/assets/Cross.svg';
import CheckSVG from '@/assets/Check.svg';

import { onMounted, onUnmounted, ref, useTemplateRef, type Ref } from 'vue';
import type { Card } from '@/types';
import { fetchCreateQuiz } from '@/core';
import { useRouter } from 'vue-router';


const router = useRouter()

const quizTitle = ref('')
const quizDescription = ref('')
const uploadedFiles: Ref<File[]> = ref([])
const questions: Ref<Card[]> = ref([])

const overlayClass = ref({'hidden-overlay': true})
const showOverlay = ref(false)
const allowGenerate = ref(false)

const currentStep = ref(1)


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


const activeQuestion: Ref<Card> = ref(structuredClone(NEW_QUESTION))
const newQuestion = ref(true)

const firstBtnLine = useTemplateRef('disappearing-buttons-line')
const skipBtnClass = ref({ hidden: false, 'not-displayed': false })
const stepGenerate = useTemplateRef('step-2')
const stepEdit = useTemplateRef('step-3')
const overlayRef = useTemplateRef('overlay')


function nextStep() {
    playStepChangeAnimation()
    currentStep.value += 1
}

function playStepChangeAnimation() {
    switch (currentStep.value) {
        case (1): {
            let btnLine: HTMLDivElement | null = null
            if (btnLine = firstBtnLine.value) {
                btnLine.classList.add('hidden')
                if (stepGenerate.value) {
                    stepGenerate.value.classList.remove('not-displayed')
                    stepGenerate.value.classList.remove('hidden')
                }
                setTimeout(()=>{
                    if (btnLine) { btnLine.style.display = 'none' }
                }, 500)
            }
            break
        }
        case (2): {
            skipBtnClass.value.hidden = true
            setTimeout(()=>{skipBtnClass.value['not-displayed'] = true}, 500)
            if (stepEdit.value) {
                stepEdit.value.classList.remove('not-displayed')
                stepEdit.value.classList.remove('hidden')
            }
            break
        }
    }
}


async function generateQuestions() {
    alert("Start generating")
    if (currentStep.value < 3) { nextStep() }
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
    activeQuestion.value.id = questions.value.length + 1
    openOverlay()
}

function onStartEditQuestion(q_id: number) {
    let q = questions.value[q_id]
    if (q) {
        newQuestion.value = false
        activeQuestion.value = q 
        openOverlay()
    }
}


function closeOverlay() {
    overlayClass.value['hidden-overlay'] = true;
    setTimeout(()=>{ showOverlay.value = false; }, 180)
}


function onFilesChanged(files: File[]) {
    uploadedFiles.value = files
    allowGenerate.value = files.length > 0
}


function onmousedown(event: MouseEvent) {
    if (overlayRef.value && event.target === overlayRef.value) {
        closeOverlay()
    }
}


onMounted(()=>{
    window.addEventListener('mousedown', onmousedown)
})

onUnmounted(()=>{
    window.removeEventListener('mousedown', onmousedown)
})


function onAddQuestion() {
    questions.value.push(activeQuestion.value)
    activeQuestion.value = structuredClone(NEW_QUESTION)
    closeOverlay()
}

function onDeleteQuestion() {
    questions.value.splice(activeQuestion.value.id-1, 1)
    for (const [i, card] of questions.value.entries()) { card.id = i+1 }  // fix the ids
    activeQuestion.value = structuredClone(NEW_QUESTION)
    closeOverlay()
}

function chooseCorrectOption(i: number) {
    activeQuestion.value.correct = [i]
}

async function onFinishCreation() {
    if (!quizTitle.value) { return alert('Enter title please, that\'s required') }
    if (questions.value.length <= 0) { return alert('Create at least one question please!')}
    let ok = await fetchCreateQuiz(
        quizTitle.value,
        quizDescription.value,
        questions.value
    )
    if (ok) {
        router.push('/quizzes')
    } else {
        alert('Something went wrong, and we could not create your quiz, sorry!')
    }

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

        <div class="step-quiz-name">
            <div class="title-line">
                <h2 class="step">Step 1</h2>
                <h2 class="step-title">Name new Quiz</h2>
            </div>
            <div class="inputs">
                <BasicInput placeholder="Enter Quiz title" v-model="quizTitle"></BasicInput>
                <BasicTextArea placeholder="Enter Quiz description (optional)" v-model="quizDescription"></BasicTextArea>
            </div>
            <div ref="disappearing-buttons-line" class="buttons-line">
                <BasicButton @click="nextStep">Continue</BasicButton>
            </div>
        </div>

        <div class="step-generate hidden not-displayed" ref="step-2">
            <div class="title-line">
                <h2 class="step">Step 2</h2>
                <h2 class="step-title">Use <span class="ai-text">AI</span> to generate questions</h2>
            </div>
            <div class="actions">
                <BasicFileUpload @changed="onFilesChanged"></BasicFileUpload>
                <div class="buttons-line">
                    <BasicButton class="skip-btn" :class="skipBtnClass" variant="secondary" @click="nextStep">Skip</BasicButton>
                    <BasicButton :disabled="!allowGenerate" class="generate-btn" variant="ai" @click="generateQuestions">Generate Questions</BasicButton>
                </div>
            </div>
        </div>

    </div>


    <div class="right-side">

        <div class="step-edit hidden not-displayed" ref="step-3">
            <div class="title-line">
                <h2 class="step">Step 3</h2>
                <h2 class="step-title">Edit generated questions</h2>
            </div>
            <div class="questions-list">
                <EditQuestion v-for="q in questions"
                    :index="q.id"
                    :question="q.question"
                    @click="onStartEditQuestion(q.id-1)"
                />
            </div>
            <div class="buttons-line">
                <BasicButton variant="secondary" @click="onStartAddNewQuestion()">Add Question</BasicButton>
                <BasicButton @click="onFinishCreation">Finish Creation</BasicButton>
            </div>
        </div>

    </div>
</div>

</template>

<style scoped>
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

.main-container {
    padding: 64px;
    gap: 32px;
    display: flex;
    flex-wrap: wrap;
}

.buttons-line.hidden {
    opacity: 0;
    margin-top: -3.5em;
}

.skip-btn {
    opacity: 1;
    width: 5em;
    transition: 0.5s;
}

.skip-btn.hidden {
    opacity: 0;
    margin-left: -6em;
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

.not-displayed {
    display: none;
}

.step-generate.hidden, .step-edit.hidden {
    opacity: 0;
    user-select: none;
    cursor: default;
}

.step-quiz-name, .step-generate, .step-edit {
    opacity: 1;
    background-color: var(--white);
    padding: 16px;
    gap: 16px;
    display: flex;
    flex-direction: column;
    border-radius: 16px;
    transition: 0.5s;
}

.buttons-line {
    display: flex;
    flex-direction: row;
    justify-content: end;
    gap: 8px;
    opacity: 1;
    transition: 0.5s;
    height: 2.5em;
}


.step-edit .buttons-line {
    justify-content: space-between;
}

.generate-btn {
    width: 100%;
}

.step-generate {
    height: 100%;
}

.step-generate .file-upload {
    width: 100%;
    height: 100%;
}

.step-generate .actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    height: 100%;
}

.step-edit {
    height: 100%;
}

.step-quiz-name textarea {
    height: 6em;
    min-height: 4em;
}

.step-quiz-name .inputs {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.questions-list { 
    display: flex;
    flex-direction: column;
    gap: 8px;
    overflow-y: auto;
    height: 100%;
}

h2 {
    font-size: 24px;
    margin: 0;
}

.step {
    color: var(--secondary)
}

.title-line {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    line-height: 150%;
}

.step-title span {
  font-weight: bold;
}

.ai-text {
  /* For modern browsers */
  background: linear-gradient(to bottom, #68F2FF 40%, #FF61ED);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  
  /* Fallback for browsers that don't support background-clip: text */
  /* This will use the starting color of the gradient */
  @supports not (background-clip: text) {
    color: #68F2FF;
  }
}
</style>
