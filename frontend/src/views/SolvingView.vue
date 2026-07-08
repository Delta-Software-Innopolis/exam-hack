<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useNewQuizzesStore } from '@/stores/new-quizzes';
import { onBeforeMount, onUnmounted, type ComputedRef } from 'vue';
import BasicButton from '@/components/newBasic/BasicButton.vue';
import type { Card } from '@/types'
import { ref, computed } from 'vue';
import router from '@/router';

import LeftArrowSVG from '@/assets/LeftArrow.svg'
import RightArrowSVG from '@/assets/RightArrow.svg'
import HintSVG from '@/assets/Hint.svg'

const route = useRoute();
const quizzesStore = useNewQuizzesStore()
const quiz = ref(quizzesStore.getMyQuizInfo(route.params.quizId))
const questionNum = ref(0)
const card = computed(() => quiz.value ? quiz.value.cards[questionNum.value] : undefined) as ComputedRef<Card>
const lastClicked = ref<number|null>(null)
const isHintClicked = ref(false)

console.log("CARD:", card)

const progressWidth = computed(()=> {
    const total = (quiz.value ? quiz.value.cards.length : 1) || 1
    const current = questionNum.value + 1
    return `${current/total * 100}%` 
})

const isDisabled = ref(false)
const styles = ref<string[]>(new Array(4).fill('default'))


function nextCard(){
    lastClicked.value = null
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
    questionNum.value--;
    styles.value = new Array(4).fill('default')
}

function checkAnswer(index:number) {
    lastClicked.value = index
    const doesInclude = card?.value.correct.includes(index)
    styles.value[index] = doesInclude ? 'green' : 'red'
    if (doesInclude) {
        styles.value[index] = "green"
        isDisabled.value = true
        setTimeout(()=> {nextCard(); isDisabled.value=false}, 1000)
    }
    else {
        styles.value[index] = "red"
        for (let ind of card.value.correct) {

            styles.value[ind] = "green"
        }
    }

}
</script>
<template>
    <div class="container" v-if="quiz">
        <div class="main-container">
            <div class="progress-bar">
                <div class="card-num">{{ questionNum + 1}} / {{ quiz.cards.length }}</div>
                <div class="progress">
                    <div class="current-progress":style="{ width: progressWidth}"></div>
                </div>
            </div>
            <div class="question">
                <div class="title">{{ card?.question }}</div>
                <div class="option-container">
                <BasicButton 
                class="option"
                v-for="(option, index) in card?.options"
                        :key="index"
                        :variant="styles[index]"
                        @click="checkAnswer(index)"
                        :class="{'clicked': lastClicked === index}"
                        >Option {{`${index}: ${option} `}}</BasicButton>
                </div>
            </div>
            <div class="arrow-container">
                <button  v-if="questionNum != 0" @click="prevCard" class="arrow left-arrow" :disabled="isDisabled"><LeftArrowSVG/></button>
                <BasicButton title="Hint from AI" @click="isHintClicked = !isHintClicked" variant="ai" class="hint-button"><HintSVG/></BasicButton>
                <button v-if="questionNum != quiz.cards.length-1" @click="nextCard" class="arrow right-arrow" :disabled="isDisabled"><RightArrowSVG/></button>
            </div>
            <div class="hint-wrapper" v-if="isHintClicked"><div>{{ card?.hint }}</div></div>
        </div>
    </div>
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
    height: 100%;
    width: 482px;
    box-sizing: border-box;
    padding: 32px 16px;
    gap: 16px;
    align-self: center;
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
    background-color: #AFF4C6;
    height: 100%;
    border-radius: 8px;

}

.question {
    display: flex;
    flex-direction: column;
    gap: 32px;
    width: 100%;

}

.title {
    font-size: 20px;
    font-weight: bold;
    height: fit-content;
    width: 100%;
    padding: 16px;
    margin: 0;
}

.option-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
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

.arrow-container {
    width: 100%;
    position: relative;
    display: flex;
    justify-content: space-evenly;
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
    left: 0; 
    position: absolute;
}

.right-arrow {
    right: 0;
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
    text: center;
}

.hint-button {
    width: 48px;
    height: 48px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0;
}

</style>

