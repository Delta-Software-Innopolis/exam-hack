<script setup lang="ts">
const value = defineModel<string>({ default: '' })
</script>


<template>
  <textarea v-bind="$attrs" v-model="value">
    <slot></slot>
  </textarea>
</template>


<style scoped>
textarea {
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
  resize: vertical;
}

textarea::placeholder {
  font-family: inter;
}

textarea:hover {
  background-color: var(--background-light);
}

textarea:focus {
  border: 1px solid var(--secondary-dimm);
  background-color: var(--white);
  color: var(--secondary-dimm);
  outline: none;
}

textarea:disabled {
  border: 1px dashed var(--secondary);
  background-color: var(--background-light);
  color: var(--secondary);
}
</style>
