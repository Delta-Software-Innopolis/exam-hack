export function errorToString(error: unknown): string {
    let output = `${error}`
    output = output[0]?.toUpperCase() + output.slice(1)
    return output
}