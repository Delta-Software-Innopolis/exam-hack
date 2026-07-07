import { ref } from 'vue'
import { defineStore } from 'pinia'

import * as Auth from '@/auth'


export const useUserStore = defineStore('user', () => {
    const username = ref('')
    const isNew = ref(true)

    var __isAuthenticated = false

    async function isAuthenticated(): Promise<boolean> {
        if (!__isAuthenticated) {
            __isAuthenticated = await __validateOrRefresh()
        }
        console.debug(`__isAuthenticated: ${__isAuthenticated}`)
        return __isAuthenticated
    }

    async function __validateOrRefresh(): Promise<boolean> {
        try {
            let valid = await Auth.validate()
            if (valid) { return true }
            else { return false }
        } catch (err) {
            console.error(err)
            return false
        }
    }

    return {
        username,
        isNew,
        isAuthenticated,
    }
},
{
    persist: true
})
