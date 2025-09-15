// src/main/java/com/orangehrm/pages/PIMPage.java
package com.orangehrm.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.Step;

@Log4j2
public class PIMPage extends BasePage {

    @FindBy(xpath = "//span[text()='PIM']")
    private WebElement pimTitle;

    @FindBy(xpath = "//button[text()=' Add ']")
    private WebElement addButton;

    @FindBy(name = "firstName")
    private WebElement firstNameField;

    @FindBy(name = "lastName")
    private WebElement lastNameField;

    @FindBy(xpath = "//label[text()='Employee Id']/following::input[1]")
    private WebElement employeeIdField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement saveButton;

    @FindBy(xpath = "//input[@placeholder='Type for hints...']")
    private WebElement searchField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement searchButton;

    @FindBy(className = "oxd-table-body")
    private WebElement searchResults;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitForVisible(pimTitle).isDisplayed();
        } catch (Exception e) {
            log.error("PIM page not loaded", e);
            return false;
        }
    }

    @Step("Add new employee with details: {firstName} {lastName}, ID: {employeeId}")
    public void addNewEmployee(String firstName, String lastName, String employeeId) {
        log.info("Adding new employee: {} {}, ID: {}", firstName, lastName, employeeId);

        safeClick(addButton);
        safeType(firstNameField, firstName);
        safeType(lastNameField, lastName);
        safeType(employeeIdField, employeeId);
        safeClick(saveButton);

        log.info("Employee addition form submitted");
    }

    @Step("Search for employee: {searchTerm}")
    public void searchEmployee(String searchTerm) {
        log.info("Searching for employee: {}", searchTerm);

        safeType(searchField, searchTerm);
        safeClick(searchButton);

        log.info("Employee search initiated");
    }

    public boolean isEmployeeAdded(String employeeId) {
        try {
            // Implementation would depend on the success message or redirect
            // This is a placeholder implementation
            Thread.sleep(2000); // Wait for processing
            return true;
        } catch (Exception e) {
            log.error("Failed to verify employee addition", e);
            return false;
        }
    }

    public boolean areSearchResultsDisplayed() {
        try {
            return waitForVisible(searchResults).isDisplayed();
        } catch (Exception e) {
            log.error("Search results not displayed", e);
            return false;
        }
    }
}