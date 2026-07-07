<script setup lang="ts">
import type QuizItem from "@/types"
import type { ComputedRef } from "vue";
import { ref, onBeforeMount, onUnmounted, computed, watch} from "vue";
import QuizComponent from "@/components/newBasic/QuizComponent.vue";
import { useRouter } from "vue-router";
import { useQuizzesStore } from "@/stores/quizzes";
import BasicButton from "@/components/newBasic/BasicButton.vue";
import BasicInput from "./BasicInput.vue";
const emit = defineEmits<{
    (event: 'search'): void
}>()
const model = defineModel<string>({ default: "" })
const timer = ref<number | null>(null)
watch(model, (value) => {
    if (timer.value !== null) clearTimeout(timer.value)

    timer.value = window.setTimeout(() => {
        if (value?.trim() !== ""){
            fetchInfo()
        }
    }, 300)
})
const suggestions = ref([])
const props = defineProps<({
    sug_type: string
})>()
async function fetchInfo() {
try {
    if (!model.value) return
    const address = import.meta.env.DEV ? "http://localhost:8000": import.meta.env.VITE_HUB_URL_DEV
    const response = await fetch(`${address}/hub/suggestions?` + new URLSearchParams({
        sug_type: props.sug_type,
        q: model.value,
    }).toString(), {
    method: 'GET'
    })
    if (!response.ok) {
        throw new Error(`Ошибка сети: ${response.status}`)
    }
    const data = await response.json()
    suggestions.value = data
    console.log(suggestions.value)
} catch (error) {
    console.error('Не удалось загрузить квизы:', error) 
}

}

</script>


<template>
    <div>
        <BasicInput :placeholder="`Any ${sug_type.replace('_', ' ')}`" v-model="model" @keydown.enter="emit('search')" class="input-search"></BasicInput>
        <div v-for="suggestion, index in suggestions" :key="index" @click="model = suggestion">{{ suggestion }}</div>
    </div>
</template>

<style scoped>
:deep(.input-search) {
    color: var(--color-text) !important;
    width: 100%;
    height: 35px;
    border-radius: 12px;
}
</style>