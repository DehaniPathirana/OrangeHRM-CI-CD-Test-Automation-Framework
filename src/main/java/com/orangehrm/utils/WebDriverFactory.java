//package com.orangehrm.utils;
//
//import com.orangehrm.config.BrowserType;
//import com.orangehrm.config.ConfigurationManager;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.firefox.FirefoxOptions;
//import org.openqa.selenium.edge.EdgeDriver;
//import org.openqa.selenium.edge.EdgeOptions;
//import org.openqa.selenium.remote.RemoteWebDriver;
//import lombok.extern.log4j.Log4j2;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.time.Duration;
//
//@Log4j2
//public class WebDriverFactory {
//
//    private static final ConfigurationManager config = ConfigurationManager.getInstance();
//
//    // ThreadLocal for parallel execution support
//    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
//
//    /**
//     * Creates and returns WebDriver instance based on configuration
//     * Supports both local and remote execution
//     */
//    public static WebDriver createDriver(BrowserType browserType) {
//        WebDriver driver;
//
//        try {
//            if (config.isRemoteExecution()) {
//                driver = createRemoteDriver(browserType);
//            } else {
//                driver = createLocalDriver(browserType);
//            }
//
//            // Configure driver with common settings
//            configureDriver(driver);
//            driverThreadLocal.set(driver);
//
//            log.info("WebDriver created successfully for browser: {}", browserType);
//            return driver;
//
//        } catch (Exception e) {
//            log.error("Failed to create WebDriver for browser: {}", browserType, e);
//            throw new RuntimeException("WebDriver creation failed", e);
//        }
//    }
//
//    /**
//     * Creates local WebDriver instance
//     */
//    private static WebDriver createLocalDriver(BrowserType browserType) {
//        return switch (browserType) {
//            case CHROME -> {
//                WebDriverManager.chromedriver().setup();
//                yield new ChromeDriver(getChromeOptions());
//            }
//            case FIREFOX -> {
//                WebDriverManager.firefoxdriver().setup();
//                yield new FirefoxDriver(getFirefoxOptions());
//            }
//            case EDGE -> {
//                WebDriverManager.edgedriver().setup();
//                yield new EdgeDriver(getEdgeOptions());
//            }
//            default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
//        };
//    }
//
//    /**
//     * Creates remote WebDriver instance for Selenium Grid
//     */
//    private static WebDriver createRemoteDriver(BrowserType browserType) throws MalformedURLException {
//        URL hubUrl = new URL(config.getHubUrl());
//
//        return switch (browserType) {
//            case CHROME -> new RemoteWebDriver(hubUrl, getChromeOptions());
//            case FIREFOX -> new RemoteWebDriver(hubUrl, getFirefoxOptions());
//            case EDGE -> new RemoteWebDriver(hubUrl, getEdgeOptions());
//            default -> throw new IllegalArgumentException("Unsupported browser for remote execution: " + browserType);
//        };
//    }
//
//    /**
//     * Chrome browser options configuration
//     */
//    private static ChromeOptions getChromeOptions() {
//        ChromeOptions options = new ChromeOptions();
//
//        if (config.isHeadless()) {
//            options.addArguments("--headless=new");
//        }
//
//        // Performance and stability options
//        options.addArguments(
//                "--no-sandbox",
//                "--disable-dev-shm-usage",
//                "--disable-gpu",
//                "--disable-extensions",
//                "--disable-web-security",
//                "--allow-running-insecure-content",
//                "--window-size=1920,1080"
//        );
//
//        return options;
//    }
//
//    /**
//     * Firefox browser options configuration
//     */
//    private static FirefoxOptions getFirefoxOptions() {
//        FirefoxOptions options = new FirefoxOptions();
//
//        if (config.isHeadless()) {
//            options.addArguments("--headless");
//        }
//
//        options.addArguments("--window-size=1920,1080");
//        return options;
//    }
//
//    /**
//     * Edge browser options configuration
//     */
//    private static EdgeOptions getEdgeOptions() {
//        EdgeOptions options = new EdgeOptions();
//
//        if (config.isHeadless()) {
//            options.addArguments("--headless=new");
//        }
//
//        options.addArguments(
//                "--no-sandbox",
//                "--disable-dev-shm-usage",
//                "--window-size=1920,1080"
//        );
//
//        return options;
//    }
//
//    /**
//     * Configure driver with timeouts and window management
//     */
//    private static void configureDriver(WebDriver driver) {
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.manage().window().maximize();
//    }
//
//    /**
//     * Get current thread's WebDriver instance
//     */
//    public static WebDriver getDriver() {
//        WebDriver driver = driverThreadLocal.get();
//        if (driver == null) {
//            throw new IllegalStateException("WebDriver not initialized. Call createDriver() first.");
//        }
//        return driver;
//    }
//
//    /**
//     * Close current thread's WebDriver and clean up
//     */
//    public static void quitDriver() {
//        WebDriver driver = driverThreadLocal.get();
//        if (driver != null) {
//            try {
//                driver.quit();
//                log.info("WebDriver closed successfully");
//            } catch (Exception e) {
//                log.error("Error while closing WebDriver", e);
//            } finally {
//                driverThreadLocal.remove();
//            }
//        }
//    }
//}


package com.orangehrm.utils;

import com.orangehrm.config.BrowserType;
import com.orangehrm.config.ConfigurationManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Log4j2
public class WebDriverFactory {

    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    // ThreadLocal for parallel execution support
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Creates and returns WebDriver instance based on configuration
     * Enhanced with better error handling for driver downloads
     */
    public static WebDriver createDriver(BrowserType browserType) {
        WebDriver driver;

        try {
            if (config.isRemoteExecution()) {
                driver = createRemoteDriver(browserType);
            } else {
                driver = createLocalDriver(browserType);
            }

            // Configure driver with common settings
            configureDriver(driver);
            driverThreadLocal.set(driver);

            log.info("WebDriver created successfully for browser: {}", browserType);
            return driver;

        } catch (Exception e) {
            log.error("Failed to create WebDriver for browser: {}. Error: {}", browserType, e.getMessage());
            throw new RuntimeException("WebDriver creation failed for " + browserType + ": " + e.getMessage(), e);
        }
    }

    /**
     * Creates local WebDriver instance with enhanced error handling
     */
    private static WebDriver createLocalDriver(BrowserType browserType) {
        return switch (browserType) {
            case CHROME -> {
                try {
                    WebDriverManager.chromedriver().setup();
                    yield new ChromeDriver(getChromeOptions());
                } catch (Exception e) {
                    log.error("Failed to setup Chrome WebDriver: {}", e.getMessage());
                    throw new RuntimeException("Chrome WebDriver setup failed", e);
                }
            }
            case FIREFOX -> {
                try {
                    WebDriverManager.firefoxdriver().setup();
                    yield new FirefoxDriver(getFirefoxOptions());
                } catch (Exception e) {
                    log.error("Failed to setup Firefox WebDriver: {}", e.getMessage());
                    throw new RuntimeException("Firefox WebDriver setup failed", e);
                }
            }
            case EDGE -> {
                try {
                    log.info("Attempting to setup Edge WebDriver...");

                    // Try to setup Edge driver with timeout
                    WebDriverManager edgeManager = WebDriverManager.edgedriver();

                    // Set timeout for download operations
                    edgeManager.timeout(30).setup();

                    yield new EdgeDriver(getEdgeOptions());

                } catch (Exception e) {
                    log.error("Failed to setup Edge WebDriver. This might be due to network issues or missing driver: {}", e.getMessage());

                    // Provide helpful error message for common Edge issues
                    if (e.getMessage().contains("UnknownHostException") || e.getMessage().contains("msedgedriver.azureedge.net")) {
                        throw new RuntimeException(
                                "Edge WebDriver download failed due to network issues. " +
                                        "Please check your internet connection or manually download the Edge driver. " +
                                        "Alternatively, run tests with Chrome or Firefox only.", e);
                    }

                    throw new RuntimeException("Edge WebDriver setup failed", e);
                }
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
        };
    }

    /**
     * Creates remote WebDriver instance for Selenium Grid
     */
    private static WebDriver createRemoteDriver(BrowserType browserType) throws MalformedURLException {
        URL hubUrl = new URL(config.getHubUrl());

        return switch (browserType) {
            case CHROME -> new RemoteWebDriver(hubUrl, getChromeOptions());
            case FIREFOX -> new RemoteWebDriver(hubUrl, getFirefoxOptions());
            case EDGE -> new RemoteWebDriver(hubUrl, getEdgeOptions());
            default -> throw new IllegalArgumentException("Unsupported browser for remote execution: " + browserType);
        };
    }

    /**
     * Chrome browser options configuration
     */
    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }

        // Performance and stability options
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--disable-web-security",
                "--allow-running-insecure-content",
                "--disable-features=VizDisplayCompositor",
                "--window-size=1920,1080"
        );

        // Add logging preferences to reduce CDP warnings
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});
        options.setExperimentalOption("useAutomationExtension", false);

        return options;
    }

    /**
     * Firefox browser options configuration
     */
    private static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless");
        }

        options.addArguments("--window-size=1920,1080");

        // Disable logging to reduce console noise
        options.addPreference("devtools.console.stdout.enabled", false);

        return options;
    }

    /**
     * Edge browser options configuration
     */
    private static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080"
        );

        return options;
    }

    /**
     * Configure driver with timeouts and window management
     */
    private static void configureDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();

        log.debug("Driver configured with implicit wait: {}s, page load timeout: 30s", config.getImplicitWait());
    }

    /**
     * Get current thread's WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized. Call createDriver() first.");
        }
        return driver;
    }

    /**
     * Close current thread's WebDriver and clean up
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver closed successfully");
            } catch (Exception e) {
                log.error("Error while closing WebDriver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}