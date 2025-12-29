package com.flipkart.core.healing;

import com.flipkart.core.config.ConfigManager;

/**
 * Configuration for AI Self-Healing behavior
 */
public class HealingConfig {

    private static HealingConfig instance;

    private boolean enabled;
    private String serviceUrl;
    private int maxRetries;
    private double confidenceThreshold;
    private String locatorsDir;
    private int connectionTimeout;
    private int readTimeout;

    private HealingConfig() {
        loadFromProperties();
    }

    /**
     * Singleton instance
     */
    public static synchronized HealingConfig getInstance() {
        if (instance == null) {
            instance = new HealingConfig();
        }
        return instance;
    }

    /**
     * Load configuration from config.properties
     */
    private void loadFromProperties() {
        ConfigManager config = ConfigManager.getInstance();

        this.enabled = Boolean.parseBoolean(
                getPropertyOrDefault(config, "healing.enabled", "true"));
        this.serviceUrl = getPropertyOrDefault(config, "healing.serviceUrl", "http://localhost:3001");
        this.maxRetries = Integer.parseInt(
                getPropertyOrDefault(config, "healing.maxRetries", "3"));
        this.confidenceThreshold = Double.parseDouble(
                getPropertyOrDefault(config, "healing.confidenceThreshold", "0.7"));
        this.locatorsDir = getPropertyOrDefault(config, "healing.locatorsDir", "../shared/locators");
        this.connectionTimeout = Integer.parseInt(
                getPropertyOrDefault(config, "healing.connectionTimeout", "5000"));
        this.readTimeout = Integer.parseInt(
                getPropertyOrDefault(config, "healing.readTimeout", "30000"));
    }

    private String getPropertyOrDefault(ConfigManager config, String key, String defaultValue) {
        try {
            String value = config.getProperty(key);
            return value != null && !value.isEmpty() ? value : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // Getters
    public boolean isEnabled() {
        return enabled;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public String getLocatorsDir() {
        return locatorsDir;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    // API endpoint helpers
    public String getHealEndpoint() {
        return serviceUrl + "/api/heal";
    }

    public String getValidateEndpoint() {
        return serviceUrl + "/api/validate";
    }

    public String getHealthEndpoint() {
        return serviceUrl + "/api/health";
    }

    @Override
    public String toString() {
        return "HealingConfig{" +
                "enabled=" + enabled +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", maxRetries=" + maxRetries +
                ", confidenceThreshold=" + confidenceThreshold +
                '}';
    }

    /**
     * Reset instance (useful for testing)
     */
    public static void reset() {
        instance = null;
    }
}
