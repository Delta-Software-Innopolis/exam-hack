<script setup lang="ts">
import { RouterLink, useRoute, useRouter } from 'vue-router';

import BookSVG from '@/assets/Book.svg'
import MagnifyingGlassSVG from '@/assets/MagnifyingGlass.svg'
import UserSVG from '@/assets/User.svg'
import SettingsSVG from '@/assets/Settings.svg'
import CircleSVG from '@/assets/Circle.svg'
import ExamHackerHorizontalSVG from '@/assets/ExamHackerHorizontal.svg';

const route = useRoute()

</script>


<template>
    <div class="navigation-sidebar">
        <div class="exam-hacker-logo">
            <ExamHackerHorizontalSVG/>
        </div>
        <nav>
            <RouterLink to="/quizzes" class="nav-button" :class="{active: route.path.match('/quizzes.*')}">
                <BookSVG />
                <h1>My Quizzes</h1>
            </RouterLink>
            <RouterLink to="/quizhub" class="nav-button" :class="{active: route.path.match('/quizhub.*')}">
                <MagnifyingGlassSVG />
                <h1>QuizHub</h1>
            </RouterLink>
            <RouterLink to="/profile" class="nav-button" :class="{active: route.name == 'profile'}">
                <UserSVG />
                <h1>Profile</h1>
            </RouterLink>
            <RouterLink to="/settings" class="nav-button" :class="{active: route.name == 'settings'}">
                <SettingsSVG />
                <h1>Settings</h1>
            </RouterLink>
            <div class="bottom">
                <RouterLink to="/showcase" class="nav-button" :class="{active: route.name == 'showcase'}">
                    <CircleSVG />
                    <h1>Showcase</h1>
                </RouterLink>
            </div>
        </nav>
    </div>
</template>


<style scoped>
    .navigation-sidebar {
        background-color: var(--white);
        display: flex;
        flex-direction: column;
        gap: 8px;
        padding: 16px;
        width: max-content;
    }

    .exam-hacker-logo {
        --icon-height: 38px;
        --icon-width: fit-content;
    }

    .navigation-sidebar nav {
        display: flex;
        flex-direction: column;
        gap: 8px;
        width: max-content;
        height: 100%;
    }

    .nav-button {
        text-decoration: none;
        width: 100%;
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 8px;
        padding: 16px;
        padding-top: 8px;
        padding-bottom: 8px;
        border-radius: 16px;
        cursor: pointer;
        color: var(--secondary-dimm);
        --icon-width: 18px;
        --icon-height: 18px;
        --icon-stroke: var(--secondary-dimm);
        transition: background-color 0.2s, color 0.2s;
    }

    nav .bottom {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: end;
    }

    .nav-button:hover{
        background-color: var(--background-light)
    }

    .nav-button h1 {
        font-size: 16px;
        font-weight: normal;
    }

    .nav-button.active {
        background-color: var(--primary);
        color: var(--white);
        --icon-stroke: var(--white);
    }
</style>
