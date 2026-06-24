<script setup lang="ts">
const props = defineProps<{
    name: string
    author?: string,
    description?: string
}>()
</script>
<template>
    <div class="quiz-item">
        <div class="header">
            <div class="name">{{ props.name }}</div>
            <div class="author">By {{ props.author === undefined ? "You" : props.author }}</div>
        </div>
        <div class="description" v-if="props.description">{{ props.description }}</div>
    </div>
</template>


<style scoped>
.quiz-item {
    display: flex;
    gap: 24px;
    flex-direction: column;
    min-width: 0;
    width: 100%;
    height: 140px;
    overflow: hidden;
    padding: 12px;
    border-radius: 12px;
    background: var(--color-background-secondary);
}

.header {
    display: flex;
    gap: 12px;
    flex-direction: row;
    min-width: 0;
}

.name {
    font-weight: bold;
    font-size: 26px;
}

.author {
    font-size: 26px;
    color: var(--color-text-secondary);
}

.description {
    color: var(--color-text);
    display: block;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    overflow-wrap: anywhere;
    word-break: break-word;
    font-size: 22px;
}
</style>