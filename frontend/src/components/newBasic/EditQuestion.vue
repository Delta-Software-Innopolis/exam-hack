<script setup lang="ts">
  const props = defineProps<{
    index: number,
    question: string,
  }>();
</script>

<template>
  <div class="question-wrapper">
    <div class="text-wrapper">
      <span class="number-wrapper">{{ props.index }}.</span>
      <span>{{ props.question }}</span>
    </div>
    <img src="@/assets/EditQuestion.svg" alt="edit question" />
  </div>
</template>

<style scoped>
.question-wrapper {
  display: flex;
  justify-content: space-between;
  max-width: 341px;
  width: 100%;
  height: 46px;
  background-color: var(--background-blueish);
  align-items: center;
  overflow: hidden;
  border-radius: 16px;
  padding: 16px;
}

img {
  width: 20px;
  height: 20px;
}

.text-wrapper {
  display: flex;
  flex-direction: row;
  gap: 8px;
  color: var(--secondary-dimm);
}

</style>
