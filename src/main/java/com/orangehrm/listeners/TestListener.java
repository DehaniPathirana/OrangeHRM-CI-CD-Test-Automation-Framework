//// src/main/java/com/orangehrm/listeners/TestListener.java
//package com.orangehrm.listeners;
//
//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.Status;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import com.aventstack.extentreports.reporter.configuration.Theme;
//import com.orangehrm.utils.WebDriverFactory;
//import org.testng.*;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import lombok.extern.log4j.Log4j2;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Log4j2
//public class TestListener implements ITestListener, ISuiteListener {
//
//    private ExtentReports extent;
//    private final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
//    private String reportPath;
//
//    @Override
//    public void onStart(ISuite suite) {
//        log.info("Starting test suite: {}", suite.getName());
//        setupExtentReports();
//    }
//
//    @Override
//    public void onFinish(ISuite suite) {
//        log.info("Finished test suite: {}", suite.getName());
//        if (extent != null) {
//            extent.flush();
//        }
//    }
//
//    @Override
//    public void onTestStart(ITestResult result) {
//        log.info("Starting test: {}", result.getMethod().getMethodName());
//
//        String testName = result.getMethod().getMethodName();
//        String description = result.getMethod().getDescription();
//
//        ExtentTest extentTest = extent.createTest(testName, description);
//        test.set(extentTest);
//
//        // Add test categories/groups
//        String[] groups = result.getMethod().getGroups();
//        for (String group : groups) {
//            extentTest.assignCategory(group);
//        }
//    }
//
//    @Override
//    public void onTestSuccess(ITestResult result) {
//        log.info("Test passed: {}", result.getMethod().getMethodName());
//        test.get().log(Status.PASS, "Test passed successfully");
//    }
//
//    @Override
//    public void onTestFailure(ITestResult result) {
//        log.error("Test failed: {}", result.getMethod().getMethodName(), result.getThrowable());
//
//        ExtentTest extentTest = test.get();
//        extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
//
//        // Capture screenshot on failure
//        try {
//            String screenshotPath = captureScreenshot(result.getMethod().getMethodName());
//            extentTest.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
//        } catch (Exception e) {
//            log.error("Failed to capture screenshot", e);
//        }
//    }
//
//    @Override
//    public void onTestSkipped(ITestResult result) {
//        log.warn("Test skipped: {}", result.getMethod().getMethodName());
//        test.get().log(Status.SKIP, "Test skipped: " + result.getThrowable().getMessage());
//    }
//
//    /**
//     * Setup ExtentReports configuration
//     */
//    private void setupExtentReports() {
//        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
//        reportPath = System.getProperty("user.dir") + "/reports/extent-reports/ExtentReport_" + timestamp + ".html";
//
//        // Create directories if they don't exist
//        try {
//            Files.createDirectories(Paths.get(reportPath).getParent());
//        } catch (IOException e) {
//            log.error("Failed to create report directory", e);
//        }
//
//        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
//        sparkReporter.config().setTheme(Theme.DARK);
//        sparkReporter.config().setDocumentTitle("OrangeHRM Automation Report");
//        sparkReporter.config().setReportName("Test Execution Report");
//        sparkReporter.config().setTimelineEnabled(true);
//
//        extent = new ExtentReports();
//        extent.attachReporter(sparkReporter);
//
//        // System information
//        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
//        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
//        extent.setSystemInfo("User", System.getProperty("user.name"));
//        extent.setSystemInfo("Test Environment", "QA");
//
//        log.info("ExtentReports initialized. Report will be generated at: {}", reportPath);
//    }
//
//    /**
//     * Capture screenshot and return the file path
//     */
//    private String captureScreenshot(String testName) {
//        try {
//            TakesScreenshot takesScreenshot = (TakesScreenshot) WebDriverFactory.getDriver();
//            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
//
//            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
//            String screenshotPath = System.getProperty("user.dir") +
//                    "/screenshots/" + testName + "_" + timestamp + ".png";
//
//            // Create directory if it doesn't exist
//            Files.createDirectories(Paths.get(screenshotPath).getParent());
//
//            // Write screenshot to file
//            Files.write(Paths.get(screenshotPath), screenshot);
//
//            log.info("Screenshot captured: {}", screenshotPath);
//            return screenshotPath;
//
//        } catch (Exception e) {
//            log.error("Failed to capture screenshot for test: {}", testName, e);
//            throw new RuntimeException("Screenshot capture failed", e);
//        }
//    }
//}


// src/main/java/com/orangehrm/listeners/TestListener.java
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