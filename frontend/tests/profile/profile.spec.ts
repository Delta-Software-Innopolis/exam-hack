import { test, expect } from '@playwright/test';
import { register } from '../helpers/auth';


test('user can open own profile', async ({ page }) => {

    const user = await register(page);


    await page.goto(`/profile/${user.username}`);


    await expect(
        page.getByText(user.username)
    ).toBeVisible();

});
