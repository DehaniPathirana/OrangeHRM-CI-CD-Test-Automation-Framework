package com.orangehrm.base;

import com.orangehrm.config.BrowserType;
import com.orangehrm.config.ConfigurationManager;
import com.orangehrm.utils.WebDriverFactory;
import com.orangehrm.listeners.TestListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.*;
import lombok.extern.log4j.Log4j2;
import io.qameta.allure.Attachment;

@Log4j2
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected ConfigurationManager config = ConfigurationManager.getInstance();

    /**
     * Setup method to initialize WebDriver before each test method
     * Supports parallel execution with ThreadLocal
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        try {
            // Determine browser type from parameter or configuration
            BrowserType browserType = (browser != null)
                    ? BrowserType.valueOf(browser.toUpperCase())
                    : config.getBrowserType();

            log.info("Starting test setup with browser: {}", browserType);

            // Create WebDriver instance
            WebDriverFactory.createDriver(browserType);

            // Navigate to base URL
            WebDriverFactory.getDriver().get(config.getBaseUrl());

            log.info("Test setup completed successfully");

        } catch (Exception e) {
            log.error("Test setup failed", e);
            throw new RuntimeException("Failed to setup test", e);
        }
    }

    /**
     * Cleanup method to close WebDriver after each test method
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            log.info("Starting test cleanup");
            WebDriverFactory.quitDriver();
            log.info("Test cleanup completed successfully");
        } catch (Exception e) {
            log.error("Test cleanup failed", e);
        }
    }

    /**
     * Suite level setup for reporting and configurations
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("Starting test suite execution");
        // Additional suite-level setup can be added here
    }

    /**
     * Suite level cleanup
     */
    @AfterSuite(alwaysRun = true)
    public void suiteCleanup() {
        log.info("Test suite execution completed");
        // Additional suite-level cleanup can be added here
    }

    /**
     * Helper method to attach screenshots to Allure reports
     */
    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        return ((TakesScreenshot) WebDriverFactory.getDriver())
                .getScreenshotAs(OutputType.BYTES);
    }
}