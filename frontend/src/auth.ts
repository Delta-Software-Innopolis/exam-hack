import useNetworkManager from "./network"
import type { AuthResponse } from "./types"


export async function register(username: string, password: string): Promise<AuthResponse> {
    const nm = useNetworkManager()
    const request = await nm.fetch_auth('/auth/reg', {
        method: 'POST',
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"username": username,
			"password": password
		}),
        credentials: 'include',
    })

	if (!request.ok){
		const response = await request.json()
		console.error(response)
        throw response.error
	}

	const response = await request.json() as AuthResponse
	console.debug(response)
    return response
}


export async function login(username: string, password: string): Promise<AuthResponse> {
    const nm = useNetworkManager()
	const request = await nm.fetch_auth('/auth/login',{
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"username": username,
			"password": password
		}),
        credentials: 'include',
	})

	if (!request.ok){
		const response = await request.json()
		console.error(response)
        throw response.error
	}

	const response = await request.json()
	console.debug(response)
    return response
}


export async function validate(): Promise<boolean> {
    const nm = useNetworkManager()
    try {
        const request = await nm.validate_token()
        if (!request.ok) {
            const response = await request.json()
            console.error(response)
            return false
        }
        const response = await request.json()
        if (response.status === 'ok') {
            return true
        } else {
            return false
        }
    } catch (err) {
        console.error(err)
        return false
    }
}
