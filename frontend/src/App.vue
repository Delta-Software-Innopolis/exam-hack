<script setup lang="ts">
import { RouterView } from 'vue-router'
import HeaderComponent from './components/HeaderComponent.vue';
</script>

<template>
  <header>
    <HeaderComponent/>
  </header>

  <main class="page-content">
    <RouterView />
  </main>
</template>

<style scoped>

.page-content {
  padding: 0 15px;
}

</style>
