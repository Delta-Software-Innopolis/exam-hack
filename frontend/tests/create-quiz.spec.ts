import { test, expect } from './fixtures/auth';


test('user can create quiz manually', async ({ authenticatedPage }) => {

    const page = authenticatedPage;


    await page.goto('/quizzes/new');


    await page
        .getByPlaceholder('Enter Quiz title')
        .fill('Playwright Test Quiz');


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

    page.on('dialog', async dialog => {
        await dialog.accept();
    });
  
   await page.waitForURL('/quizzes');



    await expect(
        page.getByText('Playwright Test Quiz')
    ).toBeVisible();
    

});
