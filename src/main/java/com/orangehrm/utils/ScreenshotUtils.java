// src/main/java/com/orangehrm/utils/ScreenshotUtils.java
package com.orangehrm.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = System.getProperty("user.dir") + "/screenshots/";

    /**
     * Capture screenshot with timestamp
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshot directory if it doesn't exist
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String screenshotPath = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";

            Files.copy(sourceFile.toPath(), Paths.get(screenshotPath));

            log.info("Screenshot captured successfully: {}", screenshotPath);
            return screenshotPath;

        } catch (IOException e) {
            log.error("Failed to capture screenshot for test: {}", testName, e);
            throw new RuntimeException("Screenshot capture failed", e);
        }
    }

    /**
     * Capture screenshot as byte array for Allure reports
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as bytes", e);
            return new byte[0];
        }
    }
}