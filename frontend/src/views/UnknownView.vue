<script setup lang="ts">
import BasicButton from '@/components/basic/BasicButton.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

</script>

<template>
<div class="unknown">
    <div class="wrapper">
        <h3>4️⃣0️⃣4️⃣ There is nothing on this page</h3>
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

.wrapper { 
    width: max-content;
    display: flex;
    flex-direction: column;
    gap: 16px;
    align-items: center;
    justify-content: center;
}

.wrapper button {
    width: 16em;
}
</style>
