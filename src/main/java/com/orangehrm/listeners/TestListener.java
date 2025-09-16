package com.orangehrm.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.orangehrm.utils.WebDriverFactory;
import org.testng.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class TestListener implements ITestListener, ISuiteListener {

    // Use singleton ExtentReports instance - thread-safe
    private static final ExtentReports extent = ExtentManager.getInstance();

    // ThreadLocal to ensure each thread has its own ExtentTest instance
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public void onStart(ISuite suite) {
        log.info("Starting test suite: {}", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("Finished test suite: {}", suite.getName());
        // Flush the reports at suite level to ensure all tests are written
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        log.info("Starting test: {}", testName);

        try {
            // Create test instance and store in ThreadLocal
            ExtentTest test = extent.createTest(testName, description);
            extentTest.set(test);

            // Add test categories/groups
            String[] groups = result.getMethod().getGroups();
            for (String group : groups) {
                test.assignCategory(group);
            }

        } catch (Exception e) {
            log.error("Error in onTestStart for test: {}", testName, e);
            // Create a fallback test to prevent null issues
            ExtentTest fallbackTest = extent.createTest(testName + "_fallback");
            extentTest.set(fallbackTest);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.info("Test passed: {}", testName);

        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.PASS, "Test passed successfully");
        } else {
            log.warn("ExtentTest is null for test: {}", testName);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("Test failed: {}", testName, result.getThrowable());

        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());

            // Capture screenshot on failure
            try {
                String screenshotPath = captureScreenshot(testName);
                if (screenshotPath != null && !screenshotPath.isEmpty()) {
                    test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                }
            } catch (Exception e) {
                log.error("Failed to capture screenshot for test: {}", testName, e);
                test.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
            }
        } else {
            log.warn("ExtentTest is null for failed test: {}", testName);
            // Create emergency test to log the failure
            try {
                ExtentTest emergencyTest = extent.createTest(testName + "_emergency");
                emergencyTest.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
            } catch (Exception e) {
                log.error("Failed to create emergency test for: {}", testName, e);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.warn("Test skipped: {}", testName);

        ExtentTest test = extentTest.get();
        if (test != null) {
            String skipReason = result.getThrowable() != null ?
                    result.getThrowable().getMessage() : "Test was skipped";
            test.log(Status.SKIP, "Test skipped: " + skipReason);
        } else {
            log.warn("ExtentTest is null for skipped test: {}", testName);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // Clean up ThreadLocal to prevent memory leaks
        extentTest.remove();
        log.info("Test context finished: {}", context.getName());
    }

    /**
     * Capture screenshot and return the file path
     * Enhanced with better error handling
     */
    private String captureScreenshot(String testName) {
        try {
            // Check if WebDriver is available
            if (WebDriverFactory.getDriver() == null) {
                log.warn("WebDriver is null, cannot capture screenshot for test: {}", testName);
                return null;
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) WebDriverFactory.getDriver();
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String screenshotPath = System.getProperty("user.dir") +
                    "/screenshots/" + testName + "_" + timestamp + ".png";

            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(screenshotPath).getParent());

            // Write screenshot to file
            Files.write(Paths.get(screenshotPath), screenshot);

            log.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath;

        } catch (Exception e) {
            log.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }
}