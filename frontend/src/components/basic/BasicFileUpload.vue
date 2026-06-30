<script setup lang="ts">
import { ref } from 'vue';

const files = ref<File[]>([]);

function handleFileSelect(e: Event) {
  const input = e.target as HTMLInputElement;
  const filesAsArray = Array.from(input?.files || []);
  files.value = files.value.concat(filesAsArray);
  emit("changed", [...files.value])
}

const emit = defineEmits<{(e: "changed", files: File[]): void}>();

function removeFile(index: number) {
  files.value.splice(index, 1);
  emit("changed", [...files.value])
}

</script>

<template>
  <div class="list-wrapper">
  <ul>
    <li v-for="(file, index) in files" :key="file.name"> 
      <span class="remove-button" @click="removeFile(index)">X</span> 
      {{ file.name }}
    </li>
  </ul>
  </div>
  <label for="file-input" class="btn">
	  <span>
        <!-- <img src="/add_files_icon.svg" alt="add_files_icon.svg" /> -->
        <span class="button-text">Add files</span>
    </span>
  </label>
  <input id="file-input" type="file" multiple hidden @change="handleFileSelect" />
</template>

<style>
  .list-wrapper {
    width: 400px;
    heigh: 78px;
    background-color: white;
    border-radius: 16px;
    border: 1px solid #D9D9D9;
    padding: 16px;
    padding-bottom: 8px;
    padding-top: 8px;
    margin-bottom: 8px;
  }

  .list-wrapper ul {
    list-style: none;
    padding-left: 0;
  }
  
  .remove-button {
    font-weight: 700;
    color: red;
    cursor: pointer;
  }

  .btn {
    display: inline-block;
    cursor: pointer;
    width: 400px;
    padding: 16px;
    padding-top: 8px;
    padding-bottom: 8px;
    font-size: 16px;
    border: 1px solid #D9D9D9;
    background-color: white;
    border-radius: 16px;
  }
  .btn,
  .list-wrapper {
    box-sizing: border-box;
  }

  .btn > span {
    display: flex;
    align-items: center;
    gap: 8px;
    justify-content: center;
  }

</style>
