// src/test/java/com/orangehrm/tests/DataDrivenLoginTest.java
package com.orangehrm.tests;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.dataproviders.LoginDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.*;
import java.util.Map;

@Log4j2
@Epic("Authentication")
@Feature("Data-Driven Login Testing")
public class DataDrivenLoginTest extends BaseTest {

    @Test(dataProvider = "loginTestData", dataProviderClass = LoginDataProvider.class,
            description = "Data-driven login test with multiple credential combinations")
    @Description("This test verifies login functionality with various credential combinations")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithMultipleCredentials(String username, String password, boolean expectedResult) {
        log.info("Testing login with username: {}, Expected result: {}", username, expectedResult);

        LoginPage loginPage = new LoginPage();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        if (expectedResult) {
            // Valid login scenario
            DashboardPage dashboardPage = loginPage.login(username, password);
            Assert.assertTrue(dashboardPage.isPageLoaded(),
                    "Dashboard should be loaded for valid credentials");

            // Logout for next iteration
            dashboardPage.logout();
        } else {
            // Invalid login scenario
            loginPage.login(username, password);
            Assert.assertTrue(loginPage.isPageLoaded(),
                    "Should remain on login page for invalid credentials");
        }

        log.info("Login test completed for username: {}", username);
    }

    @Test(dataProvider = "validLoginData", dataProviderClass = LoginDataProvider.class,
            description = "Test valid login scenarios from Excel data")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidLoginFromExcel(Map<String, String> testData) {
        log.info("Testing valid login from Excel data: {}", testData.get("username"));

        LoginPage loginPage = new LoginPage();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        DashboardPage dashboardPage = loginPage.login(
                testData.get("username"),
                testData.get("password")
        );

        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard should be loaded");
        Assert.assertEquals(dashboardPage.getDashboardTitle(), testData.get("expectedTitle"));

        dashboardPage.logout();
        log.info("Valid login test completed for: {}", testData.get("username"));
    }
}