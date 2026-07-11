<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import PlaySVG from '@/assets/Play.svg';
</script>


<template>
<BasicButton v-bind="$attrs">
    <PlaySVG/> <slot></slot>
</BasicButton>
</template>


<style scoped>
button {
    --icon-width: 20px;
    --icon-height: 20px;
    --icon-stroke-width: 3;
    --icon-stroke: var(--white);
    padding-left: 10px;
}

button .secondary {
    --icon-stroke: var(--secondary);
}
</style>
