import { Page, expect } from '@playwright/test';


export async function register(page: Page) {
    const username = `test-${Date.now()}@example.com`;
    const password = 'Password123!';


    await page.goto('/auth/signup');


    await page
        .getByPlaceholder('Enter your email')
        .fill(username);

    await page
        .getByPlaceholder('Come up with a password')
        .fill(password);

    await page
        .getByPlaceholder('Repeat the password')
        .fill(password);


    await page
        .getByRole('button', { name: 'Continue' })
        .click();


    await expect(page).toHaveURL(/\/quizzes/);


    return {
        username,
        password,
    };
}
