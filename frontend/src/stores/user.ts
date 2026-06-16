import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const name = ref("")
  const password = ref("")
  const isNew = ref(true)
  return { name, password, isNew }
})
