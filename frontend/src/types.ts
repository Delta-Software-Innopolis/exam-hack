export interface Card {
    id: number,
    question: string,
    options: string[],
    correct: number[],
    hint: string,
    explanation: string,
}

export interface Author {
    id: number,
    name: string
}

export default interface QuizItem {
    id: number,
    name: string,
    creation_date?: Date,
    updation_date?: Date,
    forked_from?: number,
    author: Author,
    description?: string
    cards: Card[]
}
