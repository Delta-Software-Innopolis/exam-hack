<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useNewQuizzesStore } from '@/stores/new-quizzes';
import { onBeforeMount, onMounted, onUnmounted, type ComputedRef } from 'vue';
import BasicButton from '@/components/basic/BasicButton.vue';
import ModalWindow from '@/components/basic/ModalWindow.vue';
import type { Card } from '@/types'
import { ref, computed, useTemplateRef, Transition } from 'vue';
import router from '@/router';

import LeftArrowSVG from '@/assets/LeftArrow.svg'
import RightArrowSVG from '@/assets/RightArrow.svg'
import HintSVG from '@/assets/Hint.svg'
import UnknownView from './UnknownView.vue';

const route = useRoute();
const quizzesStore = useNewQuizzesStore()
const quiz = ref(quizzesStore.getMyQuizInfo(route.params.quizId))
const knownQuiz = computed(()=>quiz.value.id !== -1);
const questionNum = ref(0)
const card = computed(() => quiz.value ? quiz.value.cards[questionNum.value] : undefined) as ComputedRef<Card>
const lastClicked = ref<number|null>(null)
const isHintClicked = ref(false)
const modalHint = useTemplateRef('modalHint')

console.log("CARD:", card)

const progressWidth = computed(()=> {
    const total = (quiz.value ? quiz.value.cards.length : 1) || 1
    const current = questionNum.value + 1
    return `${current/total * 100}%` 
})

const questionAnswered = ref(false)
const styles = ref<string[]>(new Array(4).fill('default'))

function nextCard(){
    lastClicked.value = null
    questionAnswered.value = false
    isHintClicked.value = false
    if (questionNum.value < (quiz.value ? quiz.value.cards.length : 1) - 1) {
        questionNum.value++;
        styles.value = new Array(4).fill('default')
        return
    }
    router.push({name: "quizzes"})

}

function prevCard(){
    lastClicked.value = null
    questionAnswered.value = false
    questionNum.value--;
    styles.value = new Array(4).fill('default')
}

function checkAnswer(index:number){
    lastClicked.value = index
    questionAnswered.value = true
    const doesInclude = card?.value.correct.includes(index)
    for (let ind of card?.value.options?.keys()) {
        if (card.value.correct.includes(ind)) {
            styles.value[ind] = "green"
        } else {
            styles.value[ind] = "quizred"
        }
    }
}
</script>


<template>
    <div class="container" v-if="knownQuiz">
        <div class="main-container">
            <div class="content">
                <div class="progress-bar">
                    <div class="card-num">{{ questionNum + 1}} / {{ quiz.cards.length }}</div>
                    <div class="progress">
                        <div class="current-progress":style="{ width: progressWidth}"></div>
                    </div>
                </div>
                <Transition name="question" mode="out-in">
                    <div class="options" :key="questionNum">
                        <div class="question">
                            <div class="title">{{ card?.question }}</div>
                            <div class="option-container">
                                <BasicButton 
                                class="option"
                                v-for="(option, index) in card?.options"
                                    :key="index"
                                    :variant="styles[index]"
                                    :class="{ answered: questionAnswered }"
                                    @click="checkAnswer(index)"
                                >
                                <span class="option-index">{{ index }}.</span>{{ option }}
                                </BasicButton>
                            </div>
                        </div>
                        <div class="arrow-container">
                            <button  v-if="questionNum != 0" @click="prevCard" class="arrow left-arrow"><LeftArrowSVG/></button>
                            <BasicButton title="Hint from AI" @click="modalHint?.open()" variant="ai" class="hint-button"><HintSVG/></BasicButton>
                            <button v-if="questionAnswered" @click="nextCard" class="arrow right-arrow"><RightArrowSVG/></button>
                        </div>
                    </div>
                </Transition>
            </div>
            <ModalWindow ref="modalHint">
                {{ card?.hint }}
            </ModalWindow>
        </div>
    </div>
    <UnknownView v-else />
</template>

<style scoped>
.container {
    display: flex;
    justify-content: center;
}

.main-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    width: 503px;
    box-sizing: border-box;
    padding: 64px 16px;
    gap: 16px;
    align-self: center;
}

.progress-bar-wrapper {
    padding: 16px;
}

.progress-bar {
    padding: 16px;
    width: 100%;
    display: flex;
    gap: 4px;
    align-items: center;
}

.card-num {
    white-space: nowrap;
    font-variant-numeric: tabular-nums;
}

.progress {
    border-radius: 8px;
    background-color: var(--color-background-secondary);
    width: 100%;
    height: 16px;
}

.current-progress {
    background-color: #00B093;
    height: 100%;
    border-radius: 8px;
    transition: width 350ms ease-out;
}

.content {
    width: 100%;
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
    padding: 16px;
}

.options {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.question {
    display: flex;
    flex-direction: column;
    gap: 32px;
    width: 100%;
    flex: 1;
    min-height: 0;
}

.title {
    font-size: 20px;
    font-weight: bold;
    height: fit-content;
    width: 100%;
    padding: 16px;
    margin: 0;
    flex-shrink: 0;
}

.option-container {
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-height: 0px;
    scrollbar-gutter: stable;
    padding-left: 16px;
    padding-right: 16px;
}


.option {
    width: 100%;
    padding: 16px;
    font-size: 18px;
    color: var(--color-text);
    border: none;
    justify-content: left;
}

.option.secondary {
    background-color: var(--white);
}

.option.secondary:hover {
    background-color: var(--background-light);
}

.option-index {
    margin-left: 3px;
    margin-right: 6px;
    color: var(--background-dimm);
}

.answered {
    pointer-events: none;
}

.arrow-container {
    width: 100%;
    position: relative;
    display: flex;
    justify-content: space-evenly;
    flex-shrink: 0;
    padding: 16px;
    padding-top: 64px;
}

.arrow {
    width: 48px;
    height: 48px;
    background-color: var(--white);
    border-radius: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 0;
    cursor: pointer;
}

.arrow:hover {
    background-color: var(--background-light);
}

.left-arrow {
    left: 16px; 
    position: absolute;
}

.right-arrow {
    right: 16px;
    position: absolute;
}

.arrow > img {
    object-fit: contain;
    display: block;
    width: 32px;
    height: 32px;
}

.hint-wrapper {
    width: 100%;
    background-color: white;
    font-size: 18px;
    border-radius: 16px;
    justify-content: left;
    align-items: center;
    padding: 16px;
}

.hint-button {
    width: 48px;
    height: 48px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0;
}

.hint-button:hover {
}

.question-enter-active,
.question-leave-active {
    transition: all .25s ease;
}

.question-enter-from {
    opacity: 0;
    transform: translateX(30px);
}

.question-leave-to {
    opacity: 0;
    transform: translateX(-30px);
}
</style>
