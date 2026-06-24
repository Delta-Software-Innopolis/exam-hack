<script setup lang="ts">
  import BasicButton from '@/components/basic/BasicButton.vue';
  import { ref } from 'vue';

  type Question = {
    id: number,
    text: string,
  }

  const questions = ref<Question[]>([
    {
      id: 1,
      text: "Why do dogs think their tails..."
    },
    {
      id: 2,
      text: "What is Vue?"
    }
  ])

  const editingIndex = ref<number | null>(null)

  const editText = ref("")

  function startEdit(index: number) {
    editingIndex.value = index
    const question = questions.value[index]
    if (!question) return
    editText.value = question.text
  }

  function saveEdit(index: number) {
    const question = questions.value[index]
    if (!question) return
    question.text = editText.value;
    editingIndex.value = null;
  }

  function addQuestion() {
    questions.value.push({
      id: Date.now(),
      text: "New question"
    });

    editingIndex.value = questions.value.length - 1;
    editText.value = "New question";
  }

</script>

<template>
  
  <div class="questions">
    <div v-for="(question, index) in questions" :key="question.id" class="question-card">
      <input v-if="editingIndex === index" v-model="editText" class="question-input" />
      <span v-else><span class="index-wrapper">{{index + 1}}.</span>{{ question.text }}</span>
      <div v-if="editingIndex === index" class="save-button" @click="saveEdit(index)"><div class="checkmark-img"><img src="/save_question_icon.svg" alt="save_question_icon" /></div></div>
      <div v-else class="edit-button" @click="startEdit(index)"><img src="/edit_question_icon.svg" alt="edit_question_icon.svg" /></div> 
    </div>
    <BasicButton class="add-button" @click="addQuestion">+ Add question</BasicButton>
  </div>
</template>

<style>
  .question-card {
    width: 400px;
    height: 48px;
    border: 0;
    background-color: white;
    border-radius: 16px;
    margin-top: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    padding-top: 8px;
    padding-bottom: 8px;
    font-size: 14px;
  }

  .index-wrapper {
    margin-right: 8px;
  }

  .save-button {
   cursor: pointer;
   margin-left: auto;
  }

  .edit-button {
   cursor: pointer; 
  }

  .checkmark-img {
    width: 20px;
    height: 20px;
  }

  .checkmark-img img {
    width: 100%;
    height: auto;
  }

  .question-input {
    border: none;
    outline: none;
    width: 348px;
    display: flex;
    flex: 1;
  }

  .add-button {
    width: 400px;
    height: 48px;
    font-size: 16px !important;
    margin-top: 16px;
  }
</style>
