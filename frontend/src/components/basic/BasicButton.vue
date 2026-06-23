<script setup lang="ts">
import { computed } from 'vue';

enum ButtonVariants {
  DEFAULT = 'default',
  GREEN = 'green',
  YELLOW = 'yellow',
  RED = 'red',
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
  <button v-bind="$attrs" :class="variantClass">
    <slot></slot>
  </button>
</template>


<style scoped>
button {
  display: flex;
  justify-content: center;
  align-items: center;
  border: solid 1px;

  padding: 16px;
  padding-top: 8px;
  padding-bottom: 8px;
  
  font-size: 14px;

  border-radius: 16px;
}

button:hover {
  filter: brightness(0.95);
}

button:active {
  filter: brightness(0.8);
}

.default {
  background-color: white;
  border-color: #757575;
  color: #757575;
}

.green {
  background-color: #CFF7D3;
  border-color: #14AE5C;
  color: #14AE5C;
}

.yellow {
  background-color: #FFF1C2;
  border-color: #BF6A02;
  color: #BF6A02;
}

.red {
  background-color: #FFBDBD;
  border-color: #EC221F;
}

.clicked {
    border-width: 2px;
}
</style>