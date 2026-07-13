<script setup lang="ts">
import { ref } from 'vue';
import BasicFileUpload from '../basic/BasicFileUpload.vue';
import BasicButton from '../basic/BasicButton.vue';

const shown = ref(false);
const showSkipButton = ref(true); 
const allowGenerate = ref(false);

const uploadedFiles = defineModel<File[]>('uploaded-files', { default: [] });


function onClickSkip() {
    showSkipButton.value = false;
    emit('click-skip');
}

function onFilesChanged(files: File[]) {
    uploadedFiles.value = files;
    allowGenerate.value = files.length > 0;
}


function show() {
    shown.value = true;
}

function resetSkipButton() {
    showSkipButton.value = true;
}


defineExpose({
    show,
    resetSkipButton
});

const emit = defineEmits<{
    (e: 'click-skip'): void,
    (e: 'click-generate'): void
}>();
</script>


<template>
    <Transition name="main">
        <div class="main" v-if="shown">
            <div class="title-line">
                <h2 class="step">Step 2</h2>
                <h2 class="step-title">Use <span class="ai-text">AI</span> to generate questions</h2>
            </div>
            <div class="actions">
                <BasicFileUpload @changed="onFilesChanged"></BasicFileUpload>
                <div class="buttons-line">
                    <Transition name="skip-button">
                        <BasicButton v-if="showSkipButton" class="skip-btn" variant="secondary" @click="onClickSkip">
                            Skip
                        </BasicButton>
                    </Transition>
                    <BasicButton :disabled="!allowGenerate" class="generate-btn" variant="ai" @click="emit('click-generate')">
                        Generate Questions
                    </BasicButton>
                </div>
            </div>
        </div>
    </Transition>
</template>


<style scoped>
.main {
    opacity: 1;
    background-color: var(--white);
    padding: 16px;
    gap: 16px;
    display: flex;
    flex-direction: column;
    border-radius: 16px;
    transition: 0.5s;
}

h2 {
    font-size: 24px;
    margin: 0;
}

.title-line {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    line-height: 150%;
}

.step {
    color: var(--secondary)
}

.step-title span {
  font-weight: bold;
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

.main {
    height: 100%;
}

.file-upload {
    width: 100%;
    height: 100%;
}

.actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    height: 100%;
}

.generate-btn {
    width: 100%;
}

.skip-btn {
    opacity: 1;
    width: 5em;
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

.main-enter-from,
.main-leave-to {
    opacity: 0;
    user-select: none;
    cursor: default;
}

.main-enter-active,
.main-leave-active {
    transition: 0.5s opacity;
}

.skip-button-enter-from,
.skip-button-leave-to {
    opacity: 0;
    margin-left: -6em;
}

.skip-button-enter-active,
.skip-button-leave-active {
    transition:
        0.5s opacity,
        0.5s margin-left
    ;
}
</style>
