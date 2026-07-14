<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import TrashSVG from '@/assets/Trash.svg';
</script>


<template>
<BasicButton v-bind="$attrs">
    <TrashSVG/> <slot></slot>
</BasicButton>
</template>


<style scoped>
button {
    --icon-width: 14px;
    --icon-height: 14px;
    --icon-stroke: var(--white);
    gap: 4px;
}

button .secondary {
    --icon-stroke: var(--secondary);
}
</style>
