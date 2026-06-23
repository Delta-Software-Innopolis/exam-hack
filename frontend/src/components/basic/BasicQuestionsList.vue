<script setup lang="ts">
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
    editingIndex.value = index;
    editText.value = questions.value[index].text;
  }

  function saveEdit(index: number) {
    questions.value[index].text = editText.value;
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
      <button v-if="editingIndex === index" class="save" @click="saveEdit(index)">✓</button>
     <button v-else class="edit" @click="startEdit(index)">✎</button> 
    </div>
    <button class="add" @click="addQuestion">+ Add question</button>
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

  }

  .index-wrapper {
    margin-right: 8px;
  }
</style>
