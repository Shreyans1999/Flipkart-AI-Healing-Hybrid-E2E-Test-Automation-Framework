package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.models.FlightData;
import com.flipkart.pages.FlipkartFlightPage;
import com.flipkart.tests.base.BaseTest;

/**
 * Flight booking end-to-end test cases for Flipkart Travel section.
 * Demonstrates use of POJO models for test data.
 */
public class FlightBookingTests extends BaseTest {

    @Test(groups = {"e2e", "flight"})
    public void testFlightSearch() throws InterruptedException {
        logger.info("Starting flight search test");
        
        // Using POJO model for test data
        FlightData flightData = FlightData.builder()
            .from(config.getDepartureCity())
            .to(config.getArrivalCity())
            .adults(1)
            .travelClass("Economy")
            .build();
        
        logger.info("Flight data: " + flightData);
        
        // Navigate to homepage
        navigateToBaseUrl();
        Thread.sleep(2000);
        
        // Initialize Page Object
        FlipkartFlightPage flightPage = new FlipkartFlightPage(driver);
        
        // Click on Flight Bookings in navigation
        flightPage.clickFlightBookings();
        logger.info("Clicked on Flight Bookings link");
        
        // Verify flight booking page is displayed
        Assert.assertTrue(flightPage.isFlightPageDisplayed(), 
            "Flight booking page should be displayed");
        
        // Perform flight search using POJO data
        flightPage.searchFlights(
            flightData.getDepartureCity(), 
            flightData.getArrivalCity()
        );
        logger.info("Entered search details and clicked Search");
        
        // Verify search results page loads
        String currentUrl = getCurrentUrl();
        logger.info("Current URL after search: " + currentUrl);
        
        Assert.assertTrue(
            currentUrl.contains("source") || 
            currentUrl.contains("destination") || 
            currentUrl.contains("travel") ||
            flightPage.isFlightResultsDisplayed(),
            "Flight search results page should be displayed"
        );
        
        logger.info("Flight search test completed successfully");
    }
}
