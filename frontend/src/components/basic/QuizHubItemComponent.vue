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
    subject: String,
    university: String,
    professor: String,
    course_book: String,
    rating: {type: Number, required: true},
    description: String
})

const styleRatingObject = () => {
        const rating = props.rating
        if (rating == 0 || rating == null) return "#757575"
        else if (rating < 2.5) return "#AF0000"
        else if (rating >= 2.5 && rating < 3.8) return "#ACAF00"
        else return "#00AF14"
    }
   
const refact_rating = (value: number|null) => {
    if (value) {
        return Number.isInteger(value)? `${value}.0` : `${value}`
    } else  return '0.0'
}

const quizItemRef = useTemplateRef('quiz-item')
// const quizIconRef = useTemplateRef('quiz-icon')


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
    router.push(`/quizhub/${quizId}`)
    // if (quizIconRef.value && quizIconRef.value.contains(e.target as Node)){
    //     router.push(`/quizhub/${quizId}`)
    // } else {
    //     router.push(`/quizhub/${quizId}`)
    // }
}
</script>


<template>
    <div class="quiz-item" ref="quiz-item" :class="variantClass" @click="onClickQuiz">
        <div class="tags">
            <div class="tag"> {{ props.subject }} </div>
            <div class="tag"> {{ props.university }} </div>
            <div class="tag"> {{ props.professor }} </div>
            <div class="tag"> {{ props.course_book }} </div>
        </div>
        <div class="header">
            <div class="author-name">
                <div class="name">{{ props.name }}</div>
                <div class="author">by {{ props.author === undefined ? "You" : props.author }}</div>
            </div>
            <h1 class="rating-title" :style="{color: styleRatingObject()}">{{refact_rating(props.rating)}}</h1>
        </div>
        <span class="description">{{ props.description}}</span>
        <!-- <div class="icon" ref="quiz-icon">
            <PlaySVG />
        </div> -->
    </div>
</template>


<style scoped>

.description {
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    max-width: calc(100% - 8px);
    color: var(--secondary);
    font-size: 14px;
}

.tags {
    display: flex;
    width: 100%;
    height: fit-content;
    flex-wrap: nowrap;
    gap: 8px;
}

.tag {
    font-size: 12px;
    color: var(--primary-dimm);
    background-color: var(--primary-light);
    border: 1px solid var(--primary);
    outline: 1px solid var(--primary-light);
    border-radius: 16px;
    padding: 4px 12px 4px 12px;

    max-width: 25%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.tag:hover {
    max-width: none;
}

.quiz-item {
    display: flex;
    flex-direction: column;
    background-color: var(--background-blueish);
    /* max-width: 369px; */
    width: 100%;
    padding: 16px;
    border-radius: 16px;
    transition: 0.2s;
    --icon-stroke: var(--secondary);
    /* --icon-fill: var(--secondary); */
    --icon-stroke-width: 1.5;
    gap: 16px;
}

.header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
}

.author-name {
    display: flex;
    gap: 8px;
    max-width: 80%;
}

.name {
    font-weight: 700;
    font-size: 24px;
    /* max-height: 29px; */
    overflow: hidden;
    word-wrap: break-word;
    text-overflow: ellipsis;
    text-wrap-mode: nowrap;
    max-width: 75%;
}

.name:hover {
    text-wrap-mode: wrap;
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
    position: relative;
    bottom: -16.5px;
    left: 5px;
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
