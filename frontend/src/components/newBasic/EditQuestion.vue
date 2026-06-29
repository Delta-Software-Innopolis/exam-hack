<script setup lang="ts">
  import PencilSVG from "@/assets/Pencil.svg"
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
    <PencilSVG/>
  </div>
</template>

<style scoped>
.question-wrapper {
  display: flex;
  justify-content: space-between;
  width: 100%;
  background-color: var(--background-blueish);
  align-items: center;
  overflow: hidden;
  box-sizing: border-box;
  border-radius: 16px;
  padding: 16px;
  padding-bottom: 8px;
  padding-top: 8px;

  --icon-stroke: var(--secondary);
  --icon-width: 16px;
  --icon-height: 16px;
  cursor: pointer;
}

.question-wrapper:hover {
  background-color: var(--background-light);
}

.text-wrapper {
  display: flex;
  width: 100%;
  flex-direction: row;
  gap: 8px;
  color: var(--secondary-dimm);
  text-wrap-mode: nowrap;
  text-overflow: ellipsis;
}

</style>
