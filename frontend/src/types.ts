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
    username: string
}

export interface QuizItem {
    id: number,
    name: string,
    creation_date?: Date,
    updation_date?: Date,
    forked_from?: number,
    author: Author,
    description?: string,
    cards: Card[],
    mock?: boolean
}


export interface AuthResponse {
    access_token: string,
    refresh_token: string,
}


export type CardType = "multiple_choice" | "single_answer";

export interface GenerateCardsResponse {
  cards: any[];
}
