<script setup lang="ts">
import { ref, watch, onUnmounted, useTemplateRef, type Ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import BasicButton from '@/components/basic/BasicButton.vue';
import type { Card, CardType, QuizItem, QuizHubItem } from '@/types';
import ModalQuestionView from '@/components/modals/ModalQuestionView.vue';
import useNetworkManager, { HUB_URL} from '@/network';
import QuizOption from '@/components/basic/QuizOption.vue';
import QuizQuestionsList from '@/components/quiz/QuizQuestionsList.vue';
import PlusButton from '@/components/buttons/PlusButton.vue';
import UnknownView from './UnknownView.vue';
import ModalWindow from '@/components/basic/ModalWindow.vue';
import PensilSVG from '@/assets/Pencil.svg'
import { debounce } from '@/utils.ts';

const route = useRoute()
const router = useRouter()
const networkManager = useNetworkManager()
const quiz = ref<QuizHubItem|null>(null)
const refact_rating = (value: number|null) => {
    if (value) {
        return Number.isInteger(value)? `${value}.0` : `${value}`
    } else  return '0.0'
}
const showPensil = ref(false)
onMounted(async() => {
    quiz.value = await getQuiz()
    console.log(quiz.value)
})

async function getQuiz() {
    const quizId = route.params.quizId
    if (quizId === undefined) {
        console.error('Missing quiz id in route params')
        return
    }

    try {
        const response = await networkManager.fetch(
            new URL(`/hub/packs/${quizId}`, HUB_URL),
            {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        },
        )

        if (!response.ok) {
            throw new Error(`Failed to get quiz: ${response.status}`)
        }
        const result = await response.json()
        return result
    } catch (error) {
        console.error('Could not add quiz to collection:', error)
    }
}

async function addToCollection() {
    const quizId = route.params.quizId
    if (quizId === undefined) {
        console.error('Missing quiz id in route params')
        return
    }

    try {
        const response = await networkManager.fetch(
            new URL(`/hub/packs/${quizId}`, HUB_URL),
            {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        },
        )

        if (!response.ok) {
            throw new Error(`Failed to add quiz: ${response.status}`)
        }

        console.log('Quiz added to collection', quizId)
        router.push("/quizzes")
    } catch (error) {
        console.error('Could not add quiz to collection:', error)
    }
}

const debounceRate = debounce(rateQuiz, 300)
async function rateQuiz(rating: number){
    const quizId = route.params.quizId
    if (quizId === undefined) {
        console.error('Missing quiz id in route params')
        return
    }
    try {
        const response = await networkManager.fetch(
            new URL(`/hub/packs/${quizId}/ratings`, HUB_URL),
            {
            method: quiz.value?.your_score ? "PUT" : "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "score": rating
            })
        },
        )

        if (!response.ok) {
            const data = await response.json()
            const errMsg = data.detail || "server error"
            alert(errMsg)
            throw new Error(`Failed to rate quiz: ${errMsg}`)
        }
        const data = await response.json()
        const newScore = data.new_score as number
        if (quiz.value) {
            quiz.value.rating = newScore
            quiz.value.your_score = rating 
        }

        console.log('Quiz successfully rated', quizId)
    }catch (error) {
        console.error('Could not rate quiz:', error)
    }finally {
        modalRatingView.value?.close()
    }
}

function notImplemented() {
    alert('Thank you for trying!\nThis will be implemented later 🫡')
}

const activeQuestionId = ref(-1);
const modalQuestionView = useTemplateRef('modal-question-view');
const modalRatingView = useTemplateRef('modal-rating-view')
const styleRatingObject = computed(() => {
    return (type: number) => {
        const rating = type == 0 ? quiz.value?.rating : quiz.value?.your_score
        if (rating == null) return "#757575"
        else if (rating < 2.5) return "#AF0000"
        else if (rating >= 2.5 && rating < 3.8) return "#ACAF00"
        else return "#00AF14"
    } 
})

const ratingVarStyle = [
    "#AF0000",
    "#AF3400",
    "#AF7A00",
    "#ACAF00",
    "#00AF14"
]
</script>

<template>
    <div class="main-container" v-if="quiz">
        <ModalQuestionView ref="modal-question-view"/>
        <ModalWindow ref="modal-rating-view">
            <div class="rating-container">
                <h1>Rate this quiz!</h1>
                <div class="rating-variants">
                    <div 
                    class="rating-var"
                    v-for="value, index in [1, 2, 3, 4, 5]"
                    :style="{color: ratingVarStyle[index]}"
                    @click="debounceRate(value)"
                    >
                    {{ value }}
                </div>
            </div>
        </div>
    </ModalWindow>
    <div class="left-side">
        <div class="title">
            <div class="name-rating">
                <h1>{{ quiz.name || 'Unknown Quiz' }} </h1>
                <h1 class="rating-title" :style="{color: styleRatingObject(0)}">{{refact_rating(quiz.rating)}}</h1>
            </div>
            <div class="author-rating">
                <span class="author">
                    by <a href="#">{{ quiz.author.name || 'Someone'}}</a>
                </span>
                <div class="your-rating" v-if="quiz.your_score" @mouseenter="showPensil = !showPensil" @mouseleave="showPensil = !showPensil" @click="modalRatingView?.open()">
                    Your rating
                    <span class="rating-info" :style="{backgroundColor: styleRatingObject(1)}">{{ refact_rating(quiz.your_score) }}</span>
                    <Transition name="pensil">
                        <PensilSVG v-if="showPensil"></PensilSVG>
                    </Transition>
                </div>
                <BasicButton class="rating-button" v-else @click="modalRatingView?.open()">Add a rating</BasicButton> 
                    
                </div>
            </div>
            <div class="wrappre">
                <div class="tags">
                    <div class="tag"> {{ quiz.subject }} </div>
                    <div class="tag"> {{ quiz.university }} </div>
                    <div class="tag"> {{ quiz.professor }} </div>
                    <div class="tag"> {{ quiz.course_book }} </div>
                </div>
                <div class="description">
                    {{ quiz?.description || "This quiz has no description..." }}
                </div>
                <div class="actions">
                    <div class="top-buttons">
                        <PlusButton variant="primary" @click="addToCollection()">Add to collection</PlusButton>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-side">
            <div class="top-action-bar">
                <h2>Questions</h2>
            </div>
            <QuizQuestionsList :cards="quiz.cards" variant="view" 
                @click-question-item="(idx) => modalQuestionView?.open(quiz?.cards[idx])"
            />
        </div>
    </div>
    <UnknownView v-else />
</template>


<style scoped>
.wrappre {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.author-rating {
    display: flex;
    gap: 30px;
    justify-content: space-between;
}

.name-rating {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pensil-enter-active,
.pensil-leave-active {
    transition: opacity 0.2s ease-in-out;
}

.pensil-enter-from,
.pensil-leave-to {
    opacity: 0;
}

.your-rating {
    display: flex;
    gap: 3px;
    justify-content: center;
    align-items: center;
}

.rating-info {
    font-weight: bold;
    color: white;
    border-radius: 16px;
    text-align: center;
    padding: 0 8px;
}

.rating-button {
    padding: 4px 10px;
    border-radius: 10px;
}

.rating-container {
    width: 254px;
    background-color: var(--color-background-secondary);
    padding: 16px;
    border-radius: 16px;

    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
}

.rating-variants {
    display: flex;
    gap: 8px;
    width: fit-content;
    height: fit-content;
}

.rating-var {
    background-color: var(--background-blueish);
    border-radius: 8px;
    height: 38px;
    width: 38px;

    display: flex;
    align-items: center;
    justify-content: center;

    font-size: 16px;
    font-weight: bold;
}

.rating-var:hover {
    cursor: pointer;
}
.tags {
    display: flex;
    width: 100%;
    height: fit-content;
    flex-wrap: wrap;
    gap: 8px;
}

.tag {
    font-size: 12px;
    color: var(--primary-dimm);
    display: flex;
    justify-content: center;
    width: fit-content;
    background-color: var(--primary-light);
    border: 1px solid var(--primary);
    outline: 1px solid var(--primary-light);
    border-radius: 16px;
    padding: 4px 12px 4px 12px;
}

.main-container {
    padding: 64px;
    gap: 32px;
    display: flex;
    flex-wrap: wrap;
}

.question-edit-window {
    background-color: var(--white);
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    border-radius: 16px;
    min-width: 26em;

    --icon-width: 12px;
    --icon-height: 12px;
}

.question-edit-window .top-line {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: top;
}

.question-edit-window input {
    border-radius: 8px !important;
}

.question-edit-window h3, .question-edit-window h4 {
    font-weight: bold;
    font-size: 16px;
}

.question-edit-window h4 {
    color: var(--secondary)
}

.options-wrapper {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 8px;
}

.options-wrapper svg {
    border: 1px solid black;
    border-radius: 4px;
    background-color: var(--white);
    cursor: pointer;
}

.option-item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    background-color: var(--background-blueish);
    padding: 8px;
    border-radius: 8px;
}

.option-item input {
    width: 100%;
    background: none;
    border: none;
    padding-left: 4px;
}

.option-item input:focus{
    outline: none;
    border: none;
}

.option-cross {
    --icon-stroke: var(--raddish);
    padding: 5px;
    --icon-stroke-width: 2.5px;
    --icon-width: 24px;
    --icon-height: 24px;
}

.option-check {
    --icon-stroke: var(--primary);
    padding: 1px;
    --icon-stroke-width: 2.2px;
    --icon-width: 24px;
    --icon-height: 24px;
}

.question-edit-window .top-line svg {
    cursor: pointer;
}

.overlay {
  z-index: 999;
  box-sizing: border-box;
  position: fixed;
  top: 0;
  left: 0;
  padding: 0;
  background-color: rgba(0,0,0,0.25);
  margin: 0;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  opacity: 1;
  animation: appear 0.2s
}

.hidden-overlay {
    animation: disappear 0.2s;
}

@keyframes disappear {
    0% { opacity: 1; }
    100% { opacity: 0;}
}

@keyframes appear {
    0% { opacity: 0; }
    100% { opacity: 1;}
}

button a {
    text-decoration: none;
    color: inherit;
}

.top-action-bar {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
}

.left-side {
    flex: 0 0 40%;
    min-width: 300px;
    display: flex;
    flex-direction: column;
    gap: 32px;
    height: 100%;
}

.right-side {
    flex: 1 1 50%;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 16px;
    height: 100%;
}

.title {
    padding: 0;
    margin: 0;
}

.description {
    padding: 16px;
    border-radius: 16px;
    background-color: var(--white);
}

.stats-n-actions {
    display: flex;
    flex-direction: row;
    height: 100%;
    width: 100%;
    gap: 16px;
}

.stats {
    display: flex;
    flex-direction: column;
    background-color: white;
    padding: 16px;
    gap: 16px;
    border-radius: 16px;
    width: 100%;
}

.statistics li {
    line-height: 1.25;
}

.completion, .statistics {
    display: flex;
    flex-direction: column;
}

.progress-bar {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 8px;
}

.progress-bar .bar {
    width: 100%;
    height: 1em;
    background-color: var(--background-light);
    border-radius: 0.5em;
}

.actions {
    display: flex;
    flex-direction: row;
    justify-content: end;
    width: fit-content;
    box-sizing: border-box;
    text-wrap-mode: nowrap;
    width: 100%;
}

.top-buttons, .bottom-buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
    box-sizing: border-box;
}

.red-button {
    background-color: var(--raddish);
}
.red-button:hover {
    background-color: var(--raddish-dimm);
}


.questions-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8px;
    background-color: var(--white);
    padding: 16px;
    border-radius: 16px;
    height: 100%;
    overflow-y: auto;
}

.edit-quiz-buttons {
    display: flex;
    gap: 12px;
    align-items: center;
}

</style>
