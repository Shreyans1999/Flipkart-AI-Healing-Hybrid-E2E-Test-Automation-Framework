package com.flipkart.core.healing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads and manages JSON locator files for self-healing
 * Provides primary selectors and fallbacks for elements
 */
public class LocatorConfig {

    private static final Logger logger = LogManager.getLogger(LocatorConfig.class);
    private static LocatorConfig instance;

    private final Gson gson;
    private final String locatorsDir;
    private final Map<String, Map<String, LocatorEntry>> pageLocators;

    private LocatorConfig() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.locatorsDir = HealingConfig.getInstance().getLocatorsDir();
        this.pageLocators = new ConcurrentHashMap<>();
        logger.info("LocatorConfig initialized with locatorsDir: " + this.locatorsDir);
        logger.info("Current working directory: " + System.getProperty("user.dir"));
    }

    /**
     * Singleton instance
     */
    public static synchronized LocatorConfig getInstance() {
        if (instance == null) {
            instance = new LocatorConfig();
        }
        return instance;
    }

    /**
     * Get locator entry for a specific element
     * 
     * @param pageName   The page name (e.g., "flipkart-homepage")
     * @param elementKey The element key (e.g., "searchBox")
     * @return LocatorEntry with primary and fallback selectors, or null if not
     *         found
     */
    public LocatorEntry getLocator(String pageName, String elementKey) {
        Map<String, LocatorEntry> locators = getPageLocators(pageName);
        if (locators == null) {
            return null;
        }
        return locators.get(elementKey);
    }

    /**
     * Get the primary selector for an element
     */
    public String getPrimarySelector(String pageName, String elementKey) {
        LocatorEntry entry = getLocator(pageName, elementKey);
        return entry != null ? entry.getPrimary() : null;
    }

    /**
     * Get fallback selectors for an element
     */
    public List<String> getFallbackSelectors(String pageName, String elementKey) {
        LocatorEntry entry = getLocator(pageName, elementKey);
        return entry != null ? entry.getFallbacks() : new ArrayList<>();
    }

    /**
     * Get all locators for a page
     */
    public Map<String, LocatorEntry> getPageLocators(String pageName) {
        // Check cache first
        if (pageLocators.containsKey(pageName)) {
            return pageLocators.get(pageName);
        }

        // Load from file
        Map<String, LocatorEntry> locators = loadLocatorFile(pageName);
        if (locators != null) {
            pageLocators.put(pageName, locators);
        }
        return locators;
    }

    private Map<String, LocatorEntry> loadLocatorFile(String pageName) {
        String fileName = pageName + ".locators.json";

        // Try multiple path resolutions
        Path filePath = null;
        String[] possiblePaths = {
                locatorsDir + "/" + fileName,
                System.getProperty("user.dir") + "/" + locatorsDir + "/" + fileName,
                System.getProperty("user.dir") + "/../shared/locators/" + fileName,
                "../shared/locators/" + fileName
        };

        for (String pathStr : possiblePaths) {
            Path testPath = Paths.get(pathStr).toAbsolutePath().normalize();
            logger.info("Trying locator path: " + testPath);
            if (Files.exists(testPath)) {
                filePath = testPath;
                logger.info("✅ Found locator file at: " + filePath);
                break;
            }
        }

        if (filePath == null || !Files.exists(filePath)) {
            logger.error("❌ Locator file not found for page: " + pageName);
            return null;
        }

        try (FileReader reader = new FileReader(filePath.toFile())) {
            Type type = new TypeToken<Map<String, LocatorEntry>>() {
            }.getType();
            Map<String, LocatorEntry> locators = gson.fromJson(reader, type);
            logger.info("Loaded locators for page: " + pageName + " (" + locators.size() + " elements)");
            return locators;
        } catch (IOException e) {
            logger.error("Error reading locator file: " + filePath, e);
            return null;
        }
    }

    /**
     * Update a locator in the file after healing
     * 
     * @param pageName    The page name
     * @param elementKey  The element key
     * @param newSelector The new healed selector
     * @return true if update was successful
     */
    public boolean updateLocator(String pageName, String elementKey, String newSelector) {
        Map<String, LocatorEntry> locators = getPageLocators(pageName);
        if (locators == null) {
            locators = new HashMap<>();
        }

        LocatorEntry entry = locators.get(elementKey);
        if (entry == null) {
            entry = new LocatorEntry();
            locators.put(elementKey, entry);
        }

        // Store old primary as fallback if not already present
        String oldPrimary = entry.getPrimary();
        if (oldPrimary != null && !oldPrimary.equals(newSelector)) {
            List<String> fallbacks = entry.getFallbacks();
            if (fallbacks == null) {
                fallbacks = new ArrayList<>();
                entry.setFallbacks(fallbacks);
            }
            if (!fallbacks.contains(oldPrimary)) {
                fallbacks.add(0, oldPrimary); // Add old primary as first fallback
            }
        }

        // Update primary selector
        entry.setPrimary(newSelector);
        entry.incrementHealCount();

        // Update cache
        pageLocators.put(pageName, locators);

        // Save to file
        return saveLocatorFile(pageName, locators);
    }

    /**
     * Save locator file to disk
     */
    private boolean saveLocatorFile(String pageName, Map<String, LocatorEntry> locators) {
        String fileName = pageName + ".locators.json";
        Path filePath = Paths.get(locatorsDir, fileName);

        // Ensure directory exists
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            logger.error("Failed to create locators directory", e);
            return false;
        }

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(locators, writer);
            logger.info("Updated locator file: " + filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error writing locator file: " + filePath, e);
            return false;
        }
    }

    /**
     * Clear cached locators
     */
    public void clearCache() {
        pageLocators.clear();
    }

    /**
     * Reset instance (for testing)
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Inner class for locator entry structure
     */
    public static class LocatorEntry {
        private String primary;
        private List<String> fallbacks;
        private String lastHealed;
        private int healCount;

        public String getPrimary() {
            return primary;
        }

        public void setPrimary(String primary) {
            this.primary = primary;
        }

        public List<String> getFallbacks() {
            return fallbacks;
        }

        public void setFallbacks(List<String> fallbacks) {
            this.fallbacks = fallbacks;
        }

        public String getLastHealed() {
            return lastHealed;
        }

        public void setLastHealed(String lastHealed) {
            this.lastHealed = lastHealed;
        }

        public int getHealCount() {
            return healCount;
        }

        public void setHealCount(int healCount) {
            this.healCount = healCount;
        }

        public void incrementHealCount() {
            this.healCount++;
            this.lastHealed = java.time.Instant.now().toString();
        }
    }
}
