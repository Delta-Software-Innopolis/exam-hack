<script setup lang="ts">
import type QuizItem from "@/types"
import type { ComputedRef } from "vue";
import { ref, onBeforeMount, onUnmounted, computed} from "vue";
import QuizComponent from "@/components/newBasic/QuizComponent.vue";
import { useRouter } from "vue-router";
import { useQuizzesStore } from "@/stores/quizzes";
import BasicButton from "@/components/newBasic/BasicButton.vue";
import Search from "@/components/newBasic/Search.vue";

const quizzes = ref(QuizComponent);
const isLoading = ref(false)

const book = ref('')
const professor = ref('')
const university = ref('')
const subject = ref('')
const main = ref('')

const params = new URLSearchParams()
const addParam = (key: string, value: string) => {
    const trimmed = value.trim()
    if (trimmed !== "") {
        params.append(key, trimmed)
    }
}

async function fetchInfo() {
try {
    addParam("subject", subject.value)
    addParam("professor", professor.value)
    addParam("course_book", book.value)
    addParam("university", university.value)
    addParam("search_main", main.value)

    params.append("offset", "1")
    params.append("limit", "16")
    const address = import.meta.env.DEV ? "http://localhost:8000": import.meta.env.VITE_HUB_URL_DEV
    isLoading.value = true 
    const response = await fetch(`${address}/hub/packs?${params.toString()}`,
    {
        method: 'GET'
    })
    if (!response.ok) {
        throw new Error(`Ошибка сети: ${response.status}`)
    }
    const data = await response.json()
    if (data.packs.length < 1) {
        return
    } 
    quizzes.value = data.packs
    console.log(quizzes.value)
} catch (error) {
    console.error('Не удалось загрузить квизы:', error) 
} finally {
    isLoading.value = false
}

}
onBeforeMount(async ()=> {
  try {
    await fetchInfo()
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
        <Search :sug_type="'name'" v-model="main" class="main-tag"></Search>
        <BasicButton @click="fetchInfo()">Search</BasicButton>
        <div class="tag-container">
            <Search :sug_type="'subject'" v-model="subject" class="tag"></Search>
            <Search :sug_type="'university'" v-model="university" class="tag"></Search>
            <Search :sug_type="'professor'" v-model="professor" class="tag"></Search>
            <Search :sug_type="'course_book'" v-model="book" class="tag"></Search>
        </div>
    </div>
    <div class="Quiz-Container">
      <QuizComponent v-for="quiz in quizzes" 
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
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

.main-tag {
    width: 100%;
}

.tag-container {
    display: flex;
    flex-direction: row;
    width: 100%;
}

.tag {
    flex: 1;
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