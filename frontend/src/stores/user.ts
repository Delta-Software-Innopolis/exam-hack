import { ref } from 'vue'
import { acceptHMRUpdate, defineStore } from 'pinia'

import * as Auth from "@/auth"


export const useUserStore = defineStore('user', () => {
    const username = ref('')
    const isNew = ref(true)
    const accessToken = ref(localStorage.getItem('access_token') || '')
    const refreshToken = ref(localStorage.getItem('refresh_token') || '')

    var __isAuthenticated = false
    async function isAuthenticated(): Promise<boolean> {
        if (!__isAuthenticated) {
            __isAuthenticated = await __validateOrRefresh()
        }
        console.debug(`__isAuthenticated: ${__isAuthenticated}`)
        return __isAuthenticated
    }

    async function __validateOrRefresh(): Promise<boolean> {
        if (accessToken.value === '') { return false }
        if (refreshToken.value === '') { return false }  // TODO: remove later (cookie)

        let valid = await Auth.validate(accessToken.value)
        if (valid) { return true }
        try {
            let response = await Auth.refresh(refreshToken.value)
            saveAccessToken(response.access_token)
            saveRefreshToken(response.refresh_token)
            return true
        } catch (err) {
            console.error(err)
            return false
        }
    }

    function getAccessToken(): string { return accessToken.value; }
    function getRefreshToken(): string { return refreshToken.value; }

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
        isAuthenticated,
    }
},
{
    persist: true
})
