<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import NavigationSidebar from './components/NavigationSidebar.vue';

const route = useRoute()
</script>

<template>
  <main class="page-content">
    <NavigationSidebar :class="{show: route.meta.showSidebar}"/>
    <RouterView class="main-view"/>
  </main>
</template>

<style scoped>

.page-content {
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: row;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
}

.navigation-sidebar {
  height: 100%;
  display: none;
}

.navigation-sidebar.show {
  display: flex;
}

.main-view {
  height: 100%;
  width: 100%;
  overflow-y: auto;
}
</style>
