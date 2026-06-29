<script setup lang="ts">
import { useRoute } from 'vue-router';
import {computed} from 'vue'
import { useQuizzesStore } from '@/stores/quizzes';
const route = useRoute()
const quizzesStore = useQuizzesStore()
const headerInfo = computed(() => quizzesStore.headerInfo)
</script>

<template>
    <div :class="$route.meta.headerClass ? $route.meta.headerClass: 'header-container'">
        <nav class="icons">
            <div @click="">
                <img src="@/assets/User.svg" alt="Profile">
                <div>Profile</div>
            </div>
            <div @click="$router.push({name: 'quizzes'})">
                <img src="@/assets/Book.svg" alt="Profile">
                <div>Quizzes</div>
            </div>
            <div>
                <img src="@/assets/Settings.svg" alt="Profile">
                <div>Settings</div>
            </div>
        </nav>
        <div class="context-info">{{ headerInfo }}</div> 
    </div>
</template>

<style scoped>
.header-container {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    width: 100%;
    height: fit-content;
    padding:16px 32px;
    justify-content: space-between;
    background-color: var(--color-background-secondary);
    border-radius: 0 0 16px 16px;
    margin-bottom: 30px;
}

.icons {
    width: fit-content;
    height: fit-content;
    display: flex;
    gap: 32px;
}

.icons > div {
    width: 64px;
    display: grid;
    grid-template-rows: 42px auto;
    row-gap: 4px;
    justify-items: center;
}

.icons > div > img {
    width: 42px;
    height: 42px;
    object-fit: contain;
    display: block;
}

.icons > div > div {
    line-height: 1;
    min-height: 1em;
    white-space: nowrap;
    text-align: center;
}

.context-info {
    font-weight: bold;
    font-size: 20px;
}

.hidden {
    display: none;
}
</style>