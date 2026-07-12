<script setup lang="ts">
import { onMounted, onUnmounted, ref, useTemplateRef, type Ref, watch } from 'vue';
import type { Card } from '@/types';
import { fetchCreateQuiz } from '@/core';
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import { generateCards } from '@/generation';
import ModalGenerationLoading from '@/components/modals/ModalGenerationLoading.vue';
import ModalGenerationSettings from '@/components/modals/ModalGenerationSettings.vue';
import QuizCreationTitleDescription from '@/components/quiz/QuizCreationTitleDescription.vue';
import QuizCreationGeneration from '@/components/quiz/QuizCreationGeneration.vue';
import QuizCreationEdit from '@/components/quiz/QuizCreationEdit.vue';


const router = useRouter();

const quizTitle = ref('');
const quizDescription = ref('');
const uploadedFiles: Ref<File[]> = ref([]);
const questions: Ref<Card[]> = ref([]);

const allowMultipleCorrects = ref(true);
const generateQuestionsNumber = ref<number>(10);

const hasUnsavedProgress = ref(false)


const hasUnsavedProgress = ref(false)


async function onGenerateConfirm() {
    modalGenerationSettings?.value?.close();
    if (!quizTitle.value) {
        alert('enter quiz title first');
        return;
    }
    else if (uploadedFiles.value.length == 0) {
        alert('upload at list one file');
        return;
    } else {
        try {
            modalGenerationLoading.value?.open();
            const result = await generateCards(
                uploadedFiles.value,
                quizTitle.value,
                allowMultipleCorrects.value ? "multiple_choice" : "single_answer",
                generateQuestionsNumber.value
            );
            console.log("GOT RESULT", result)
            questions.value = questions.value.concat(result.cards)
        } catch (err) {
            alert(`Sorry, we experienced some error: ${err}. But you can create your quiz manually!`)
        }
        showStepEdit();
    }
    modalGenerationLoading.value?.close();
}

function showStepEdit() {
    if (!stepEditShown.value) {
        stepEditShown.value = true;
        stepEdit.value?.show();
    }
}

const stepEditShown = ref(false);

const stepTitle = useTemplateRef('step-title');
const stepGenerate = useTemplateRef('step-generate');
const stepEdit = useTemplateRef('step-edit');

const modalGenerationLoading = useTemplateRef('modal-generation-loading');
const modalGenerationSettings = useTemplateRef('modal-generation-settings');


async function onFinishCreation() {
    if (!quizTitle.value) { return alert('Enter title please, that\'s required') }
    if (questions.value.length <= 0) { return alert('Create at least one question please!')}
    let ok = await fetchCreateQuiz(
        quizTitle.value,
        quizDescription.value,
        questions.value
    )
    if (ok) {
        router.push('/quizzes')
    } else {
        alert('Something went wrong, and we could not create your quiz, sorry!')
    }

}


watch(
    [quizTitle, quizDescription],
    () => hasUnsavedProgress.value = true
)


function beforeUnload(event: BeforeUnloadEvent) {
    if (!hasUnsavedProgress.value) return
    event.preventDefault()
    event.returnValue = ''
}


onMounted(()=>{
    window.addEventListener('beforeunload', beforeUnload)
})


onUnmounted(()=>{
    window.removeEventListener('beforeunload', beforeUnload)
})


onBeforeRouteLeave(() => {
    if (!hasUnsavedProgress.value) return true

    return window.confirm(
        "Your quiz progress will be lost. Continue?"
    )
})
</script>


<template>
<div class="main-container">
    <ModalGenerationLoading ref="modal-generation-loading" />
    <ModalGenerationSettings ref="modal-generation-settings" 
        v-model:number-of-questions="generateQuestionsNumber"
        @click-generate="onGenerateConfirm"
    />
    <div class="left-side">
        <QuizCreationTitleDescription ref='step-title'
            v-model:title="quizTitle"
            v-model:description="quizDescription"
            @click-next-step="stepGenerate?.show"
        />
        <QuizCreationGeneration ref="step-generate"
            v-model:uploaded-files="uploadedFiles"
            @click-skip="showStepEdit"
            @click-generate="modalGenerationSettings?.open"
        />
    </div>
    <div class="right-side">
        <QuizCreationEdit ref="step-edit"
            v-model:cards="questions"
            @click-finish="onFinishCreation"
        />
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
</style>
