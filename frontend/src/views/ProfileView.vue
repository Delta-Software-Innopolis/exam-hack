<script setup lang="ts">
import { useRoute } from 'vue-router'
import AvatarPlaceholderSVG from '@/assets/AvatarPlaceholder.svg'
import LogoutSVG from '@/assets/Logout.svg'
import BasicButton from '@/components/basic/BasicButton.vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useTokenStore } from '@/stores/token'
import * as Auth from '@/auth'


const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const tokenStore = useTokenStore()

const username = route.params.username as string

/*
const logout = () => {
    tokenStore.accessToken = "";
    userStore.logout();
    router.push("/");
}
*/

async function logout() {
    try {
        await Auth.logout()
    } finally {
        tokenStore.accessToken = ''
        userStore.logout()
        router.push({ name: 'welcome' })
    }
}

</script>

<template>
    <div class="main-wrapper">
        <div class="profile-wrapper">
            <AvatarPlaceholderSVG />
            <div class="username-wrapper">{{ username }}</div>
            <BasicButton
                v-if="username === userStore.username"
                variant="primary"
                @click="logout"
                class="logout-button">
            <LogoutSVG />Logout</BasicButton>
        </div>
    </div>
</template>

<style scoped>
.main-wrapper {
    padding: 64px;
    image-rendering: crisp-edges;
}

.profile-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.username-wrapper {
    font-weight: 700;
    font-size: 32px;
}

.logout-button {
    display: flex;
    gap: 11px;
}
</style>
