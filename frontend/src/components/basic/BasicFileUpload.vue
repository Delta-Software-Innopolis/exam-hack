<script setup lang="ts">
import { ref } from 'vue';

import FileAddSVG from "@/assets/FileAdd.svg"


const files = ref<File[]>([]);
const isDragging = ref(false);


const props = withDefaults(
    defineProps<{
        allowedExtensions?: string[];
        maxFileSize?: number; // bytes
    }>(),
    {
        allowedExtensions: () => [],
        maxFileSize: undefined,
    }
);

const emit = defineEmits<{
    (e: "changed", files: File[]): void
}>();

function removeFile(index: number) {
    files.value.splice(index, 1);
    emit("changed", [...files.value])
}


function isAllowedExtension(file: File) {
    if (props.allowedExtensions.length === 0) return true;

    const name = file.name.toLowerCase();

    return props.allowedExtensions.some(ext =>
        name.endsWith(ext.toLowerCase())
    );
}

function formatFileSize(bytes: number) {
    if (bytes < 1024) return `${bytes} B`;

    const units = ["KB", "MB", "GB", "TB"];
    let size = bytes / 1024;
    let unit = 0;

    while (size >= 1024 && unit < units.length - 1) {
        size /= 1024;
        unit++;
    }

    return `${size.toFixed(1)} ${units[unit]}`;
}

function isDuplicate(file: File) {
    return files.value.some(existing =>
        existing.name === file.name &&
        existing.size === file.size &&
        existing.lastModified === file.lastModified
    );
}

function processFiles(selectedFiles: File[]) {
    const validFiles: File[] = [];

    for (const file of selectedFiles) {
        if (!isAllowedExtension(file)) {
            alert(`"${file.name}" is not an allowed file type.`);
            continue;
        }

        if (isDuplicate(file)) {
            continue;
        }

        if (props.maxFileSize && file.size > props.maxFileSize) {
            alert(
                `"${file.name}" is too big. Please choose a file under ${formatFileSize(props.maxFileSize)}.`
            );
            continue;
        }

        validFiles.push(file);
    }

    files.value.push(...validFiles);
    emit("changed", [...files.value]);
}

function handleFileSelect(e: Event) {
    const input = e.target as HTMLInputElement;

    processFiles(Array.from(input.files || []));

    // Allow selecting the same file again later
    input.value = "";
}

const dragCounter = ref(0);

function handleDragOver(e: DragEvent) {
    e.preventDefault();
}

function handleDragEnter(e: DragEvent) {
    e.preventDefault();
    dragCounter.value++;
    isDragging.value = true;
}

function handleDragLeave(e: DragEvent) {
    e.preventDefault();
    dragCounter.value--;

    if (dragCounter.value <= 0) {
        dragCounter.value = 0;
        isDragging.value = false;
    }
}

function handleDrop(e: DragEvent) {
    e.preventDefault();

    dragCounter.value = 0;
    isDragging.value = false;

    if (!e.dataTransfer) return;

    processFiles(Array.from(e.dataTransfer.files));
}

</script>

<template>
    <div class="file-upload">
        <div class="file-list-wrapper" :class="{ dragging: isDragging }" @dragenter="handleDragEnter"
            @dragover="handleDragOver" @dragleave="handleDragLeave" @drop="handleDrop">
            <ul v-if="files.length > 0">
                <li v-for="(file, index) in files" :key="`${file.name}-${file.size}-${file.lastModified}`">
                    <span class="remove-button" @click="removeFile(index)">X</span>
                    {{ file.name }}
                </li>
            </ul>
            <div v-else class="no-files-wrapper">
                Drop files here or click "Add files"
            </div>
        </div>
        <label for="file-input" class="btn">
            <span>
                <FileAddSVG />
                <span class="button-text">Add files</span>
            </span>
        </label>
        <input id="file-input" type="file" multiple hidden :accept="props.allowedExtensions.join(',')"
            @change="handleFileSelect" />
    </div>
</template>

<style>
.file-upload {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

file-list-wrapper ul {
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

.btn>span {
    display: flex;
    align-items: center;
    gap: 8px;
    justify-content: center;
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
    transition: background-color .2s, border-color .2s;
}

.file-list-wrapper.dragging {
    background: var(--background-light);
    border: 2px dashed var(--secondary);
}
</style>
