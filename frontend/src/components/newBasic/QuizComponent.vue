<script setup lang="ts">
import { computed } from 'vue';
const props = defineProps<{
    variant: {type: String, default: 'blueish'},
    name: string,
    author?: string,
}>()


enum ButtonVariants {
  WHITE = "white",
  BLUEISH = "blueish"
}

const variantClass = computed(() => {
  if (!Object.values<string>(ButtonVariants).includes(props.variant)) {
    return 'blueish';
  }
  return props.variant;
})

</script>
<template>
    <div class="quiz-item" :class="variantClass">
        <div class="header">
            <div class="name">{{ props.name }}</div>
            <div class="author">by {{ props.author === undefined ? "You" : props.author }}</div>
        </div>
        <img src="@/assets/StartQuiz.svg" alt="Start Quiz">
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
  max-heigh: 82px;
  height: 100%;
  align-items: center;
  padding: 16px;
  border-radius: 16px;
}

.header {
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.name {
    font-weight: 700;
    font-size: 24px;
    max-height: 29px;
    align-items: center;
    display: flex;
}

.author {
    font-size: 16px;
    color: var(--secondary-dim);
    max-height: 19px;
    display: flex;
    align-items: center;
}

.white {
  background-color: var(--white);
}

</style>
