package com.orangehrm.tests;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.*;

@Log4j2
@Epic("Authentication")
@Feature("Login Functionality")
public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Verify successful login with valid credentials")
    @Description("This test verifies that user can login with valid username and password")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Valid Login")
    public void testValidLogin() {
        log.info("Starting valid login test");

        // Initialize page objects
        LoginPage loginPage = new LoginPage();

        // Verify login page is loaded
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Perform login
        DashboardPage dashboardPage = loginPage.login("Admin", "admin123");

        // Verify successful login
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard should be loaded after login");
        Assert.assertTrue(dashboardPage.getDashboardTitle().contains("Dashboard"),
                "Dashboard title should contain 'Dashboard'");

        log.info("Valid login test completed successfully");
    }

    @Test(priority = 2, description = "Verify login fails with invalid credentials")
    @Description("This test verifies that login fails with invalid username and password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Invalid Login")
    public void testInvalidLogin() {
        log.info("Starting invalid login test");

        LoginPage loginPage = new LoginPage();

        // Verify login page is loaded
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Attempt login with invalid credentials
        loginPage.login("InvalidUser", "InvalidPassword");

        // Verify login failure
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Invalid credentials"),
                "Error message should indicate invalid credentials");

        log.info("Invalid login test completed successfully");
    }

    @Test(priority = 3, description = "Verify login fails with empty credentials")
    @Severity(SeverityLevel.NORMAL)
    @Story("Empty Credentials")
    public void testEmptyCredentials() {
        log.info("Starting empty credentials test");

        LoginPage loginPage = new LoginPage();

        // Verify login page is loaded
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Attempt login with empty credentials
        loginPage.login("", "");

        // Verify validation messages appear
        Assert.assertTrue(loginPage.isPageLoaded(), "Should remain on login page");

        log.info("Empty credentials test completed successfully");
    }
}