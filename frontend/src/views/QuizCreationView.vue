<script setup lang="ts">
import BasicFileUpload from '@/components/newBasic/BasicFileUpload.vue';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import BasicInput from '@/components/newBasic/BasicInput.vue';
import BasicTextArea from '@/components/newBasic/BasicTextArea.vue';
import EditQuestion from '@/components/newBasic/EditQuestion.vue';


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
            <BasicInput placeholder="Enter Quiz title"></BasicInput>
            <BasicTextArea placeholder="Enter Quiz description (optional)"></BasicTextArea>
            <div class="buttons-line">
                <BasicButton>Continue</BasicButton>
            </div>
        </div>
        <div class="step-generate">
            <div class="title-line">
                <h2 class="step">Step 2</h2>
                <h2 class="step-title">Use <span class="ai-text">AI</span> to generate questions</h2>
            </div>
            <div class="actions">
                <BasicFileUpload @changed="onFilesChanged"></BasicFileUpload>
                <div class="buttons-line">
                    <BasicButton variant="secondary">Skip</BasicButton>
                    <BasicButton disabled class="generate-btn" variant="ai">Generate Questions</BasicButton>
                </div>
            </div>
        </div>
    </div>
    <div class="right-side">
        <div class="step-edit">
            <div class="title-line">
                <h2 class="step">Step 3</h2>
                <h2 class="step-title">Edit generated questions</h2>
            </div>
            <div class="questions-list">
                <EditQuestion v-for="i in 20"
                    :index="i"
                    question="First question"
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

.step-quiz-name, .step-generate, .step-edit {
    background-color: var(--white);
    padding: 16px;
    gap: 16px;
    display: flex;
    flex-direction: column;
    border-radius: 16px;
}

.buttons-line {
    display: flex;
    flex-direction: row;
    justify-content: end;
    gap: 8px;
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
