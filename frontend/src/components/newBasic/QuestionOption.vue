<script setup lang="ts">
  import { ref } from 'vue';
  
  const props = defineProps<{
    option: string,
    isCorrect: boolean,
  }>()

</script>

<template>
  <div class="option-wrapper">
    <span class="text-wrapper">{{ props.option }}</span>
    <img v-if="props.isCorrect == false" src="@/assets/IncorrectOption.svg" />
    <img v-else src="@/assets/CorrectOption.svg" />
  </div>
</template>

<style scoped>
.option-wrapper {
  max-height: 44px;
  max-width: 274px;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  padding: 8px;
  background-color: var(--background-blueish);
  border-radius: 8px;
}

.text-wrapper {
  font-size: 16px;
  display: flex;
  max-height: 19px;
  align-items: center;
  height: 100%;
}
</style>
