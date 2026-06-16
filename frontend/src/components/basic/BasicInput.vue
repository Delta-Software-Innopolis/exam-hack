<script setup lang="ts">
const value = defineModel()
</script>


<template>
  <input v-bind="$attrs" v-model="value">
    <slot></slot>
  </input>
</template>


<style scoped>
input {
  display: flex;
  justify-content: center;
  align-items: center;
  border: solid 1px;

  padding: 16px;
  padding-top: 8px;
  padding-bottom: 8px;
  
  font-size: 14px;

  border-radius: 16px;

  background-color: white;
  border-color: #757575;
  color: #757575;
}

input:hover {
  filter: brightness(0.95);
}

input:active {
  filter: brightness(0.8);
}
</style>