export default interface QuizItem {
    id: number,
    name: string,
    creation_date: Date,
    updation_date: Date,
    forked_from: number|null,
    question: string,
    options: string[],
    correct: number,
    hint: string,
    explanation: string
}