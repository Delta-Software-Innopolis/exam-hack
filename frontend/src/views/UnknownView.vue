<script setup lang="ts">
import BasicButton from '@/components/newBasic/BasicButton.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

</script>

<template>
<div class="unknown">
    <div class="unknown-wrapper">
        <h3>Sorry, you're not supposed to be here</h3>
        <BasicButton @click="router.push('/')">Go back</BasicButton>
    </div>
</div>
</template>

<style scoped>
.unknown {
    display: flex;
    flex-direction: column;
    gap: 16px;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
}

.unknown-wrapper { 
    width: max-content;
    display: flex;
    flex-direction: column;
    gap: 16px;
    align-items: center;
    justify-content: center;
}

.unknown-wrapper button {
    width: 16em;
}
</style>
