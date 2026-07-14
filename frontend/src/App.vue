<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import NavigationSidebar from './components/NavigationSidebar.vue';
import { ref } from 'process';

const route = useRoute()

</script>

<template>
  <main class="page-content">
    <NavigationSidebar v-if="route.meta.showSidebar"/>
    <RouterView class="main-view" v-slot="{ Component }">
        <Transition :name="(route.meta.withAnimation ? 'view' : '')" mode="out-in">
            <component :is="Component"/>
        </Transition>
    </RouterView>
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
  display: flex;
}

.main-view {
  height: 100%;
  width: 100%;
  overflow-y: auto;
}

.view-enter-from,
.view-leave-to {
    opacity: 0;
}

.view-enter-active,
.view-leave-active {
    transition: opacity 0.2s,
                transform 0.2s;
}
</style>
