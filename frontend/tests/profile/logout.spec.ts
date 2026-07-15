import { test, expect } from '@playwright/test';
import { register } from '../helpers/auth';


test('user can logout and cannot access profile', async ({ page }) => {

    const user = await register(page);

    await page.goto(`/profile/${user.username}`);


    await page
        .getByRole('button', { name: /Logout/i })
        .click();


    await expect(page).toHaveURL(/welcome/);


    await page.goto(`/profile/${user.username}`);

    await expect(page).toHaveURL(/welcome/);
});
