/* tslint:disable no-unused-expression */
import { browser, element, by, $ } from 'protractor';

import SignInPage from '../../page-objects/signin-page';
import NavBarPage from '../../page-objects/navbar-page';
import RegisterPage from '../../page-objects/register-page';
import PasswordPage from '../../page-objects/password-page';
import SettingsPage from '../../page-objects/settings-page';
import {
  getUserDeactivatedButtonByLogin,
  getModifiedDateSortButton,
  waitUntilDisplayed,
  waitUntilHidden,
  getUserDeleteButtonByLogin,
  getUserDetailsButtonByLogin,
  getUserEditButtonByLogin,
  getUserEmailByLogin,
  getSuccessToast,
  getInfoToast,
  getDangerToast
} from '../../util/utils';

const expect = chai.expect;

describe('Account', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let passwordPage: PasswordPage;
  let settingsPage: SettingsPage;
  let registerPage: RegisterPage;

  const registerPageTitle = 'register-title';
  const passwordPageTitle = 'password-title';
  const settingsPageTitle = 'settings-title';
  const loginPageTitle = 'login-title';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
  });

  it('should fail to login with bad password', async () => {
    // Login page should appear
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('foo');
    await signInPage.loginButton.click();

    // Login page should stay open when login fails
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);
  });

  it('should login with admin account', async () => {
    await signInPage.username.clear();
    await signInPage.username.sendKeys('admin');
    await signInPage.password.clear();
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    // Login page should close when login success
    expect(await signInPage.isHidden()()).to.be.true;
    await navBarPage.autoSignOut();
  });

  it('should be able to sign up', async () => {
    await browser.get('/');
    await waitUntilDisplayed(navBarPage.accountMenu);

    registerPage = await navBarPage.getRegisterPage();
    await registerPage.waitUntilDisplayed();
    expect(await registerPage.getTitle()).to.eq(registerPageTitle);

    await registerPage.autoSignUpUsing('user_test', 'admin@localhost.jh', 'user_test');
    const toast = getSuccessToast();
    await waitUntilDisplayed(toast);

    // Success toast should appear
    expect(await toast.isPresent()).to.be.true;
  });

  it('should load user management', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    await waitUntilDisplayed(navBarPage.adminMenu);

    await navBarPage.clickOnAdminMenuItem('user-management');
    const title = element(by.id('user-management-page-heading'));
    await waitUntilDisplayed(title);

    expect(await title.isPresent()).to.be.true;
  });

  it('should activate the new registered user', async () => {
    expect(await element(by.id('user-management-page-heading')).isPresent()).to.be.true;

    const modifiedDateSortButton = getModifiedDateSortButton();
    await waitUntilDisplayed(modifiedDateSortButton);
    await modifiedDateSortButton.click();

    const deactivatedButton = getUserDeactivatedButtonByLogin('user_test');
    await waitUntilDisplayed(deactivatedButton);
    await deactivatedButton.click();
    await waitUntilHidden(deactivatedButton);

    // Deactivated button should disappear
    expect(await deactivatedButton.isPresent()).to.be.false;
    await navBarPage.autoSignOut();
  });

  it('should not be able to sign up if login already taken', async () => {
    await browser.get('/');
    await registerPage.get();
    expect(await registerPage.getTitle()).to.eq(registerPageTitle);

    await registerPage.autoSignUpUsing('user_test', 'admin@localhost.jh', 'user_test');
    const toast = getDangerToast();
    await waitUntilDisplayed(toast);

    // Error toast should appear
    expect(await toast.isPresent()).to.be.true;
  });

  it('should not be able to sign up if email already taken', async () => {
    expect(await registerPage.getTitle()).to.eq(registerPageTitle);

    await registerPage.username.sendKeys('_jhi');
    await registerPage.saveButton.click();
    const toast = getDangerToast();
    await waitUntilDisplayed(toast);

    // Error toast should appear
    expect(await toast.isPresent()).to.be.true;
  });

  it('should be able to log in with new registered account', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('user_test');
    await signInPage.password.sendKeys('user_test');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    // Login page should close when login success
    expect(await signInPage.isHidden()()).to.be.true;
    await navBarPage.autoSignOut();
  });

  it('should login with admin account', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    expect(await signInPage.isHidden()()).to.be.true;
  });

  it('should fail to update password when using incorrect current password', async () => {
    passwordPage = await navBarPage.getPasswordPage();
    await passwordPage.waitUntilDisplayed();
    expect(await passwordPage.getTitle()).to.eq(passwordPageTitle);

    await passwordPage.autoChangePassword('bad_password', 'new_password', 'new_password');
    const toast = getDangerToast();
    await waitUntilDisplayed(toast);

    // Error toast should appear
    expect(await toast.isPresent()).to.be.true;
    await navBarPage.autoSignOut();
  });

  it('should login with admin account', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    expect(await signInPage.isHidden()()).to.be.true;
  });

  it('should be able to update password', async () => {
    passwordPage = await navBarPage.getPasswordPage();
    await passwordPage.waitUntilDisplayed();
    expect(await passwordPage.getTitle()).to.eq(passwordPageTitle);

    await passwordPage.autoChangePassword('admin', 'new_password', 'new_password');
    const toast = getSuccessToast();
    await waitUntilDisplayed(toast);

    // Success toast should appear
    expect(await toast.isPresent()).to.be.true;
    await navBarPage.autoSignOut();
  });

  it('should be able to log in with new password', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('new_password');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    expect(await signInPage.isHidden()()).to.be.true;

    // change back to default
    await passwordPage.get();
    expect(await passwordPage.getTitle()).to.eq(passwordPageTitle);

    await passwordPage.autoChangePassword('new_password', 'admin', 'admin');

    await navBarPage.autoSignOut();
  });

  it('should login with user_test account', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('user_test');
    await signInPage.password.sendKeys('user_test');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    expect(await signInPage.isHidden()()).to.be.true;
  });

  it('should be able to change user_test settings', async () => {
    settingsPage = await navBarPage.getSettingsPage();
    await settingsPage.waitUntilDisplayed();

    expect(await settingsPage.getTitle()).to.eq(settingsPageTitle);

    await settingsPage.firstName.sendKeys('jhipster');
    await settingsPage.lastName.sendKeys('retspihj');
    await settingsPage.saveButton.click();

    const toast = getSuccessToast();
    await waitUntilDisplayed(toast);

    // Success toast should appear
    expect(await toast.isPresent()).to.be.true;
    await navBarPage.autoSignOut();
  });

  it('should login with admin account', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    expect(await signInPage.getTitle()).to.eq(loginPageTitle);

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    expect(await signInPage.isHidden()()).to.be.true;
  });

  it('should not be able to change admin settings if email already exists', async () => {
    settingsPage = await navBarPage.getSettingsPage();
    await settingsPage.waitUntilDisplayed();
    expect(await settingsPage.getTitle()).to.eq(settingsPageTitle);

    await settingsPage.setEmail('.jh');
    await settingsPage.save();

    const toast = getDangerToast();
    await waitUntilDisplayed(toast);

    // Error toast should appear
    expect(await toast.isPresent()).to.be.true;
  });

  it('should preview details from previously created fake user', async () => {
    await browser.get('#/admin/user-management');
    const detailsBtn = getUserDetailsButtonByLogin('user_test');
    await waitUntilDisplayed(detailsBtn);
    await detailsBtn.click();

    const detailsHeading = element(by.css('h2.jh-entity-heading'));
    await waitUntilDisplayed(detailsHeading);
    const loginDetails = element.all(by.css('.jh-entity-details > dd > span')).first();
    expect(await loginDetails.getText()).to.eq('user_test');
  });

  it('should edit details from previously created fake user', async () => {
    await browser.get('#/admin/user-management');
    const editBtn = getUserEditButtonByLogin('user_test');
    await waitUntilDisplayed(editBtn);
    await editBtn.click();

    const editHeading = element(by.css('h2#myUserLabel'));
    await waitUntilDisplayed(editHeading);
    await $('input#email').sendKeys('ipster');
    const saveBtn = element(by.css('.btn-primary'));
    await saveBtn.click();
    const toast = getInfoToast();
    await waitUntilDisplayed(toast);
    // Info toast should appear
    expect(await toast.isPresent()).to.be.true;

    const userEmail = getUserEmailByLogin('user_test');
    expect(await userEmail.getText()).to.eq('admin@localhost.jhipster');
  });

  it('should delete previously created fake user', async () => {
    await browser.get('#/admin/user-management');
    const deleteBtn = getUserDeleteButtonByLogin('user_test');
    await waitUntilDisplayed(deleteBtn);
    await deleteBtn.click();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    await element(by.buttonText('OK')).click();
    await waitUntilHidden(deleteModal);

    // Delete modal should disappear
    expect(await deleteModal.isDisplayed()).to.be.false;

    const toast = getDangerToast();
    // Danger toast should appear
    expect(await toast.isPresent()).to.be.true;
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
