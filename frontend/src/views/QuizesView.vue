<script setup lang="ts">
import type QuizItem from "@/types"
import type { ComputedRef } from "vue";
import { ref, onBeforeMount, onUnmounted, computed} from "vue";
import QuizComponent from "@/components/newBasic/QuizComponent.vue";
import { useRouter } from "vue-router";
import { useQuizzesStore } from "@/stores/quizzes";
import BasicButton from "@/components/newBasic/BasicButton.vue";

const router = useRouter();
const quizzesStore = useQuizzesStore()
const quizes = computed(() => quizzesStore.quizzes) as ComputedRef<QuizItem[]>;
const isLoading = ref(true);

onBeforeMount(async ()=> {
  try {
    await quizzesStore.fetchQuizzes();
  } catch (error) {
    console.error("Error", error);
  } finally {
    isLoading.value = false;
  }
})
</script>

<template>
  <div class="main-container" v-if="!isLoading">
    <div class="top-container">
      <h1>Saved Quizzes</h1>
      <div class="actions-wrapper">
        <BasicButton variant="primary" @click="router.push({name: 'quizzes-new'})">Create New</BasicButton>
      </div>
    </div>
    <div class="Quiz-Container">
      <QuizComponent v-for="quiz in quizes" 
        :key="quiz.id" 
        :id="quiz.id"
        :name="quiz.name"
        :author="quiz.author.username"
        :description="quiz.description"
      </QuizComponent>
    </div>
  </div>
  <div v-else>Loading</div>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 64px;
}

.top-container { 
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.Quiz-Container {
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  gap: 16px;
  cursor: pointer;
}
.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>