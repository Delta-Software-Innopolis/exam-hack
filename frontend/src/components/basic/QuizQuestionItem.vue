<script setup lang="ts">
import PencilSVG from "@/assets/Pencil.svg"


type Variant = 'view' | 'edit';

const props = defineProps<{
    index: number,
    question: string,
    variant: Variant
}>();
</script>

<template>
    <div class="question-wrapper">
        <div class="text-wrapper">
            <span class="number-wrapper">{{ props.index }}.</span>
            <span class="question-text">{{ props.question }}</span>
        </div>
        <PencilSVG class="pencil-icon" v-if="variant === 'edit'"/>
    </div>
</template>

<style scoped>
.question-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    gap: 12px;
    background-color: var(--background-blueish);
    border-radius: 16px;
    padding: 8px 16px;
    cursor: pointer;
    min-width: 0;
}

.question-wrapper:hover {
    background-color: var(--background-light);
}

.text-wrapper {
    display: flex;
    flex-direction: row;
    gap: 8px;
    align-items: center;
    min-width: 0;
    flex: 1 1 auto;
    overflow: hidden;
}

.number-wrapper {
    flex-shrink: 0;
    color: var(--secondary-dimm);
}

.question-text {
    color: var(--secondary-dimm);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    min-width: 0;
}

.pencil-icon {
    flex-shrink: 0;
    --icon-stroke: var(--secondary);
    --icon-width: 16px;
    --icon-height: 16px;
}
</style>
