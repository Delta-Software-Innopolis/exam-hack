<script setup lang="ts">
import { onUnmounted, useTemplateRef } from 'vue';
import { ref } from 'vue';
import LoadingThingSVG from '@/assets/LoadingThing.svg';

import UnclosableModalWindow from '../basic/UnclosableModalWindow.vue';


const textIdx = ref(0);
setInterval(async ()=>{
    textIdx.value = (textIdx.value + 1) % 10;
}, 3000);


defineExpose({ open, close });

function open() {
    textIdx.value = 0;
    modal?.value?.open();
}
function close() {
    modal?.value?.close();
}

const modal = useTemplateRef('modal');
</script>


<template>
    <UnclosableModalWindow ref="modal">
        <div class="modal-wrapper">
            <h1>Generating Questions</h1>
            <LoadingThingSVG class="loading-icon"/>
            <div class="loading-text">
                <Transition name="loading-text">
                    <p v-if="textIdx === 0">Skimming through the documents...</p>
                    <p v-else-if="textIdx === 1">Remembering the task...</p>
                    <p v-else-if="textIdx === 2">Identifying key topics...</p>
                    <p v-else-if="textIdx === 3">Selecting relevant pain points...</p>
                    <p v-else-if="textIdx === 4">Counting number of questions...</p>
                    <p v-else-if="textIdx === 5">Allowing for some creativity...</p>
                    <p v-else-if="textIdx === 6">Lighting up overhead bulbs...</p>
                    <p v-else-if="textIdx === 7">Strengthening engagement right here...</p>
                    <p v-else-if="textIdx === 8">Hope nobody notices...</p>
                    <p v-else-if="textIdx === 9">Controling quiz difficulty...</p>
                    <p v-else-if="textIdx === 10">Coming up to the deadline...</p>
                </Transition>
            </div>
        </div>
    </UnclosableModalWindow>
</template>


<style scoped>
.modal-wrapper {
    display: flex;
    flex-direction: column;
    width: 20em;
    height: fit-content;
    gap: 8px;
    padding-bottom: 32px;
    background-color: var(--white);
    border-radius: 16px;
    justify-content: center;
    align-items: center;
}

.modal-wrapper h1 {
    font-size: 24px;
}

.modal-wrapper p {
    font-size: 16px;
    color: var(--secondary)
}

.loading-icon {
    -webkit-animation:spin 2s linear infinite;
    -moz-animation:spin 2s linear infinite;
    animation:spin 2s linear infinite;
}

@-moz-keyframes spin { 100% { -moz-transform: rotate(360deg); } }
@-webkit-keyframes spin { 100% { -webkit-transform: rotate(360deg); } }
@keyframes spin { 100% { -webkit-transform: rotate(360deg); transform:rotate(360deg); } }

.loading-text {
    position: relative;
    width: 100%;
}

.loading-text p {
    position: absolute;
    inset: 0;
    width: 100%;
    text-align: center;
    height: 0;
}

.loading-text-enter-active,
.loading-text-leave-active {
    transition: 
        opacity 0.5s ease,
        transform 0.5s ease
    ;
}

.loading-text-enter-active {
    transition-delay: 1s;
}

.loading-text-enter-from {
    opacity: 0;
}

.loading-text-leave-to {
    opacity: 0;
    transform: translateX(16px);
}
</style>
