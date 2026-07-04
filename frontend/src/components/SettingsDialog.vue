<script setup lang="ts">
import BasicButton from './newBasic/BasicButton.vue'
import CrossSVG from '@/assets/Cross.svg'

const allowMultipleCorrects = defineModel<boolean>('allowMultipleCorrects', {default: true})
const numberOfQuestions = defineModel<number>('numberOfQuestions', {default: 10})


const emit = defineEmits<{
    (e: 'clickGenerate'): void,
    (e: 'clickClose'): void
}>()
</script>


<template>
<div class="main-wrapper">
    <div class="top-line">
        <h3>Generation Settings</h3>
        <CrossSVG @click="emit('clickClose')"/>
    </div>
    <div class="settings-wrapper">
        <div class="setting">
            <p>Number of questions</p>
            <input type="number" max="20" min="1" v-model="numberOfQuestions">
        </div>
        <div class="setting">
            <p>Allow multiple correct answers</p>
            <input type="checkbox" v-model="allowMultipleCorrects">
        </div>
    </div>
    <BasicButton variant="ai" @click="emit('clickGenerate')">Generate Questions</BasicButton>
</div>
</template>


<style scoped>
.main-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 32px;
    padding: 16px;
    background-color: var(--white);
    border-radius: 16px;
    width: fit-content;
}

.top-line {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: top;
    --icon-stroke: var(--secondary-dimm);
    --icon-height: 12px;
    width: 100%;
}

svg {
    cursor: pointer;
}

h3 {
    font-size: 20px;
    color: var(--secondary-dimm);
    margin: 0;
    padding: 0;
    line-height: 75%;
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
