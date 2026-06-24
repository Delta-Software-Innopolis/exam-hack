<script setup lang="ts">
const value = defineModel()
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
  border: 1px solid #D9D9D9;

  padding: 16px;
  padding-top: 8px;
  padding-bottom: 8px;
  
  font-size: 16px;

  border-radius: 16px;

  background-color: white;
  color: #757575;

  resize: none;
}

textarea::placeholder {
  font-family: inter;
}

textarea:hover {
  filter: brightness(0.95);
}

textarea:active {
  filter: brightness(0.8);
}
</style>
