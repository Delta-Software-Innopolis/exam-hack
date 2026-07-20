import useNetworkManager from "./network";

export async function publishQuizToHub(
    pack_id: number,
    subject: string,
    university: string,
    professor: string,
    course_book: string
): Promise<boolean> {
    const nm = useNetworkManager()

    try {
        const response = await nm.fetch_hub('/hub/packs', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                pack_id,
                subject,
                university,
                professor,
                course_book,
            }),
        });
        if (!response.ok) {
            console.error(response);
            throw new Error('something wrong with publishToHub');
        }
        return true;
    } catch (err) {
        console.error(err)
        return false;
    }
}
