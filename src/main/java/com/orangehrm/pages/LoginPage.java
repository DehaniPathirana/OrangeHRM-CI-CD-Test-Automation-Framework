package com.orangehrm.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.Step;

@Log4j2
public class LoginPage extends BasePage {

    // Page Elements using Page Factory
    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    @FindBy(className = "oxd-alert-content-text")
    private WebElement errorMessage;

    @FindBy(className = "orangehrm-login-branding")
    private WebElement loginBranding;

    /**
     * Verify if login page is loaded
     */
    @Override
    public boolean isPageLoaded() {
        try {
            return waitForVisible(loginBranding).isDisplayed() &&
                    waitForVisible(usernameField).isDisplayed();
        } catch (Exception e) {
            log.error("Login page not loaded", e);
            return false;
        }
    }

    /**
     * Perform login with given credentials
     */
    @Step("Login with username: {username}")
    public DashboardPage login(String username, String password) {
        log.info("Attempting to login with username: {}", username);

        safeType(usernameField, username);
        safeType(passwordField, password);
        safeClick(loginButton);

        log.info("Login attempt completed for username: {}", username);
        return new DashboardPage();
    }

    /**
     * Get login error message
     */
    @Step("Get login error message")
    public String getErrorMessage() {
        try {
            return getText(errorMessage);
        } catch (Exception e) {
            log.debug("No error message found");
            return "";
        }
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
