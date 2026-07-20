<script setup lang="ts">
import { ref, onBeforeMount, computed, watch} from "vue";
import QuizHubItemComponent from "@/components/basic/QuizHubItemComponent.vue";
import Search from "@/components/basic/Search.vue";
import SearchSVG from "@/assets/Search.svg"
import type { QuizPartialHubItem } from "@/types";
import { useNewQuizzesStore } from "@/stores/new-quizzes";
import BasicButton from "@/components/basic/BasicButton.vue";

const quizzesStore = useNewQuizzesStore();
const quizzes = ref<QuizPartialHubItem[]>([]);
const isLoading = ref(false)

const book = ref('')
const professor = ref('')
const university = ref('')
const subject = ref('')
const main = ref('')

const pageNum = ref(1)
const totalMatches = ref(null)
const limit = ref(12)
watch(pageNum, async () => {
  fetchInfo()
})

const addParam = (params: URLSearchParams, key: string, value: string) => {
    const trimmed = value.trim()
    if (trimmed !== "") {
        params.append(key, trimmed)
    }
}

function unfocusInput() {
  const input = document.activeElement as HTMLInputElement | null
  input?.blur()
}
async function fetchInfo() {
try {
  const params = new URLSearchParams()
  addParam(params, "subject", subject.value)
  addParam(params, "professor", professor.value)
  addParam(params, "course_book", book.value)
  addParam(params, "university", university.value)
  addParam(params, "search_main", main.value)

    params.append("offset", pageNum.value.toString())
    params.append("limit", limit.value.toString())
    const address = import.meta.env.DEV ? "http://localhost:8067": import.meta.env.VITE_HUB_URL_DEV
    isLoading.value = true 
    const response = await fetch(`${address}/hub/packs/?${params.toString()}`,
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
    quizzesStore.hubQuizzes = data.packs
    totalMatches.value = data.total
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
        <div class="search-wrapper">
          <Search :sug_type="'name'" v-model="main" class="main-tag" @search="fetchInfo"></Search>
          <span class="search-icon" @click="fetchInfo"><SearchSVG/></span>
        </div>
        <div class="tag-container">
            <Search :sug_type="'subject'" v-model="subject" class="tag" @search="unfocusInput"></Search>
            <Search :sug_type="'university'" v-model="university" class="tag"></Search>
            <Search :sug_type="'professor'" v-model="professor" class="tag"></Search>
            <Search :sug_type="'course_book'" v-model="book" class="tag"></Search>
        </div>
    </div>
    <div class="Quiz-Container">
      <QuizHubItemComponent class="quiz-item" v-if="quizzes.length > 0"  v-for="quiz in quizzes" 
        :key="quiz.id" 
        :id="quiz.id"
        :name="quiz.name"
        :author="quiz.author.name"
        :description="quiz.description"
        :course_book="quiz.course_book"
        :professor="quiz.professor"
        :subject="quiz.subject"
        :rating="quiz.rating ? quiz.rating : 0"
        :university="quiz.university"/>
        
      <div v-else>Sorry, we can't find such quizzes</div>
    </div>
    <div class="buttons-container"v-if="totalMatches">
      <BasicButton v-if="pageNum > 1" @click="pageNum--">previous</BasicButton>
      <span>{{ pageNum }} out of {{ Math.ceil(totalMatches / limit)}}</span>
      <BasicButton v-if="pageNum < Math.ceil(totalMatches / limit)" @click="pageNum++">next</BasicButton>
    </div>
  </div>
  <div v-else>Loading...</div>
</template>

<style scoped>

.buttons-container {
  display: flex;
  justify-content: center;
}

.main-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 64px;
}

.search-wrapper {
    align-self: stretch;
    position: relative; 
}

.search-icon {
    position: absolute;
    right: 16px;   
    top: 50%;      
    transform: translateY(-50%);
    width: 16px;
    z-index: 2;
    cursor: pointer;
    --icon-stroke: var(--secondary);
}


.top-container { 
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: space-between;
}


.main-tag > *{
    width: 100%;
}

.tag-container {
    display: flex;
    flex-direction: row;
    width: 100%;
    gap: 8px
}

.tag {
  flex: 1;
}



.Quiz-Container {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  cursor: pointer;
}

.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-icon {
    -webkit-animation:spin 2s linear infinite;
    -moz-animation:spin 2s linear infinite;
    animation:spin 2s linear infinite;
}

@-moz-keyframes spin { 100% { -moz-transform: rotate(360deg); } }
@-webkit-keyframes spin { 100% { -webkit-transform: rotate(360deg); } }
@keyframes spin { 100% { -webkit-transform: rotate(360deg); transform:rotate(360deg); } }


</style>