<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import BasicInput from '@/components/basic/BasicInput.vue';
import BasicTextArea from '@/components/basic/BasicTextArea.vue';
import BasicFileUpload from '@/components/basic/BasicFileUpload.vue';
import BasicQuestionsList from '@/components/basic/BasicQuestionsList.vue';
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import router from '@/router';
const userStore = useUserStore()
const quizTitle = ref("");
const quizDescription = ref("");
const files = ref<File[]>([])
const isLoading = ref(false)
async function generateQuestions() {
  try {
    isLoading.value = true
    const formData = new FormData()

    formData.append('name', quizTitle.value)
    console.log(files.value)

    for (const file of files.value) {
      formData.append('files', file)
    }
    const address = import.meta.env.DEV ? "http://localhost:8001": import.meta.env.VITE_CORE_URL_DEV 
    const response = await fetch(
      `${address}/core/pack/generate`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${userStore.getAccessToken()}`,
        },
        body: formData,
      }
    )

    if (!response.ok) {
      const text = await response.text()
      throw new Error(`Error generating quiz: ${text}`)
    }

    const data = await response.json()

    console.log(data)
    router.push({name: "quizzes"})

  } catch (error) {
    console.error('Failed to generate questions:', error)
  } finally {
    isLoading.value = false
  }
}
function onFilesChanged(f: File[]) {
  files.value = [...f]
  console.log('saved', files.value)
}
</script>

<template>
  <div class="window-wrapper" v-if="!isLoading">
    <div class="segment">
      <div class="elements-wrapper">
        <div class="title">Name your new quiz</div>
        <div class="inputs-wrapper">
          <BasicInput placeholder='Quiz title' v-model="quizTitle"></BasicInput>
          <BasicTextArea placeholder='Quiz description' v-model="quizDescription"></BasicTextArea>
        </div>
        <BasicButton variant="green">Next</BasicButton>
      </div>
    </div>
    <div class="segment">
      <div class="title">Use <span class="gradient-text">AI</span> to generate questions</div>
      <div class="instructions-wrapper">
        <p>1. Upload files (presentations, books, notes)</p>
        <p>2. AI will generate questions</p>
        <p>3. Edit them as you want</p>
      </div>
      <p>Uploaded files</p>
      <BasicFileUpload @changed="onFilesChanged"></BasicFileUpload>
      <div class="buttons-wrapper">
        <BasicButton class="skip-button">Skip</BasicButton>
        <BasicButton class="generate-button" variant="gradient" @click="generateQuestions()"><span>Generate Questions</span></BasicButton>
      </div>
    </div>
    <div class="segment">
      <div class="title">Review generated questions</div>
      <BasicQuestionsList></BasicQuestionsList>
      <BasicButton class="save-button" variant="green">Save Quiz</BasicButton>
    </div>
  </div>
  <div v-else>Loading</div>
</template>

<style>
  .window-wrapper {  
    display: flex;
    flex-direction: row;
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    justify-content: space-evenly;
    background-color: #f3f6f7;
  }

  .segment {
    margin: 0 5px;
    padding: 0 32px;
 
  }

  .elements-wrapper {
    margin-right: 32.67px;
    width: 366px;
  }

  .title {
    font-size: 24px;
    font-weight: 700;  
    margin-top: 202px;
  }

  .inputs-wrapper {
    margin-top: 32px;

    display: flex;
    flex-direction: column;
    gap: 16px;
    height: fit-content;
    width: fit-content;
  }

  .inputs-wrapper input {
    border: 1px solid #D9D9D9;
    font-size: 16px;
    font-family: inter;
    width: 366px;
  }

  .inputs-wrapper textarea {
    width: 366px;
    height: 107px;
  }
  
  .elements-wrapper button {
    width: 366px;
    margin-top: 260px;
    font-size: 16px;
    height: 48px;
  }

  .instructions-wrapper {
    color: #757575;
    margin-top: 32px;
  }

  .segment > p {
    margin-top: 32px;
    font-weight: 700;
    margin-bottom: 8px;
  }

  .buttons-wrapper {
    display: flex;
    gap: 16px;
    flex-direction: row;
    align-items: center;
    margin-top: 181px;
  }

  .buttons-wrapper button {
    font-size: 16px !important;
  }

  .skip-button {
    width: 96px !important; 
  }

  .generate-button {
    width: 287px !important;
  }

  .gradient-text {
    font-weight: 700;

    background: linear-gradient(
      to bottom,
      #68F2FF 60%,
      #FF61ED 90%
    );

    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .save-button {
    width: 400px;
    font-size: 16px !important;
    height: 48px;
    margin-top: 16px;
  }
</style>
