<script setup lang="ts">
import { computed, useTemplateRef } from 'vue';
import PlaySVG from '@/assets/Play.svg'
import { useRouter, type RouteParamsRawGeneric } from 'vue-router';

const router = useRouter()

const props = defineProps({
    variant: { type: String, default: 'white' },
    id: { type: Number, required: true },
    mock: { type: Boolean, default: false },
    name: String,
    author: String,
})


const quizItemRef = useTemplateRef('quiz-item')
const quizIconRef = useTemplateRef('quiz-icon')


const quizId = props.mock ? `mock_${props.id}` : String(props.id)


enum ButtonVariants {
    WHITE = "white",
    BLUEISH = "blueish"
}

const variantClass = computed(() => {
    if (Object.values<string>(ButtonVariants).includes(props.variant)) {
        return props.variant;
    } else {
        return 'blueish';
    }
})

function onClickQuiz(e: MouseEvent) {
    if (!quizItemRef.value) { return }
    if (quizIconRef.value && quizIconRef.value.contains(e.target as Node)){
        router.push(`/quizzes/${quizId}/solving`)
    } else {
        router.push({ name: 'quiz', params: { quizId: quizId } })
    }
}
</script>


<template>
    <div class="quiz-item" ref="quiz-item" :class="variantClass" @click="onClickQuiz">
        <div class="header">
            <div class="name">{{ props.name }}</div>
            <div class="author">by {{ props.author === undefined ? "You" : props.author }}</div>
        </div>
        <div class="icon" ref="quiz-icon">
            <PlaySVG />
        </div>
    </div>
</template>


<style scoped>
.quiz-item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    background-color: var(--background-blueish);
    max-width: 369px;
    width: 100%;
    max-height: 82px;
    height: 100%;
    align-items: center;
    padding: 16px;
    border-radius: 16px;
    transition: 0.2s;
    --icon-stroke: var(--secondary);
    /* --icon-fill: var(--secondary); */
    --icon-stroke-width: 1.5;
}

.header {
    display: flex;
    flex-direction: column;
    justify-content: center;
    text-overflow: ellipsis;
    overflow: hidden;
}

.name {
    font-weight: 700;
    font-size: 24px;
    /* max-height: 29px; */
    overflow: hidden;
    word-wrap: break-word;
    text-overflow: ellipsis;
    text-wrap-mode: nowrap;
}

.icon {
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.author {
    font-size: 16px;
    color: var(--secondary);
    max-height: 19px;
    display: flex;
    align-items: center;
}

.white {
    background-color: var(--white);
}

.quiz-item:hover {
    /* transform: translateY(-4px);
  box-shadow: 0 6px 10px 0 rgba(0,0,0,0.1); */
    background-color: var(--background-light);
}

.icon:hover {
    --icon-fill: var(--primary);
    --icon-stroke: var(--primary);
    /* background: radial-gradient(circle,rgba(78, 222, 138, 1) 0%, rgba(87, 199, 133, 0) 75%); */
}
</style>
