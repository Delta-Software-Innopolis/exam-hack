<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import BasicInput from '@/components/basic/BasicInput.vue';
import QuizComponent from '@/components/basic/QuizComponent.vue';
import QuestionOption from '@/components/basic/QuestionOption.vue';
import QuizOption from '@/components/basic/QuizOption.vue';
import EditQuestion from '@/components/basic/EditQuestion.vue';
import NavigationSidebar from '@/components/NavigationSidebar.vue';
import { ref, useTemplateRef } from 'vue';
import SettingsDialog from '@/components/SettingsDialog.vue';
import ModalWindow from '@/components/basic/ModalWindow.vue';
import ModalQuestionView from '@/components/quiz-info/ModalQuestionView.vue';
import { MOCK_QUIZZES } from '@/stores/mock-quizzes';
import ModalQuestionEdit from '@/components/quiz-info/ModalQuestionEdit.vue';

const quiz = ref({ id: 1, name: "Quiz Name", author: "User", variant: "white", cards: [{}] });
const mock_quiz = MOCK_QUIZZES[0];
const option1 = ref({ option: "Option 1", isCorrect: false });
const option2 = ref({ option: "Option 2", isCorrect: true });
const question = ref({ index: 1, text: "what is Vue.js?" })

function _alert(...any: unknown[]) { alert(...any); }

const modalView = useTemplateRef('modalView');
const modalEdit = useTemplateRef('modalEdit');
const modalHint = useTemplateRef('modalHint');
</script>

<template>
  <div class="wrapper">
    <BasicButton>Attempt</BasicButton>
    <BasicButton disabled>Attempt</BasicButton>
    <BasicButton variant="secondary">Button</BasicButton>
    <BasicButton variant="secondary" disabled>Button</BasicButton>
    <BasicButton variant="teritary">Button</BasicButton> 
    <BasicButton variant="teritary" disabled>Button</BasicButton> 
    <BasicButton variant="ai">Button</BasicButton>
    <img src="@/assets/DisabledAiButton.svg" alt="disabled ai button" />
    <BasicInput placeholder="Enter something"></BasicInput>
    <BasicInput placeholder="Enter something" disabled></BasicInput>
    <QuizComponent :id="quiz.id" :name="quiz.name" :author="quiz.author"></QuizComponent>
    <QuizComponent :id="quiz.id" :name="quiz.name" :author="quiz.author" :variant="quiz.variant"></QuizComponent>
    <QuestionOption :option="option1.option" :isCorrect="option1.isCorrect"></QuestionOption>
    <QuestionOption :option="option2.option" :isCorrect="option2.isCorrect"></QuestionOption>
    <QuizOption>Option 1: option description</QuizOption>
    <QuizOption variant="white">Option 1: option description</QuizOption>
    <QuizOption variant="green">Option 2: option description</QuizOption>
    <QuizOption variant="red">Option 3: option description</QuizOption>
    <EditQuestion :index="question.index" :question="question.text"></EditQuestion>
    <SettingsDialog></SettingsDialog>
    <BasicButton @click="modalView?.open(mock_quiz?.cards[0])">Open modalView</BasicButton>
    <BasicButton @click="modalEdit?.open(mock_quiz?.cards[0])">Open modalEdit</BasicButton>
    <ModalQuestionView ref="modalView"/>
    <ModalQuestionEdit ref="modalEdit"
        @click-delete="(q)=>_alert(`clicked delete on ${q}`)"
    />


    <ModalWindow ref="modalHint">
        Hint cool hint etc...
    </ModalWindow>

    <BasicButton @click="modalHint?.open()">Open modal hint</BasicButton>

  </div>
</template>

<style scoped>
.wrapper {
  overscroll-behavior: contain;
  padding: 64px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
</style>
