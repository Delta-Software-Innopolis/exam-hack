<script setup lang="ts">
import type { QuizItem } from "@/types"
import type { ComputedRef, Ref } from "vue";
import { ref, onBeforeMount, onUnmounted, computed, onMounted, onBeforeUpdate} from "vue";
import QuizComponent from "@/components/newBasic/QuizComponent.vue";
import { useRouter } from "vue-router";
import { useNewQuizzesStore } from "@/stores/new-quizzes";
import BasicButton from "@/components/newBasic/BasicButton.vue";

const router = useRouter();
const quizzesStore = useNewQuizzesStore()
const quizzes = ref(quizzesStore.getAllMyQuizzesInfo()) as Ref<QuizItem[]>;
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
onBeforeUpdate(()=>{
    quizzes.value = quizzesStore.getAllMyQuizzesInfo()
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
      <QuizComponent v-for="quiz in quizzes" 
        :key="quiz.id" 
        :mock="quiz.mock"
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