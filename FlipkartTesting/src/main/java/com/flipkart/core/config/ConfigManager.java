package com.flipkart.core.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager - Singleton configuration manager for the framework.
 * Loads properties from config files and provides type-safe getters.
 * Supports environment-specific configurations.
 */
public class ConfigManager {
    
    private static ConfigManager instance;
    private Properties properties;
    private String environment;
    
    // Config file paths
    private static final String CONFIG_PATH = "config/config.properties";
    
    // Private constructor for Singleton pattern
    private ConfigManager() {
        properties = new Properties();
        loadConfig();
    }
    
    /**
     * Get singleton instance of ConfigManager
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load configuration from properties file
     */
    private void loadConfig() {
        try {
            // Try loading from classpath (resources folder)
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH);
            
            if (inputStream == null) {
                // Fallback to old path for backward compatibility
                inputStream = getClass().getClassLoader().getResourceAsStream("Properties/Config.properties");
            }
            
            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
                System.out.println("Configuration loaded successfully from classpath");
            } else {
                throw new FileNotFoundException("Config file not found in classpath");
            }
            
            // Set environment (default to 'test' if not specified)
            this.environment = getProperty("environment", "test");
            
        } catch (IOException e) {
            System.err.println("Error loading config file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Reload configuration (useful for runtime config changes)
     */
    public void reload() {
        properties.clear();
        loadConfig();
    }
    
    // ==================== Generic Getters ====================
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value or null
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("Warning: Property '" + key + "' not found in config");
        }
        return value;
    }
    
    /**
     * Get property with default value
     * @param key Property key
     * @param defaultValue Default if not found
     * @return Property value or default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get integer property
     * @param key Property key
     * @param defaultValue Default if not found or parse error
     * @return Integer value
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get boolean property
     * @param key Property key
     * @param defaultValue Default if not found
     * @return Boolean value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }
    
    // ==================== Application-Specific Getters ====================
    
    /**
     * Get the base URL for the application
     */
    public String getBaseUrl() {
        return getProperty("url", "https://www.flipkart.com/");
    }
    
    /**
     * Get the login URL
     */
    public String getLoginUrl() {
        return getProperty("loginURL", "https://www.flipkart.com/account/login");
    }
    
    /**
     * Get the phone number for authentication
     */
    public String getPhoneNumber() {
        return getProperty("PhoneNo");
    }
    
    /**
     * Get the product name for search tests
     */
    public String getProductName() {
        return getProperty("product", "iPhone");
    }
    
    /**
     * Get pincode for delivery tests
     */
    public String getPincode() {
        return getProperty("pincode", "560022");
    }
    
    /**
     * Get departure city for flight tests
     */
    public String getDepartureCity() {
        return getProperty("departureCity", "Mumbai");
    }
    
    /**
     * Get arrival city for flight tests
     */
    public String getArrivalCity() {
        return getProperty("arrivalCity", "Delhi");
    }
    
    /**
     * Get flight booking link
     */
    public String getFlightLink() {
        return getProperty("flightLink", "https://www.flipkart.com/travel/flights");
    }
    
    /**
     * Get product link for direct navigation
     */
    public String getProductLink() {
        return getProperty("productLink");
    }
    
    /**
     * Get filter/search link
     */
    public String getFilterLink() {
        return getProperty("filterLink");
    }
    
    /**
     * Get grocery section link
     */
    public String getGroceryLink() {
        return getProperty("groceryLink");
    }
    
    /**
     * Get the current environment
     */
    public String getEnvironment() {
        return environment;
    }
    
    /**
     * Get default wait timeout in seconds
     */
    public int getDefaultTimeout() {
        return getIntProperty("defaultTimeout", 15);
    }
    
    /**
     * Get page load timeout in seconds
     */
    public int getPageLoadTimeout() {
        return getIntProperty("pageLoadTimeout", 30);
    }
    
    /**
     * Check if screenshots should be taken on failure
     */
    public boolean shouldTakeScreenshotOnFailure() {
        return getBooleanProperty("screenshotOnFailure", true);
    }
    
    /**
     * Get browser name from config
     */
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
}
