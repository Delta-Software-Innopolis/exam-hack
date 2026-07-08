import { useTokenStore } from "./stores/token";
import type { AuthResponse } from "./types";


export const AUTH_URL = import.meta.env.DEV
    ? "http://localhost:8081"
    : import.meta.env.VITE_AUTH_URL_DEV;
export const CORE_URL = import.meta.env.DEV
    ? "http://localhost:8001"
    : import.meta.env.VITE_CORE_URL_DEV;
export const HUB_URL = import.meta.env.DEV
    ? "http://localhost:8067"
    : import.meta.env.VITE_HUB_URL_DEV;


export class NetworkManager {
    #tokenStore: ReturnType<typeof useTokenStore>;

    constructor(tokenStore: ReturnType<typeof useTokenStore>) {
        this.#tokenStore = tokenStore;
    }

    /**
     * Send request to auth service
     * @param path relative url path, without 'http://domain:port', example: '/auth/login'
     * @param init parameters for fetch
     * @param auth use Authorization header or not (default is false)
     */
    async fetch_auth(path: string, init?: RequestInit | undefined, auth: boolean = false): Promise<Response> {
        let url = new URL(path, AUTH_URL)
        if (auth)
            return await this.fetch(url, init)
        else
            return await this.unauth_fetch(url, init)
    }

    /**
     * Send request to core service
     * @param path relative url path, without 'http://domain:port', example '/packs/3'
     * @param init parameters for fetch
     * @param auth use Authorization header or not (default is true)
     */
    async fetch_core(path: string, init?: RequestInit | undefined, auth: boolean = true): Promise<Response> {
        let url = new URL(path, CORE_URL)
        return await this.fetch(url, init)
    }


    async fetch(
        input: string | URL | Request,
        init?: RequestInit | undefined,
    ): Promise<Response> {
        init = this.#populate_request_init(init)
        try {
            const response = await fetch(input, init)
            if (response.status == 401) {
                throw 401
            }
            return response
        } catch (err) {
            if (err != 401) {  // only catch 401
                throw err
            }
            if (await this.#refresh_with_cookie()) {
                const response = await fetch(input, init);
                if (response.status == 401) {
                    console.error('')
                    throw 401
                }
                return response
            } else {
                console.error('Could not refresh')
                throw 401
            }
        }
    }


    async unauth_fetch(
        input: string | URL | Request,
        init?: RequestInit | undefined,
    ): Promise<Response> {
        const response = await fetch(input, init)
        return response
    }

    
    async validate_token() {
        const response = await this.fetch_auth('/auth/validate', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "access_token": this.#tokenStore.accessToken
            })
        })
        const payload = await response.json()
        if (payload.status !== 'ok') {
            await this.#refresh_with_cookie()
        }
        return await this.fetch_auth('/auth/validate', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "access_token": this.#tokenStore.accessToken
            })
        })
    }


    #populate_request_init(init?: RequestInit | undefined): RequestInit {
        if (init === undefined) {
            init = {}
        }
        
        const headers: Record<string, string> = {
            'Authorization': `Bearer ${this.#tokenStore.accessToken}`
        };
        
        // Merge existing headers
        if (init.headers) {
            const existingHeaders = new Headers(init.headers);
            existingHeaders.forEach((value, key) => {
                headers[key] = value;
            });
        }
        
        return {
            ...init,
            headers
        };
    }

    async #refresh_with_cookie(): Promise<boolean> {
        const response = await fetch(`${AUTH_URL}/auth/refresh`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: 'include'
        });

        if (!response.ok) {
            console.error("Refresh failed: bad request", response);
            return false;
        }

        const payload = (await response.json()) as AuthResponse;

        if (payload.access_token == undefined) {
            console.error("Refresh failed: invalid payload", payload);
            return false;
        }

        this.#tokenStore.accessToken = payload.access_token;
        return true;
    }
}


export default function useNetworkManager() {
    const tokenStore = useTokenStore();
    return new NetworkManager(tokenStore);
}
