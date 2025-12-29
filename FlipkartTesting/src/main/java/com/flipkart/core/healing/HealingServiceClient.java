package com.flipkart.core.healing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP Client for communicating with the AI Healing Service
 * Uses Java 11 HttpClient for non-blocking requests
 */
public class HealingServiceClient {

    private static final Logger logger = LogManager.getLogger(HealingServiceClient.class);
    private static HealingServiceClient instance;

    private final HttpClient httpClient;
    private final Gson gson;
    private final HealingConfig config;
    private boolean serviceAvailable = false;

    private HealingServiceClient() {
        this.config = HealingConfig.getInstance();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectionTimeout()))
                .build();
        this.gson = new GsonBuilder().create();

        // Check service availability on init
        checkServiceHealth();
    }

    /**
     * Singleton instance
     */
    public static synchronized HealingServiceClient getInstance() {
        if (instance == null) {
            instance = new HealingServiceClient();
        }
        return instance;
    }

    /**
     * Check if the healing service is available
     */
    public boolean checkServiceHealth() {
        if (!config.isEnabled()) {
            logger.debug("Healing is disabled in configuration");
            return false;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getHealthEndpoint()))
                    .timeout(Duration.ofMillis(config.getConnectionTimeout()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            serviceAvailable = response.statusCode() == 200;

            if (serviceAvailable) {
                logger.info("Healing service is available at: " + config.getServiceUrl());
            } else {
                logger.warn("Healing service returned status: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            serviceAvailable = false;
            logger.warn("Healing service not available: " + e.getMessage());
        }

        return serviceAvailable;
    }

    /**
     * Request healing for a failed selector
     * 
     * @param request The healing request with failed selector info
     * @return HealingResult with healed selector if successful
     */
    public HealingResult heal(HealingRequest request) {
        if (!config.isEnabled()) {
            return HealingResult.builder()
                    .success(false)
                    .error("Healing is disabled")
                    .build();
        }

        if (!serviceAvailable && !checkServiceHealth()) {
            return HealingResult.builder()
                    .success(false)
                    .error("Healing service not available")
                    .build();
        }

        logger.info("🔧 Requesting healing for: " + request.getPageName() + "." + request.getElementKey());

        try {
            String jsonBody = gson.toJson(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(config.getHealEndpoint()))
                    .timeout(Duration.ofMillis(config.getReadTimeout()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                HealingResult result = gson.fromJson(response.body(), HealingResult.class);

                if (result.isSuccess()) {
                    logger.info("✅ Healing successful: " + result.getHealedSelector() +
                            " (confidence: " + result.getConfidence() + ")");
                } else {
                    logger.warn("❌ Healing failed: " + result.getError());
                }

                return result;
            } else {
                logger.error("Healing API returned error status: " + response.statusCode());
                return HealingResult.builder()
                        .success(false)
                        .error("HTTP " + response.statusCode() + ": " + response.body())
                        .build();
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Healing request failed", e);
            return HealingResult.builder()
                    .success(false)
                    .error("Request failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Validate a selector against the current DOM
     * 
     * @param selector    The selector to validate
     * @param domSnapshot The current DOM HTML
     * @return true if selector is valid
     */
    public boolean validateSelector(String selector, String domSnapshot) {
        if (!serviceAvailable && !checkServiceHealth()) {
            return false;
        }

        try {
            String jsonBody = gson.toJson(new ValidateRequest(selector, domSnapshot));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(config.getValidateEndpoint()))
                    .timeout(Duration.ofMillis(config.getReadTimeout()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ValidateResponse result = gson.fromJson(response.body(), ValidateResponse.class);
                return result.isValid();
            }

        } catch (IOException | InterruptedException e) {
            logger.warn("Validation request failed", e);
        }

        return false;
    }

    /**
     * Check if healing is enabled and service is available
     */
    public boolean isHealingAvailable() {
        return config.isEnabled() && (serviceAvailable || checkServiceHealth());
    }

    /**
     * Reset instance (for testing)
     */
    public static void reset() {
        instance = null;
    }

    // Inner classes for validation request/response
    private static class ValidateRequest {
        String selector;
        String domSnapshot;

        ValidateRequest(String selector, String domSnapshot) {
            this.selector = selector;
            this.domSnapshot = domSnapshot;
        }
    }

    private static class ValidateResponse {
        boolean valid;
        double confidence;
        int elementCount;

        boolean isValid() {
            return valid;
        }
    }
}
