<script setup lang="ts">
  import { computed } from 'vue';
  enum ButtonVariants {
    DEFAULT = 'default',
    WHITE = 'white',
    RED = 'red',
    GREEN = 'green',
  }

  const props = defineProps({
    variant: {type: String, default: 'default'},
  })

  const variantClass = computed(() => {
    if (!Object.values<string>(ButtonVariants).includes(props.variant)) {
      return 'default';
    }
    return props.variant;
  })
</script>

<template>
  <div class="option-wrapper" v-bind="$attrs" :class="variantClass">
    <slot></slot>
  </div>
</template>

<style scoped>
.option-wrapper {
  max-width: 369px;
  width: 100%;
  max-height: 54px;
  height: 100%;
  overflow: hidden;
  font-size: 16px;
  padding: 16px;
  border-radius: 16px;
  display: flex;
  align-items: center;
} 

.default {
  background-color: var(--background-blueish);
}

.green {
  background-color: var(--quiz-green);
}

.red {
  background-color: var(--quiz-red);
}

.white {
  background-color: var(--white);
}
</style>
