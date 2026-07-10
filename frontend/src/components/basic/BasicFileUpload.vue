<script setup lang="ts">
import { ref } from 'vue';

import FileAddSVG from "@/assets/FileAdd.svg"

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
  <div class="file-upload">
    <div class="file-list-wrapper">
      <ul v-if="files.length > 0">
        <li v-for="(file, index) in files" :key="file.name"> 
          <span class="remove-button" @click="removeFile(index)">X</span> 
          {{ file.name }}
        </li>
      </ul>
      <div v-else class="no-files-wrapper">Upload files first</div>
    </div>
    <label for="file-input" class="btn">
      <span>
          <FileAddSVG />
          <span class="button-text">Add files</span>
      </span>
    </label>
    <input id="file-input" type="file" multiple hidden @change="handleFileSelect" />
  </div>
</template>

<style>
  .file-upload {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .file-list-wrapper {
    display: flex;
    flex-direction: column;
    min-height: 10em;
    height: 100%;
    background-color: var(--white);
    border-radius: 16px;
    border: 1px solid var(--secondary);
    padding: 16px;
  }

  .file-list-wrapper ul {
    list-style: none;
    padding-left: 0;
  }

  .no-files-wrapper {
    user-select: none;
    display: flex;
    height: 100%;
    width: 100%;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: var(--secondary);
    text-align: center;
  }
  
  .remove-button {
    font-weight: 700;
    color: var(--raddish);
    cursor: pointer;
  }

  .btn {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    width: 100%;
    cursor: pointer;
    padding: 16px;
    padding-top: 8px;
    padding-bottom: 8px;
    font-size: 16px;
    border: 1px solid var(--secondary);
    background-color: var(--white);
    border-radius: 16px;
    --icon-width: 16px;
    --icon-height: 16px;
    --icon-stroke: var(--secondary-dimm);
    color: var(--secondary-dimm);
    transition: 0.2s;
  }

  .btn,
  .file-list-wrapper {
    box-sizing: border-box;
  }

  .btn:hover {
    background-color: var(--background-light);
  }

  .btn:active {
    background-color: var(--background-dimm);
  }

  .btn > span {
    display: flex;
    align-items: center;
    gap: 8px;
    justify-content: center;
  }

</style>
