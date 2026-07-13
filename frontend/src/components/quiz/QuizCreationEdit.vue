<script setup lang="ts">
import { ref, useTemplateRef } from 'vue';
import QuizQuestionsList from '@/components/quiz/QuizQuestionsList.vue';
import ModalQuestionEdit from '../modals/ModalQuestionEdit.vue';
import type { Card } from '@/types.ts';
import PlusButton from '../buttons/PlusButton.vue';
import ModalQuestionAdd from '../modals/ModalQuestionAdd.vue';
import BasicButton from '../basic/BasicButton.vue';

const cards = defineModel<Card[]>('cards', { default: []});
const modalQuestionEdit = useTemplateRef('modal-question-edit');
const modalQuestionAdd = useTemplateRef('modal-question-add');

function onDeleteQuestion(q: Card) {
    const cardIdx = cards.value.indexOf(q);
    if (cardIdx === -1) { return; }
    const card = cards.value.at(cardIdx);
    if (card === undefined) { return; }

    cards.value.splice(cardIdx, 1)
    modalQuestionEdit?.value?.close();
}

function onAddQuestion(q: Card) {
    cards.value.push(q);
    modalQuestionAdd?.value?.close();
}

const shown = ref(false);

function show() {
    shown.value = true;
}

defineExpose({ show });

const emit = defineEmits<{
    (e: 'click-finish'): void
}>();
</script>


<template>
    <Transition name="main">
        <div class="main" v-if="shown">
            <ModalQuestionEdit ref="modal-question-edit" @click-delete="onDeleteQuestion" />
            <ModalQuestionAdd ref="modal-question-add" @click-add="onAddQuestion" />
            <div class="title-line">
                <h2 class="step">Step 3</h2>
                <h2 class="step-title">Edit generated questions</h2>
            </div>
            <QuizQuestionsList
                variant="edit"
                :cards="cards"
                @click-question-item="(idx)=>modalQuestionEdit?.open(cards.at(idx))"
            />
            <div class="buttons-line">
                <PlusButton variant="secondary" @click="modalQuestionAdd?.open()">
                    Add Question
                </PlusButton>
                <BasicButton variant="primary" @click="emit('click-finish')">
                    Finish Creation
                </BasicButton>
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
</style>
