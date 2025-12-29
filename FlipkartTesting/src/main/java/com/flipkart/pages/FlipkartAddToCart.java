package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartAddToCart extends BasePage {
	
	public FlipkartAddToCart(WebDriver driver) {
		super(driver);
	}
	
	// Add to Cart button - find by text "Add to cart"
	@FindBy(xpath = "//button[text()='Add to cart']")
	public WebElement addToCartButton;

	public boolean clickAddToCartButton() throws InterruptedException {
		// Wait for page to load
		pause(2000);
		
		try {
			// Maximize window and reset zoom to handle Eclipse's smaller window issue
			maximizeWindow();
			resetZoom();
			
			// Scroll down to bring the Add to Cart button into view
			scrollBy(0, 300);
			pause(500);
			
			// Close any login popup if present
			closeLoginPopupIfPresent();
			
			// First, try to find "Add to cart" button (product not in cart yet)
			String[] addToCartPatterns = {
				"//button[text()='Add to cart']",
				"//button[contains(text(),'Add to cart')]",
				"//button[contains(text(),'ADD TO CART')]"
			};
			
			// If product already in cart, it shows "Go to cart" instead
			String[] goToCartPatterns = {
				"//button[text()='Go to cart']",
				"//button[contains(text(),'Go to cart')]",
				"//button[contains(text(),'GO TO CART')]"
			};
			
			WebElement buttonToClick = null;
			boolean isGoToCart = false;
			
			// Try "Add to cart" first
			for (String xpath : addToCartPatterns) {
				try {
					buttonToClick = waitForClickable(By.xpath(xpath));
					System.out.println("Found 'Add to Cart' button - product is new to cart");
					break;
				} catch (Exception e) {
					// Try next pattern
				}
			}
			
			// If "Add to cart" not found, try "Go to cart" (product already in cart)
			if (buttonToClick == null) {
				for (String xpath : goToCartPatterns) {
					try {
						buttonToClick = waitForClickable(By.xpath(xpath));
						System.out.println("Found 'Go to Cart' button - product is already in cart!");
						isGoToCart = true;
						break;
					} catch (Exception e) {
						// Try next pattern
					}
				}
			}
			
			if (buttonToClick == null) {
				System.out.println("ERROR: Could not find Add to Cart or Go to Cart button!");
				return false;
			}
			
			// Scroll to the button and click
			scrollIntoView(buttonToClick);
			pause(500);
			
			// Try native click first, fallback to JavaScript
			String buttonName = isGoToCart ? "Go to Cart" : "Add to Cart";
			try {
				buttonToClick.click();
				System.out.println(buttonName + " button clicked!");
			} catch (Exception clickException) {
				clickWithJS(buttonToClick);
				System.out.println(buttonName + " button clicked using JavaScript!");
			}
			
			// Wait for cart action to complete
			pause(1500);
			
			// Navigate to cart page if not already there
			String currentUrl = getCurrentUrl();
			if (!currentUrl.contains("viewcart")) {
				driver.get("https://www.flipkart.com/viewcart?exploreMode=true&preference=FLIPKART");
			}
			
			// *** VISUAL WAIT - See the product in cart ***
			System.out.println(">> Product added! Viewing cart for 3 seconds...");
			pause(3000);
			
			// Quick verification
			boolean cartHasItems = isElementPresent(By.xpath("//*[contains(text(),'Place Order') or contains(text(),'PLACE ORDER')]"));
			if (cartHasItems) {
				System.out.println("SUCCESS: Product is in cart!");
			} else {
				boolean isCartEmpty = isElementPresent(By.xpath("//*[contains(text(),'Your cart is empty')]"));
				if (isCartEmpty) {
					System.out.println("WARNING: Cart is empty!");
					return false;
				}
			}
			
			return true;
			
		} catch (Exception e) {
			System.out.println("Add to cart failed: " + e.getMessage());
			return false;
		}
	}
}
