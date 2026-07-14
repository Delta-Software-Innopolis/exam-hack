<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import { useRouter } from 'vue-router';

const router = useRouter();
</script>

<template>
  <div class="window-wrapper">
    <div class="sidebar">
      <h1>Welcome to ExamHacker</h1>
      <div class="buttons-wrapper">
        <BasicButton variant="primary" @click="router.push('/auth/signup')">Sign up</BasicButton>
        <BasicButton variant="secondary" @click="router.push('/auth/login')">I already have an account</BasicButton>
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
  gap: 16px;
  justify-content: center;
  align-items: center;
  background-color: white;
  box-shadow: none;
  /* box-shadow: 0px 0px 16px 4px rgba(0,0,0,0.25); */
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

.buttons-wrapper button {
  width: 100%;
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
