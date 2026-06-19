<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import QuizesView from './views/QuizesView.vue';
</script>

<template>
  <header>
      <!-- <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav> -->
  </header>

  <QuizesView />
  <!-- <RouterView /> -->
</template>

<style scoped>

</style>
