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
  border: 1px solid var(--secondary);

  padding: 16px;
  padding-top: 12px;
  padding-bottom: 12px;
  
  font-size: 16px;

  border-radius: 16px;

  background-color: var(--white);
  color: var(--secondary);
}

input:hover {
  background-color: var(--background-light);
}

input:focus {
  border: 1px solid var(--secondary-dimm);
  background-color: var(--white);
  color: var(--secondary-dimm);
  outline: none;
}

input:disabled {
  border: 1px dashed var(--secondary);
  background-color: var(--background-light);
  color: var(--secondary);
}
</style>
