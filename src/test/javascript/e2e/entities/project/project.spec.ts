/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { ProjectComponentsPage, ProjectDeleteDialog, ProjectUpdatePage } from './project.page-object';

const expect = chai.expect;

describe('Project e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let projectUpdatePage: ProjectUpdatePage;
    let projectComponentsPage: ProjectComponentsPage;
    let projectDeleteDialog: ProjectDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load Projects', async () => {
        await navBarPage.goToEntity('project');
        projectComponentsPage = new ProjectComponentsPage();
        expect(await projectComponentsPage.getTitle()).to.eq('tfTestApp.project.home.title');
    });

    it('should load create Project page', async () => {
        await projectComponentsPage.clickOnCreateButton();
        projectUpdatePage = new ProjectUpdatePage();
        expect(await projectUpdatePage.getPageTitle()).to.eq('tfTestApp.project.home.createOrEditLabel');
        await projectUpdatePage.cancel();
    });

    it('should create and save Projects', async () => {
        const nbButtonsBeforeCreate = await projectComponentsPage.countDeleteButtons();

        await projectComponentsPage.clickOnCreateButton();
        await promise.all([projectUpdatePage.setNameInput('name')]);
        expect(await projectUpdatePage.getNameInput()).to.eq('name');
        await projectUpdatePage.save();
        expect(await projectUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await projectComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last Project', async () => {
        const nbButtonsBeforeDelete = await projectComponentsPage.countDeleteButtons();
        await projectComponentsPage.clickOnLastDeleteButton();

        projectDeleteDialog = new ProjectDeleteDialog();
        expect(await projectDeleteDialog.getDialogTitle()).to.eq('tfTestApp.project.delete.question');
        await projectDeleteDialog.clickOnConfirmButton();

        expect(await projectComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
