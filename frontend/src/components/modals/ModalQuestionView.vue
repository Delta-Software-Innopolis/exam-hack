<script setup lang="ts">
import { ref, useTemplateRef } from 'vue';

import ModalWindow from '@/components/basic/ModalWindow.vue';
import BasicButton from '@/components/basic/BasicButton.vue';
import EyeSVG from '@/assets/Eye.svg';
import CheckSVG from '@/assets/Check.svg';
import CrossSVG from '@/assets/Cross.svg';

import type { Card } from '@/types';

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

</script>


<template>
    <ModalWindow ref="modal">
        <div v-if="question" class="question">
            <p>{{ question.question }}</p>
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
                    <div class="options-item" v-for="[i, option] in question.options.entries()">
                        <span>{{ option }}</span>
                        <CheckSVG v-if="question.correct.includes(i)" class="option-check"/>
                        <CrossSVG v-else class="option-cross"/>
                    </div>
                </div>
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

.question p {
    margin-top: 8px;
    padding: 8px;
    padding-left: 12px;
    padding-right: 12px;
    background-color: var(--background-blueish);
    border-radius: 8px;
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

.options-item span {
    display: flex;
    align-items: center;

    width: 100%;
    background: none;
    border: none;
    padding-left: 4px;
    line-height: 1.2;
    field-sizing: content; /* Chrome/Edge */

    flex: 1;
    overflow: hidden;
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

.options-cover-enter-active,
.options-cover-leave-active {
    transition: opacity 0.2s ease;
}

.options-cover-enter-from,
.options-cover-leave-to {
    opacity: 0;
}
</style>
