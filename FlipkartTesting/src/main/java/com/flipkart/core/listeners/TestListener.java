package com.flipkart.core.listeners;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.flipkart.core.driver.DriverFactory;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

/**
 * TestListener - TestNG ITestListener implementation for:
 * - Screenshot capture on test failure (also attached to Allure)
 * - Test execution logging
 */
public class TestListener implements ITestListener {
    
    private static final String SCREENSHOT_DIR = "screenshots/";
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("========================================");
        System.out.println("Test Suite Started: " + context.getName());
        System.out.println("========================================");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("========================================");
        System.out.println("Test Suite Finished: " + context.getName());
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());
        System.out.println("========================================");
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        
        System.out.println("Starting Test: " + className + "." + testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("✓ Test PASSED: " + testName);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("✗ Test FAILED: " + testName);
        System.out.println("Failure Reason: " + result.getThrowable().getMessage());
        
        // Take screenshot on failure
        try {
            if (DriverFactory.hasDriver()) {
                captureScreenshotOnFailure(testName);
            }
        } catch (Exception e) {
            System.out.println("Could not capture screenshot: " + e.getMessage());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("○ Test SKIPPED: " + testName);
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not commonly used
    }
    
    /**
     * Capture screenshot and save to file + attach to Allure
     * @param testName Name of the test for filename
     */
    private void captureScreenshotOnFailure(String testName) {
        // Ensure directory exists
        new File(SCREENSHOT_DIR).mkdirs();
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String screenshotPath = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";
        
        try {
            WebDriver driver = DriverFactory.getDriver();
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            
            // Save to file
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, new File(screenshotPath));
            System.out.println("Screenshot saved: " + screenshotPath);
            
            // Attach to Allure report
            Allure.getLifecycle().addAttachment(
                testName + "_failure", 
                "image/png", 
                "png", 
                screenshot
            );
            
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Allure attachment helper method
     */
    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
