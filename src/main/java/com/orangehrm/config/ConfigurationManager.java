package com.orangehrm.config;

import lombok.Getter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class ConfigurationManager {
    private static ConfigurationManager instance;
    private final Properties properties;

    // Singleton pattern for configuration management
    private ConfigurationManager() {
        properties = new Properties();
        loadProperties();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config/config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    // Helper methods for common configurations
    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public BrowserType getBrowserType() {
        String browser = properties.getProperty("browser", "CHROME");
        return BrowserType.valueOf(browser.toUpperCase());
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }

    public int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait", "10"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicit.wait", "15"));
    }

    public boolean isRemoteExecution() {
        return Boolean.parseBoolean(properties.getProperty("remote.execution", "false"));
    }

    public String getHubUrl() {
        return properties.getProperty("hub.url", "http://localhost:4444");
    }
}