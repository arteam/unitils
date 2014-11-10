package org.unitils.selenium.scenario;

import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.unitils.selenium.annotation.BaseUrl;
import org.unitils.selenium.annotation.TestWebDriver;
import org.unitils.UnitilsJUnit4TestClassRunner;

//START SNIPPET: AbstractWebTest
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AbstractWebTest {


    @TestWebDriver
    private WebDriver driver;

    @BaseUrl
    private String baseUrl;
    
    public AbstractWebTest(){
    }
    
    public WebDriver getDriver() {
        return driver;
    }

    
    public String getBaseUrl() {
        return baseUrl;
    }


}
//END SNIPPET: AbstractWebTest