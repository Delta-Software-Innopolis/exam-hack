<script setup lang="ts">
import type { QuizItem } from "@/types"
import type { Ref } from "vue";
import { computed, ref, onMounted, onBeforeUpdate} from "vue";
import QuizComponent from "@/components/basic/QuizComponent.vue";
import { useRouter } from "vue-router";
import { useNewQuizzesStore } from "@/stores/new-quizzes";
import PlusButton from "@/components/buttons/PlusButton.vue";

const router = useRouter();
const quizzesStore = useNewQuizzesStore()
const quizzes = computed(() => quizzesStore.getAllMyQuizzesInfo())
const isLoading = ref(true);

onMounted(async ()=> {
  try {
    await quizzesStore.fetchMyQuizzes();
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
        <PlusButton variant="primary" @click="router.push({name: 'quizzes-new'})">
            Create New
        </PlusButton>
      </div>
    </div>
    <div class="Quiz-Container">
      <QuizComponent v-for="quiz in quizzes" 
        :key="quiz.id" 
        :mock="quiz.mock"
        :id="quiz.id"
        :name="quiz.name"
        :author="quiz.author.name"
        :description="quiz.description"
      ></QuizComponent>
    </div>
  </div>
  <div v-else>Loading...</div>
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
