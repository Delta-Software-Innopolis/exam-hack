<template>
    <div :class="$route.meta.headerClass">
        <nav class="icons">
            <div @click="">
                <img src="@/assets/Profile.svg" alt="Profile">
                <div>Profile</div>
            </div>
            <div @click="$router.push({name: 'quizzes'})">
                <img src="@/assets/Quizzes.svg" alt="Profile">
                <div>Quizzes</div>
            </div>
            <div>
                <img src="@/assets/Settings.svg" alt="Profile">
                <div>Settings</div>
            </div>
        </nav>
        <div class="context-info">{{ $route.meta?.headerInfo }}</div> 
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