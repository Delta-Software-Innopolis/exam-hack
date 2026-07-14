<script setup lang="ts">
import { computed } from 'vue';

enum ButtonVariants {
  PRIMARY = 'primary',
  SECONDARY = 'secondary',
  TERITARY = 'teritary',
  GREEN = 'green',
  RED = 'red',
  AI = 'ai',
  QUIZRED = 'quizred'
}

const props = defineProps({
  variant: {type: String, default: 'secondary'},
})

const variantClass = computed(() => {
  if (!Object.values<string>(ButtonVariants).includes(props.variant)) {
    return 'secondary';
  }
  return props.variant;
})

</script>


<template>
  <button v-bind="$attrs" :class="variantClass">
    <slot></slot>
  </button>
</template>


<style scoped>
button {
  display: flex;
  justify-content: center;
  align-items: center;
  transition: 0.2s;
  border: 0;

  padding: 16px;
  padding-top: 10px;
  padding-bottom: 10px;
  
  font-size: 16px;

  border-radius: 16px;
  cursor: pointer;
}

.primary {
  background-color: var(--primary);
  color: var(--white);
}

.primary:hover {
  background-color: var(--primary-dimm);
}

.primary:active {
  background-color: var(--primary-dark);
}

.primary:focus-visible {
  outline: 2px solid var(--primary);
  outline-offset: 2px;
}

.primary:disabled {
  background-color: var(--secondary);
}

.secondary {
  background-color: transparent;
  border: 1px solid var(--secondary-dimm);
  color: var(--secondary-dimm);
}

.secondary:hover {
  background-color: var(--background-light);
}

.secondary:active {
  background-color: var(--background-dimm);
}

.secondary:focus-visible {
  outline: 2px solid var(--primary);
  color: var(--primary);
  border: 0;
}

.secondary:disabled {
  border: 1px dashed var(--secondary-dimm);
  background-color: var(--background-light);
}

.teritary {
  border: 0;
  background-color: transparent;
  color: var(--secondary-dimm);
}

.teritary:not(:disabled):hover {
  font-weight: 700;
}

.teritary:not(:disabled):active {
  font-weight: 700;
  color: var(--primary-dimm);
}

.teritary:focus-visible {
  text-decoration: underline;
  outline: 0;
  color: var(--primary);
  text-underline-offset: 3px;
}

.teritary:disabled {
  color: var(--secondary);
}

.ai {
  background-color: var(--white);
  font-weight: 700;
  border: 2px solid transparent;
  border-radius: 16px;
  background:
    linear-gradient(var(--white), var(--white)) padding-box,
    linear-gradient(to bottom, #68F2FF 40%, #FF61ED) border-box;
}

.ai:disabled {
  background-color: var(--white);
  font-weight: 700;
  border: 2px solid transparent;
  border-radius: 16px;
  background:
    linear-gradient(var(--white), var(--white)) padding-box,
    linear-gradient(to bottom, #9ef7ff 40%, #ffacf5) border-box;
  cursor: default;
}

.green {
  background-color: var(--quiz-green)
}

.green:hover {
  background-color: var(--quiz-green-dimm);
}

.red {
  background-color: var(--raddish);
  color: var(--white);
  /* font-weight: bold; */
}

.red:hover {
  background-color: var(--raddish-dimm);
}

.red:active {
  background-color: var(--raddish-dark);
}

.quizred {
    background-color: var(--quiz-red)
}

/* in order to make .ai:disabled i think we should use svg image of the disabled button,
   since it's quite difficult to implement it using clean css */

</style>
