<script setup lang="ts">
import BasicFileUpload from '@/components/newBasic/BasicFileUpload.vue';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import BasicInput from '@/components/newBasic/BasicInput.vue';
import BasicTextArea from '@/components/newBasic/BasicTextArea.vue';
import EditQuestion from '@/components/newBasic/EditQuestion.vue';

import { ref, useTemplateRef, type Ref } from 'vue';
import type { Card } from '@/types';


const quizTitle: Ref<string | null> = ref(null)
const quizDescription: Ref<string | null> = ref(null)
const uploadedFiles: Ref<File[]> = ref([])
const questions: Ref<Card[]> = ref([])

const currentStep = ref(1)
const generateBtnClass = ref({})

const firstBtnLine = useTemplateRef('disappearing-buttons-line')
const skipBtnClass = ref({ hidden: false, 'not-displayed': false })
const stepGenerate = useTemplateRef('step-2')
const stepEdit = useTemplateRef('step-3')


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


function onFilesChanged(f: File[]) {
    console.log(f)
}

</script>


<template>
<div class="main-container">
    <div class="left-side">
        <div class="step-quiz-name">
            <div class="title-line">
                <h2 class="step">Step 1</h2>
                <h2 class="step-title">Name new Quiz</h2>
            </div>
            <div class="inputs">
                <BasicInput placeholder="Enter Quiz title"></BasicInput>
                <BasicTextArea placeholder="Enter Quiz description (optional)"></BasicTextArea>
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
                    <BasicButton disabled class="generate-btn" variant="ai">Generate Questions</BasicButton>
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
                />
            </div>
            <div class="buttons-line">
                <BasicButton variant="secondary">Add Question</BasicButton>
                <BasicButton>Finish Creation</BasicButton>
            </div>
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
