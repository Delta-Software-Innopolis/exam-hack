<script setup lang="ts">
import { ref, useTemplateRef, watch } from 'vue';

import ModalWindow from '@/components/basic/ModalWindow.vue';
import BasicButton from '@/components/basic/BasicButton.vue';
import EyeSVG from '@/assets/Eye.svg';
import CheckSVG from '@/assets/Check.svg';
import CrossSVG from '@/assets/Cross.svg';

import type { Card } from '@/types';
import BasicTextArea from '../basic/BasicTextArea.vue';
import TrashButton from '../buttons/TrashButton.vue';


const question = ref<Card>();
const optionsHidden = ref(true);

defineExpose({ open, close });


function open(_question: Card | undefined) {
    question.value = _question;
    optionsHidden.value = true;
    modal?.value?.open();
}
function close() { modal?.value?.close(); }

const modal = useTemplateRef('modal');


function makeIncorrect(i: number) {
    if (question.value === undefined) { return; }
    const idx = question.value.correct.indexOf(i);
    if (idx !== -1) {
        question.value.correct.splice(idx, 1);
    }
}

function makeCorrect(i: number) {
    if (question.value === undefined) { return; }
    const idx = question.value.correct.indexOf(i);
    if (idx === -1) {
        question.value.correct.push(i);
    }
}

const emit = defineEmits<{
    (e: 'click-delete', q: Card): void,
}>();
</script>


<template>
    <ModalWindow ref="modal">
        <div v-if="question" class="question">
            <BasicTextArea v-model="question.question" class="question-question"/>
            <h4>Options</h4>
            <div class="options">
                <Transition name="options-cover">
                    <div class="options-cover" v-if="optionsHidden">
                        <BasicButton @click="optionsHidden = false">
                            <EyeSVG /> Show 
                        </BasicButton>
                    </div>
                </Transition>
                <div class="options-list">
                    <div class="options-item" v-for="[i,_] in question.options.entries()">
                        <textarea v-model="question.options[i]"></textarea>
                        <CheckSVG v-if="question.correct.includes(i)" class="option-check" @click="makeIncorrect(i)"/>
                        <CrossSVG v-else class="option-cross" @click="makeCorrect(i)"/>
                    </div>
                </div>
            </div>
            <div class="buttons">
                <TrashButton variant="red" @click="emit('click-delete', question)">Delete Question</TrashButton>
            </div>
        </div>
        <div v-else>
            Question undefined...
        </div>
    </ModalWindow>
</template>


<style scoped>
.question {
    max-width: 60vw;
    min-width: 30em;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.question textarea {
    font-family: 'Inter', sans-serif;
    resize: none;
}

.question-question {
    margin-top: 8px;
    padding: 8px;
    padding-left: 12px;
    padding-right: 12px;
    background-color: var(--background-blueish);
    border-radius: 8px;
    width: 100%;
}

.question h4 {
    color: var(--secondary);
    font-weight: bold;
    margin-bottom: -8px;
}

.options {
    display: grid;
    grid-template: 1fr/1fr;
    place-items: center;
}

.options > * {
  grid-column: 1 / 1;
  grid-row: 1 / 1;
}

.options-cover {
    z-index: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    border-radius: 8px;
    border: 1px dashed var(--secondary);
    background-color: var(--background-blueish);

    button {
        --icon-stroke: var(--secondary-dimm);
        gap: 8px;
    }
}

.options-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
    height: fit-content;
}

.options-item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    background-color: var(--background-blueish);
    padding: 8px;
    border-radius: 8px;
}

.options-item textarea {
    flex: 1;
    resize: none;
    overflow: hidden;
    max-width: 30em;

    background: none;
    border: none;
    outline: none;
    font: inherit;
    color: inherit;
    line-height: 1.2;
    padding-left: 4px;

    field-sizing: content; /* Chrome/Edge */
}

.option-cross, .option-check {
    border: 1px solid black;
    border-radius: 4px;
    background-color: var(--white);
    cursor: pointer;
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

.buttons {
    display: flex;
    flex-direction: column;
}

.options-cover-enter-active,
.options-cover-leave-active {
    transition: opacity 0.2s ease;
}

.options-cover-enter-from,
.options-cover-leave-to {
    opacity: 0;
}
</style>
