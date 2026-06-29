const AUTH_URL = import.meta.env.DEV ? "http://localhost:8081": import.meta.env.VITE_AUTH_URL_DEV 


interface AuthResponse {
    access_token: string,
    refresh_token: string,
}


export async function register(username: string, password: string): Promise<AuthResponse> {
	const request = await fetch(`${AUTH_URL}/auth/reg`,{
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"username": username,
			"password": password
		})
	})

	if (!request.ok){
		const response = await request.json()
		console.error(response)
        throw response.error
	}

	const response: AuthResponse = await request.json()
	console.debug(response)
    return response
}


export async function login(username: string, password: string): Promise<AuthResponse> {
	const request = await fetch(`${AUTH_URL}/auth/login`,{
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"username": username,
			"password": password
		})
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


export async function refresh(refreshToken: string): Promise<AuthResponse> {
	const request = await fetch(`${AUTH_URL}/auth/refresh`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"refresh_token": refreshToken
		})
	})
	
	if (!request.ok) {
		const response = await request.json()
		console.error(response)
		throw response.error
	}

	const response = await request.json()
	console.debug(response)
	return response
}


export async function validate(accessToken: string): Promise<boolean> {
	const request = await fetch(`${AUTH_URL}/auth/validate`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"access_token": accessToken
		})
	})

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
}
