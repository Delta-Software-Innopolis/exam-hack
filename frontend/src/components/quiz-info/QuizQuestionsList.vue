<script setup lang="ts">
import type { Card } from '@/types';
import QuizQuestionItem from '../basic/QuizQuestionItem.vue';

type Variant = 'view' | 'edit';

const props = defineProps<{
    cards: Card[],
    variant: Variant
}>();

const emit = defineEmits<{
    clickQuestionItem: [index: number]
}>();
</script>


<template>
    <div class="quiz-questions-list">
        <QuizQuestionItem
            v-for="[i, q] in cards.entries()"
            :index="i+1"
            :question="q.question"
            :variant="variant"
            @click="emit('clickQuestionItem', i)" 
        />
    </div>
</template>


<style scoped>
.quiz-questions-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    background-color: var(--white);
    padding: 16px;
    border-radius: 16px;
    height: 100%;
    overflow-y: auto;
}
</style>
