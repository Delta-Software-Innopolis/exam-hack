<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import BasicInput from '@/components/basic/BasicInput.vue';
import { useRouter } from 'vue-router';

const router = useRouter();
</script>

<template>
  <div class="window-wrapper">
    <div class="sidebar">
      <h1>Sign Up to ExamHacker</h1>
      <div class="inputs-wrapper">
        <BasicInput placeholder="Enter your email" type="email"></BasicInput>
        <BasicInput placeholder="Come up with a password" type="password"></BasicInput>
        <BasicInput placeholder="Repeat the password" type="password"></BasicInput>
      </div>
      <div class="buttons-wrapper">
        <BasicButton variant="green" @click="router.push('/end-of-demo-0')">Continue</BasicButton>
        <BasicButton @click="router.push('/auth/login')">I already have an account</BasicButton>
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
}

.sidebar {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 32px;
  justify-content: center;
  align-items: center;
  background-color: white;
  box-shadow: 0px 0px 16px 4px rgba(0,0,0,0.25);
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