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
export interface QuizHubItem {
    id: number,
    author: Author,
    raiting: number|null,
    name: string,
    subject: string,
    university: string,
    professor: string,
    course_book: string
}


export interface AuthResponse {
    access_token: string,
    refresh_token: string,
}


export type CardType = "multiple_choice" | "single_answer";

export interface GenerateCardsResponse {
  cards: any[];
}
