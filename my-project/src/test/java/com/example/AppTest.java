package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    private WebDriver driver;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        //assertTrue( true );
        //System.out.println("Hello, world!");

        // Notes:
        // Never used JAVA or Maven before or setup a Github.
        // Using console print for error messages, not sure what would normally be used in this setup.
        // It appears 'assertTrue' does not have a failure message parameter, which I generally prefer for debugging info. There's probably a library for this or a custom method could be created.
        // Some companies like to have variables declared at the top, some don't care. I haven't in this case but can do either.
        // Object identifiers would be in a separate file / variables for maintainability with page object model. Skipped for time constraints.

        // Setup & Declare
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        driver.get("https://www.webstaurantstore.com/");

        // Would prefer not to use index next, but keeps returning element not interactable. Would require more troubleshooting, so workaround.
        //WebElement searchbox = driver.findElement(By.xpath("//div[contains(@class, 'hidden flex-1 ml-0 lt')]//input[@id='searchval']"));

        // Find all elements with the ID 'searchval' and use index for the one needed as workaround
        List<WebElement> searchboxAll = driver.findElements(By.id("searchval"));
        WebElement searchbox = searchboxAll.get(1);

        // From troubleshooting the not interactable issue
        if (searchbox.isDisplayed() && searchbox.isEnabled()) {
            searchbox.click();
        } else {
            System.out.println("Debug: Search box was not displayed or enabled");
        }

        // Search for the test value
        searchbox.sendKeys("stainless work table");
        searchbox.sendKeys(Keys.ENTER);

        // Note: There are 9 pages of results. I only did the first page for time constraints.
        // Otherwise, would have to get the qty of pages and cycle through to count/check all results.
        List<WebElement> searchResults = driver.findElements(By.xpath("//span[@data-testid='itemDescription']"));
        assertTrue(searchResults.size() > 0);

        // Iterate through each search result and verify the title contains "table"
        boolean tableInTitle = true;
        for (WebElement result : searchResults) {
            String title = result.getText().toLowerCase();
            if (!title.contains("table")) {
                tableInTitle = false;
                System.out.println("Product without 'table' in title: " + title);
            }
        }
        assertTrue(tableInTitle);

        // Add last item to cart
        searchResults.get(searchResults.size() - 1).click();
        driver.findElement(By.id("buyButton")).click();

        // Ensure item was added to cart before moving to next steps. Could also add verification here.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("cartItemCountSpan"), "1"));

        // Navigate to cart
        driver.findElement(By.xpath("//a[@data-testid='cart-button']")).click();

        //Confirm cart populated. (Could do >0 or =1 depending on valdiation needs.)
        // Note: There's a space in this ID which seems bad.
        List<WebElement> cartContent = driver.findElements(By.xpath("//div[@class='cartItem ag-item gtm-product-auto ']"));
        assertTrue(cartContent.size() > 0);

        // Clear cart
        driver.findElement(By.xpath("//button[@class='emptyCartButton btn btn-mini btn-ui pull-right']")).click();

        // Popup confirmation box
        driver.findElement(By.xpath("//div[@role='alertdialog']//button[contains(text(), 'Empty')]")).click();

        // Wait until cart is empty / page load and verify.
        WebElement cartEmpty = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='cartEmpty']")));
        assertNotNull(cartEmpty);

        // Close the browser
        driver.quit();
    }
}
