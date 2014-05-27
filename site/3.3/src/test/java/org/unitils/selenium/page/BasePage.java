package org.unitils.selenium.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//START SNIPPET: basePageSeleniumExample
public class BasePage {

    @FindBy(id="navigationForm:menuCreate")
    private WebElement createElement;


    @FindBy(id="navigationForm:menuSearch")
    private WebElement searchElement;

    public void gotoCreate() {
        createElement.click();

    }

    public void gotoSearch() {
        searchElement.click();
    }

}
//END SNIPPET: basePageSeleniumExample