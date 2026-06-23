export default interface Card {
    id: number,
    question: string,
    options: string[],
    correct: number,
    hint: string,
    explanation: string,
}
export default interface QuizItem {
    id: number,
    name: string,
    creation_date?: Date,
    updation_date?: Date,
    forked_from?: number,
    author?: string,
    description?: string
    cards: Card[]
}
