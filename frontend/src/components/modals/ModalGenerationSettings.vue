<script setup lang="ts">
import { useTemplateRef } from 'vue';
import ModalWindow from '@/components/basic/ModalWindow.vue';
import BasicButton from '@/components/basic/BasicButton.vue';

const numberOfQuestions = defineModel<number>('number-of-questions');

const modal = useTemplateRef('modal');

defineExpose({ open, close });

function open() { modal.value?.open(); }
function close() { modal.value?.close(); }

const emit = defineEmits<{
    (e: 'click-generate'): void
}>();
</script>


<template>
    <ModalWindow ref="modal">
    <div class="wrapper">
        <div class="settings-wrapper">
            <div class="setting">
                <p>Number of questions</p>
                <input type="number" max="20" min="1" v-model="numberOfQuestions">
            </div>
        </div>
        <BasicButton variant="ai" @click="emit('click-generate')">
            Generate Questions
        </BasicButton>
    </div>
    </ModalWindow>
</template>


<style scoped>

.wrapper {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding-top: 12px;
}

.setting {
    display: flex;
    flex-direction: row;
    width: 100%;
    gap: 16px;
    justify-content: space-between;
}

.settings-wrapper {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 8px;
}

button {
    width: 100%;
}
</style>
