export function errorToString(error: unknown): string {
    let output = `${error}`
    output = output[0]?.toUpperCase() + output.slice(1)
    return output
}

export interface JwtToken {
    exp: EpochTimeStamp,
    iat: EpochTimeStamp,
    type: string,
    user_id: number,
    username: string,
}

export  function parseJwt(token: string): JwtToken {
    var base64Url: string | undefined = token.split('.')[1];
    if (base64Url === undefined) { throw 'invalid jwt' }
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}
