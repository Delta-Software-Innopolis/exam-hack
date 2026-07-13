<script setup lang="ts">
import { ref } from 'vue';
import BasicInput from '../basic/BasicInput.vue';
import BasicTextArea from '../basic/BasicTextArea.vue';
import BasicButton from '../basic/BasicButton.vue';

const title = defineModel<string>('title');
const description = defineModel<string>('description');

const showNextStepButtons = ref(true); 


function onClickNextStep() {
    showNextStepButtons.value = false;
    emit('click-next-step');
}

function resetNextStepButtons() {
    showNextStepButtons.value = true;
}


defineExpose({
    resetNextStepButtons
});

const emit = defineEmits<{
    (e: 'click-next-step'): void
}>();
</script>


<template>
    <div class="main">
        <div class="title-line">
            <h2 class="step">Step 1</h2>
            <h2 class="step-title">Name new Quiz</h2>
        </div>
        <div class="inputs">
            <BasicInput placeholder="Enter Quiz title" v-model="title"></BasicInput>
            <BasicTextArea placeholder="Enter Quiz description (optional)" v-model="description"></BasicTextArea>
        </div>
        <Transition name="buttons-line">
        <div class="buttons-line" v-if="showNextStepButtons">
            <BasicButton variant="primary" @click="onClickNextStep">Continue</BasicButton>
        </div>
        </Transition>
    </div>
</template>


<style scoped>
.buttons-line-leave-active {
    transition:
        0.1s opacity,
        0.2s margin-top
    ;
}

.buttons-line-leave-to {
    opacity: 0;
    margin-top: -3.5em;
}

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
    transition: 0.5s;
    height: 2.5em;
}

.inputs {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

textarea {
    font-family: inherit;
    height: 6em;
    min-height: 4em;
}
</style>
