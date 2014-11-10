package org.unitils.selenium;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.selenium.ScreenshotTakingWebDriver;
import org.unitils.selenium.annotation.BaseUrl;
import org.unitils.selenium.annotation.TestWebDriver;


/**
 * just an example with {@link ScreenshotTakingWebDriver}.
 * 
 * @author Willemijn Wouters
 * 
 * @since 1.0.5
 * 
 */
//START SNIPPET: screenshotTakingWebdriverExample
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class ScreenshotTakingWebdriverExample {
    
    @BaseUrl
    private String baseUrl;
    
    @TestWebDriver
    private WebDriver webDriver;

    @Test
    public void testWithScreenshots() {
        ScreenshotTakingWebDriver screenshotWebdriver = new ScreenshotTakingWebDriver(webDriver, baseUrl);
        webDriver.get(baseUrl);
        screenshotWebdriver.saveScreenshot();
        webDriver.get("http://unitils.org/summary.html");
        screenshotWebdriver.saveScreenshot();
    }
}
//END SNIPPET: screenshotTakingWebdriverExample