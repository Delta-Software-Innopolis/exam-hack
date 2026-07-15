import { test, expect } from './fixtures/auth';

test('user can delete quiz', async ({ authenticatedPage }) => {
    const page = authenticatedPage;

    await page.goto('/quizzes/new');

    await page
        .getByPlaceholder('Enter Quiz title')
        .fill('Quiz To Delete');

    await page
        .getByRole('button', { name: 'Continue' })
        .click();

    await expect(
        page.getByText('Use AI to generate questions')
    ).toBeVisible();

    await page
        .getByRole('button', { name: 'Skip' })
        .click();

    await expect(
        page.getByText('Edit generated questions')
    ).toBeVisible();

    await page
        .getByTestId('open-add-question')
        .click();

    await expect(
        page.getByText('Options')
    ).toBeVisible();

    await page
        .getByTestId('confirm-add-question')
        .click();

    await page
        .getByRole('button', { name: 'Finish Creation' })
        .click();

    await page.waitForURL('/quizzes');

    await expect(
        page.getByText('Quiz To Delete')
    ).toBeVisible();


    await page
        .getByText('Quiz To Delete')
        .click();


    page.once('dialog', dialog => dialog.accept());

    await page
        .getByTestId('delete-quiz')
        .click();


    await page.waitForURL('/quizzes');

    await expect(
        page.getByText('Quiz To Delete')
    ).not.toBeVisible();
});
