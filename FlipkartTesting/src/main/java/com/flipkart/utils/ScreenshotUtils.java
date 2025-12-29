package com.flipkart.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * ScreenshotUtils - Utility class for capturing and managing screenshots.
 */
public class ScreenshotUtils {
    
    private static final String SCREENSHOT_DIR = "screenshots/";
    
    /**
     * Capture screenshot and save to default directory
     * @param driver WebDriver instance
     * @param testName Name for the screenshot file
     * @return Path to the saved screenshot
     */
    public static String capture(WebDriver driver, String testName) {
        // Ensure directory exists
        new File(SCREENSHOT_DIR).mkdirs();
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String screenshotPath = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";
        
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, new File(screenshotPath));
            System.out.println("Screenshot saved: " + screenshotPath);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
        
        return screenshotPath;
    }
    
    /**
     * Capture screenshot and save to specified directory
     * @param driver WebDriver instance
     * @param testName Name for the screenshot file
     * @param directory Target directory
     * @return Path to the saved screenshot
     */
    public static String capture(WebDriver driver, String testName, String directory) {
        // Ensure directory exists
        new File(directory).mkdirs();
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String screenshotPath = directory + "/" + testName + "_" + timestamp + ".png";
        
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, new File(screenshotPath));
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
        
        return screenshotPath;
    }
    
    /**
     * Get screenshot as Base64 string (useful for embedding in reports)
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot
     */
    public static String captureAsBase64(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
    
    /**
     * Get screenshot as byte array
     * @param driver WebDriver instance
     * @return Screenshot as bytes
     */
    public static byte[] captureAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
    
    /**
     * Get current timestamp string for filenames
     * @return Formatted timestamp
     */
    public static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}
