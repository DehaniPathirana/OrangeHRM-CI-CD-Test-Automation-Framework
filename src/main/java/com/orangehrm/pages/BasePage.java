package com.orangehrm.pages;

import com.orangehrm.config.ConfigurationManager;
import com.orangehrm.utils.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;

@Log4j2
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor jsExecutor;
    private final ConfigurationManager config = ConfigurationManager.getInstance();

    public BasePage() {
        this.driver = WebDriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        this.jsExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Wait for element to be clickable
     */
    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to be visible
     */
    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Safe click with wait
     */
    protected void safeClick(WebElement element) {
        try {
            waitForClickable(element).click();
            log.debug("Clicked on element: {}", element);
        } catch (Exception e) {
            log.error("Failed to click on element: {}", element, e);
            throw e;
        }
    }

    /**
     * Safe text input with clear
     */
    protected void safeType(WebElement element, String text) {
        try {
            WebElement visibleElement = waitForVisible(element);
            visibleElement.clear();
            visibleElement.sendKeys(text);
            log.debug("Typed '{}' into element: {}", text, element);
        } catch (Exception e) {
            log.error("Failed to type '{}' into element: {}", text, element, e);
            throw e;
        }
    }

    /**
     * Get text from element with wait
     */
    protected String getText(WebElement element) {
        try {
            return waitForVisible(element).getText();
        } catch (Exception e) {
            log.error("Failed to get text from element: {}", element, e);
            throw e;
        }
    }

    /**
     * Scroll to element
     */
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Check if page is loaded by verifying page title or specific element
     */
    public abstract boolean isPageLoaded();
}