package com.orangehrm.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.Step;

@Log4j2
public class DashboardPage extends BasePage {

    @FindBy(className = "oxd-topbar-header-breadcrumb-module")
    private WebElement dashboardTitle;

    @FindBy(className = "oxd-userdropdown-tab")
    private WebElement userDropdown;

    @FindBy(xpath = "//a[text()='Logout']")
    private WebElement logoutLink;

    @FindBy(xpath = "//span[text()='PIM']")
    private WebElement pimMenu;

    @FindBy(xpath = "//span[text()='Admin']")
    private WebElement adminMenu;

    /**
     * Verify if dashboard page is loaded
     */
    @Override
    public boolean isPageLoaded() {
        try {
            return waitForVisible(dashboardTitle).isDisplayed() &&
                    getText(dashboardTitle).contains("Dashboard");
        } catch (Exception e) {
            log.error("Dashboard page not loaded", e);
            return false;
        }
    }

    /**
     * Get dashboard title text
     */
    @Step("Get dashboard title")
    public String getDashboardTitle() {
        return getText(dashboardTitle);
    }

    /**
     * Logout from application
     */
    @Step("Logout from application")
    public LoginPage logout() {
        log.info("Logging out from application");

        safeClick(userDropdown);
        safeClick(logoutLink);

        log.info("Logout completed");
        return new LoginPage();
    }

    /**
     * Navigate to PIM module
     */
    @Step("Navigate to PIM module")
    public void navigateToPIM() {
        log.info("Navigating to PIM module");
        safeClick(pimMenu);
    }

    /**
     * Navigate to Admin module
     */
    @Step("Navigate to Admin module")
    public void navigateToAdmin() {
        log.info("Navigating to Admin module");
        safeClick(adminMenu);
    }
}
