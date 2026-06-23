import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const name = ref("")
  const password = ref("")
  const isNew = ref(true)
  const access_token = ref(localStorage.getItem("access_token") || "")
  const refresh_token = ref("")
  return { name, password, isNew, access_token, refresh_token }
},
{
  persist: true
})
