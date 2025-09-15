// src/main/java/com/orangehrm/listeners/ExtentManager.java
package com.orangehrm.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class ExtentManager {
    private static ExtentReports extent;
    private static String reportPath;

    /**
     * Thread-safe singleton pattern for ExtentReports
     * This ensures only one ExtentReports instance is created across all threads
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    /**
     * Create ExtentReports instance with proper configuration
     */
    private static void createInstance() {
        try {
            // Generate timestamped report path
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportPath = System.getProperty("user.dir") + "/reports/extent-reports/ExtentReport_" + timestamp + ".html";

            // Create directories if they don't exist
            Files.createDirectories(Paths.get(reportPath).getParent());

            // Configure Spark Reporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("OrangeHRM Automation Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setTimelineEnabled(true);

            // Create ExtentReports instance
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Add system information
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Test Environment", "QA");

            log.info("ExtentReports initialized successfully. Report path: {}", reportPath);

        } catch (IOException e) {
            log.error("Failed to initialize ExtentReports", e);
            throw new RuntimeException("ExtentReports initialization failed", e);
        }
    }

    /**
     * Get the report path (useful for logging or other purposes)
     */
    public static String getReportPath() {
        return reportPath;
    }
}
