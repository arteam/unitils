package org.unitils.selenium.scenario;

import org.junit.Test;
import org.openqa.selenium.support.PageFactory;
import org.unitils.selenium.page.SearchPage;

//START SNIPPET: searchTestSeleniumExample
public class SearchScenarioTest extends AbstractWebTest {

    @Test
    public void test() throws InterruptedException {
        getDriver().get(getBaseUrl());
        
        SearchPage searchPage = PageFactory.initElements(getDriver(), SearchPage.class);
        //Thread.sleep(10000);
        searchPage.gotoCreate();
        searchPage.gotoSearch();
        
        //... Asserts...
    }
    

}
//END SNIPPET: searchTestSeleniumExample