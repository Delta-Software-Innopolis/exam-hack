<script setup lang="ts">
import { onMounted, onUnmounted, ref, useTemplateRef } from 'vue';
import CrossSVG from '@/assets/Cross.svg';
import BasicButton from '@/components/basic/BasicButton.vue';


defineExpose({
    open,
    close,
});


const shown = ref(false);


function open() { shown.value = true; }
function close() { shown.value = false; }

const overlay = useTemplateRef('overlay');

</script>


<template>
    <Transition>
    <div ref="overlay" class="overlay" v-if="shown">
        <div class="window">
            <div class="topbar">
                <!-- <BasicButton @click="close">
                    <CrossSVG />
                </BasicButton> -->
            </div>
            <div class="content">
                <slot></slot>
            </div>
        </div>
    </div>
    </Transition>
</template>


<style scoped>
.overlay {
    position: fixed;
    display: flex;
    justify-content: center;
    align-items: center;
    inset: 0;
    background: rgba(0,0,0,0.5);
}

.window {
    border-radius: 16px;
    background-color: white;
    width: fit-content;
    height: fit-content;
}

.content {
    padding: 16px;
    padding-top: 0px;
}

.topbar {
    display: flex;
    width: 100%;
    border-top-right-radius: 16px;
    height: 16px;

    button {
        margin-left: auto;
        border: none;
        background: none;
        height: fit-content;
        padding: 8px;
        padding-left: 9px;
        padding-bottom: 9px;
        --icon-stroke: var(--secondary);
        --icon-width: 10px;
        --icon-height: 10px;
        border-bottom-left-radius: 16px;
        border-top-left-radius: 0;
        border-bottom-right-radius: 0;
    }
}

.v-enter-to,
.v-leave-from {
    opacity: 1;
    .window { opacity: 1; }
}

.v-enter-active {
  transition: 
    opacity 0.2s ease;

  .window {
    transition: 
        opacity 0.2s ease,
        transform 0.2s ease
    ;
    transition-delay: 0.1s;
  }
}

.v-leave-active {
  transition: opacity 0.2s ease;
  transition-delay: 0.2s;

  .window {
    transition: 
        opacity 0.2s ease,
        transform 0.2s ease
    ;
  }
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
  .window {
    opacity: 0; 
    transform: translateY(10px);
  }
}
.v-leave-to .window {
    transform: translateY(-10px);
}
</style>
