<script setup lang="ts">
import type QuizItem from "@/types"
import type { ComputedRef } from "vue";
import { ref, onBeforeMount, onUnmounted, computed} from "vue";
import QuizComponent from "@/components/QuizComponent.vue";
import { useRouter } from "vue-router";
import { useQuizzesStore } from "@/stores/quizzes";
const router = useRouter();
const quizzesStore = useQuizzesStore()
const quizes = computed(() => quizzesStore.quizes) as ComputedRef<QuizItem[]>;
const isLoading = ref(true);
onBeforeMount(async ()=> {
  quizzesStore.headerInfo = "Your quizzes" 
  try {
    await quizzesStore.fetchQuizzes();
  } catch (error) {
    console.error("Error", error);
  } finally {
    isLoading.value = false;
  }
})
onUnmounted(() => {
  quizzesStore.headerInfo == "Your quizzes" ? "" : quizzesStore.headerInfo
})
</script>

<template>
  <div class="main-container">
    <div v-if="!isLoading" class="Quiz-Container">
      <QuizComponent v-for="quiz in quizes" 
        :key="quiz.id" 
        :name="quiz.name"
        :author="quiz.author.name"
        :description="quiz.description"
        @click="router.push({name: 'solving', params: {quizId: quiz.id}})">
      </QuizComponent>
    </div>
    <div v-else>Loading</div>
  </div>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  gap: 64px
}
.main-container h1 {
  align-self: center;
  font-weight: bold;
  font-size: 40px;
  margin: 10px;
}
.Quiz-Container {
    display: flex;
    flex-wrap: wrap;
    gap: 2rem 2.5rem;
    justify-content: center;
}

.Quiz-Container > * {
    flex: 0 0 calc((100% - 9rem) / 4);
    box-sizing: border-box;
}


</style>