<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue';
import PlusSVG from '@/assets/Plus.svg';
</script>


<template>
<BasicButton v-bind="$attrs">
    <PlusSVG/> <slot></slot>
</BasicButton>
</template>


<style scoped>
button {
    --icon-width: 16px;
    --icon-height: 16px;
    --icon-stroke: var(--white);
    padding-left: 12px;
    gap: 4px;
}

.secondary {
    --icon-stroke: var(--secondary-dimm);
}
</style>
