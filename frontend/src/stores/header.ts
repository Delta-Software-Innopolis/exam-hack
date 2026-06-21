import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useHeaderStore = defineStore('header', () => {
  const contextInfo = ref("")
  function setContextInfo(text: string){
    contextInfo.value = text
  }
  return {contextInfo, setContextInfo}
})