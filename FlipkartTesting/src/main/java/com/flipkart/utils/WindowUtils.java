package com.flipkart.utils;

import java.util.Set;

import org.openqa.selenium.WebDriver;

/**
 * WindowUtils - Utility class for handling browser windows and tabs.
 */
public class WindowUtils {
    
    /**
     * Switch to a newly opened window/tab
     * @param driver WebDriver instance
     * @return Original window handle (to switch back later)
     */
    public static String switchToNewWindow(WebDriver driver) {
        String originalWindow = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        
        for (String handle : windowHandles) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        return originalWindow;
    }
    
    /**
     * Switch to a specific window by title
     * @param driver WebDriver instance
     * @param title Window title to find
     * @return true if window was found and switched
     */
    public static boolean switchToWindowByTitle(WebDriver driver, String title) {
        String originalWindow = driver.getWindowHandle();
        
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(title)) {
                return true;
            }
        }
        
        // Window not found, switch back to original
        driver.switchTo().window(originalWindow);
        return false;
    }
    
    /**
     * Switch to a specific window by URL pattern
     * @param driver WebDriver instance
     * @param urlPattern URL pattern to match
     * @return true if window was found and switched
     */
    public static boolean switchToWindowByUrl(WebDriver driver, String urlPattern) {
        String originalWindow = driver.getWindowHandle();
        
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().contains(urlPattern)) {
                return true;
            }
        }
        
        // Window not found, switch back to original
        driver.switchTo().window(originalWindow);
        return false;
    }
    
    /**
     * Switch back to the main/first window
     * @param driver WebDriver instance
     */
    public static void switchToMainWindow(WebDriver driver) {
        String mainWindow = driver.getWindowHandles().iterator().next();
        driver.switchTo().window(mainWindow);
    }
    
    /**
     * Switch to a window by its handle
     * @param driver WebDriver instance
     * @param windowHandle Handle to switch to
     */
    public static void switchToWindow(WebDriver driver, String windowHandle) {
        driver.switchTo().window(windowHandle);
    }
    
    /**
     * Close current window and switch to another
     * @param driver WebDriver instance
     * @param targetHandle Handle to switch to after closing
     */
    public static void closeAndSwitchTo(WebDriver driver, String targetHandle) {
        driver.close();
        driver.switchTo().window(targetHandle);
    }
    
    /**
     * Close all windows except the main window
     * @param driver WebDriver instance
     */
    public static void closeAllExceptMain(WebDriver driver) {
        String mainWindow = driver.getWindowHandles().iterator().next();
        
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        
        driver.switchTo().window(mainWindow);
    }
    
    /**
     * Get count of open windows/tabs
     * @param driver WebDriver instance
     * @return Number of windows
     */
    public static int getWindowCount(WebDriver driver) {
        return driver.getWindowHandles().size();
    }
}
