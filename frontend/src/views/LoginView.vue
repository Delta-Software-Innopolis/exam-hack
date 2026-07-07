<script setup lang="ts">
import BasicButton from '@/components/newBasic/BasicButton.vue';
import BasicInput from '@/components/newBasic/BasicInput.vue';
import { onMounted, onUnmounted, ref, useTemplateRef, type Ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

import * as Auth from "@/auth"
import { errorToString } from '@/utils';
import { useTokenStore } from '@/stores/token';


const router = useRouter()
const username = ref("")
const password = ref("")

const errorMessage: Ref<string | null> = ref(null)


const inputs = useTemplateRef("inputs")


async function login() {
    const userStore = useUserStore()
    const tokenStore = useTokenStore()
    try {
        const response = await Auth.login(username.value, password.value)
        userStore.username = username.value
        userStore.isNew = false
        tokenStore.accessToken = response.access_token
        router.push({name: "quizzes"})
    } catch (err) {
        errorMessage.value = errorToString(err)
    }
}

function onInput() {
    errorMessage.value = null;
}

onMounted(()=>{
    if (inputs.value) {
        inputs.value.addEventListener("input", onInput)
    }
})

onUnmounted(()=>{
    if (inputs.value) {
        inputs.value.removeEventListener("input", onInput)
    }
})
</script>

<template>
  <div class="window-wrapper">
    <div class="sidebar">
      <h1>Login to ExamHacker</h1>
      <div ref="inputs" class="inputs-wrapper">
        <BasicInput placeholder="Enter your email" type="email" v-model="username"></BasicInput>
        <BasicInput placeholder="Enter your password" type="password" v-model="password"></BasicInput>
      </div>
      <div v-if="errorMessage" style="color: red;">{{ errorMessage }}</div>
      <div class="buttons-wrapper">
        <BasicButton variant="primary" @click="login()">Continue</BasicButton>
        <BasicButton variant="secondary" @click="router.push('/auth/signup')">I don't have an account yet</BasicButton>
      </div>
    </div>
    <div class="backbone">
      <img src="/pretty_img_here_soon.png" alt="pretty_img_here_soon">
    </div>
  </div>
</template>

<style scoped>
.window-wrapper {
  display: flex;
  flex-direction: row;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  justify-content: space-evenly;
  background-color: var(--background-blueish);
}

.sidebar {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 32px;
  justify-content: center;
  align-items: center;
  background-color: white;
  box-shadow: none;
  animation: bounce 1s;
}

.sidebar * {
  animation: appear 0.5s;
}

.buttons-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: fit-content;
  width: fit-content;
}

.inputs-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: fit-content;
  width: fit-content;
}

.inputs-wrapper * {
  height: 48px;
  width: 387px;
  font-size: 18px;
}

.buttons-wrapper button {
  height: 48px;
  width: 387px;
  font-size: 18px;
}

.backbone {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.backbone img {
  scale: 0.75;
}

@keyframes bounce {
  0% { padding-left: 0; }
  25% { padding-left: 5%; }
  100% { padding-left: 0; }
}

@keyframes appear {
  0% { opacity: 0%; }
  100% { opacity: 100%; }
}
</style>
