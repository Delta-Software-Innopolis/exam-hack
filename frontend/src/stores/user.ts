import { ref } from 'vue'
import { defineStore } from 'pinia'


export const useUserStore = defineStore('user', () => {
  const username = ref('')
  const isNew = ref(true)
  const accessToken = ref(localStorage.getItem('access_token') || '')
  const refreshToken = ref(localStorage.getItem('refresh_token') || '')

  function getAccessToken() { return accessToken.value; }
  function getRefreshToken() { return refreshToken.value; }

  function saveAccessToken(token: string) {
    accessToken.value = token;
    localStorage.setItem('access_token', token);
  }

  function saveRefreshToken(token: string) {
    refreshToken.value = token;
    localStorage.setItem('refresh_token', token);
  }

  return {
    username,
    isNew,
    saveAccessToken,
    saveRefreshToken,
    getAccessToken,
    getRefreshToken,
  }
},
{
  persist: true
})
