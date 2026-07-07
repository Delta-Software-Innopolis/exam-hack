import { parseJwt } from "@/utils";
import { defineStore } from "pinia";
import { ref } from "vue";


export const useTokenStore = defineStore('token', () => {
    const accessToken = ref('')
    setTimeout(()=>{
        let jwt = parseJwt(accessToken.value)
        console.log(jwt)
        console.log(new Date(jwt.exp*1000))
    }, 4000)

    return {
        accessToken,
    }
},
{
    persist: true
})
