import { test as base, expect, Page } from '@playwright/test';


type Fixtures = {
    authenticatedPage: Page;
};


export const test = base.extend<Fixtures>({

    authenticatedPage: async ({ page }, use) => {

        const username = `test_${Date.now()}@mail.com`;
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


        await page.waitForURL('/quizzes');


        await use(page);
    }

});


export { expect };
