// src/test/java/com/orangehrm/tests/EmployeeManagementTest.java
package com.orangehrm.tests;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.PIMPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.*;

@Log4j2
@Epic("Employee Management")
@Feature("PIM Module")
public class EmployeeManagementTest extends BaseTest {

    @Test(priority = 1, groups = {"smoke", "regression"})
    @Description("Test adding a new employee to the system")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Add Employee")
    public void testAddNewEmployee() {
        log.info("Starting add new employee test");

        // Login to application
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login("Admin", "admin123");
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard should be loaded");

        // Navigate to PIM
        dashboardPage.navigateToPIM();
        PIMPage pimPage = new PIMPage();
        Assert.assertTrue(pimPage.isPageLoaded(), "PIM page should be loaded");

        // Add new employee
        String firstName = "John";
        String lastName = "Doe";
        String employeeId = "EMP" + System.currentTimeMillis();

        pimPage.addNewEmployee(firstName, lastName, employeeId);

        // Verify employee was added
        Assert.assertTrue(pimPage.isEmployeeAdded(employeeId),
                "Employee should be added successfully");

        log.info("Add new employee test completed successfully");
    }

    @Test(priority = 2, groups = {"regression"}, dependsOnMethods = {"testAddNewEmployee"})
    @Description("Test searching for an existing employee")
    @Severity(SeverityLevel.NORMAL)
    @Story("Search Employee")
    public void testSearchEmployee() {
        log.info("Starting search employee test");

        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login("Admin", "admin123");
        dashboardPage.navigateToPIM();

        PIMPage pimPage = new PIMPage();

        // Search for employee
        String searchTerm = "John";
        pimPage.searchEmployee(searchTerm);

        // Verify search results
        Assert.assertTrue(pimPage.areSearchResultsDisplayed(),
                "Search results should be displayed");

        log.info("Search employee test completed successfully");
    }
}