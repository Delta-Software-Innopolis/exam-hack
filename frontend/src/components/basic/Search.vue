<script setup lang="ts">
import { ref, watch } from "vue";
import BasicInput from "@/components/basic/BasicInput.vue";
import { debounce } from "@/utils";
const emit = defineEmits<{
    (event: 'search'): void
}>()

const model = defineModel<string>({ default: "" })
const suggestions = ref<string[]>([])
const isOpen = ref(false)
const bounceFetch = debounce(fetchInfo, 350)

const props = defineProps<{
    sug_type: string
}>()

watch(model, (value) => {
        if (value?.trim()) {
            bounceFetch()
        } else {
            suggestions.value = []
            isOpen.value = false
        }
})

async function fetchInfo() {
    try {
        if (!model.value) return
        if (suggestions?.value[0] == model.value) {
            return
        }
        const address = import.meta.env.DEV ? "http://localhost:8067" : import.meta.env.VITE_HUB_URL_DEV
        const response = await fetch(
            `${address}/hub/suggestions/?` +
                new URLSearchParams({
                    sug_type: props.sug_type,
                    q: model.value,
                }).toString(),
            { method: "GET" },
        )

        if (!response.ok) {
            throw new Error(`Ошибка сети: ${response.status}`)
        }

        const data = await response.json()
        suggestions.value = data
        isOpen.value = data.length > 0
    } catch (error) {
        console.error("Не удалось загрузить подсказки:", error)
    }
}

function chooseSuggestion(suggestion: string) {
    model.value = suggestion
    isOpen.value = false
    emit("search")
}

function openSuggestions() {
    if (suggestions.value.length > 0) {
        isOpen.value = true
    }
}
</script>

<template>
    <div class="search-shell">
        <BasicInput
            v-model="model"
            class="input-search"
            :class="{'opend': isOpen && props.sug_type !=='name'}"
            :placeholder="`Any ${sug_type.replace('_', ' ')}`"
            @keydown.enter="emit('search')"
            @focus="openSuggestions"
        />

        <Transition name="search-drop" v-if="props.sug_type !== 'name'">
            <div v-if="isOpen && suggestions.length" class="search-shell__overlay">
                <div class="search-shell__suggestions">
                    <button
                        v-for="(suggestion, index) in suggestions"
                        :key="index"
                        type="button"
                        class="search-shell__suggestion"
                        @click="chooseSuggestion(suggestion)"
                    >
                        {{ suggestion }}
                    </button>
                </div>
            </div>
        </Transition>
    </div>
</template>

<style scoped>
.search-shell {
    position: relative;
    width: 100%;
    display: block;
    overflow: visible;
    isolation: isolate;
}

:deep(.opend) {
    border-bottom: none;
    border-bottom-left-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
}

:deep(.input-search) {
    position: relative;
    z-index: 1;
    color: var(--color-text) !important;
    width: 100%;
    border-radius: 12px;
}

.search-shell__overlay {
    position: absolute;
    display: flex;
    flex-direction: column;
    left: 0;
    top: 33px;
    width: 100%;
    z-index: 2;
    background-color: white;
    pointer-events: none;
    border-radius: 12px;
    border: solid 1px;
    border-top: none;
    border-top-left-radius: 0;
    border-top-right-radius: 0;
    border-color: var(--color-text-secondary);
}

.search-input {
    padding: 8px 16px;
    font-size: 14px;
}

.search-shell__suggestions {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding-top: 10px;
    width: 100%;
}

.search-shell__suggestion {
    width: 100%;
    border: 0;
    border-radius: 10px;
    padding: 10px 12px;
    text-align: left;
    color: var(--color-text-secondary);
    cursor: pointer;
    pointer-events: auto;
    background-color: inherit;
}

.search-shell__suggestion:hover {
    background: var(--secondary-dimm);
    color: var(--white);
}

.search-drop-enter-active,
.search-drop-leave-active {
    transition: opacity 0.18s ease, transform 0.18s ease;
}

.search-drop-enter-from,
.search-drop-leave-to {
    opacity: 0;
    transform: translateY(-4px);
}
</style>