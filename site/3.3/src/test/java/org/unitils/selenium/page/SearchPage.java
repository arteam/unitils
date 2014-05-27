/*
 * Copyright (c) Smals
 */
package org.unitils.selenium.page;

import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//START SNIPPET: searchPageSeleniumExample
public class SearchPage extends BasePage {
    
    @FindBy(id = "createForm:name")
    private WebElement surname;
    
    @FindBy(id = "createForm:firstName")
    private WebElement firstName;
    
    @FindBy(id = "createForm:tolerance")
    private WebElement tolerance;
    
    @FindBy(id = "createForm:birthdate")
    private WebElement birthDate;
    
    @FindBy(id = "createForm:searchButton")
    private WebElement searchButton;
    
    
    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname.sendKeys(surname);
    }
    
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName.sendKeys(firstName);
    }
    
    
    /**
     * @param tolerance the tolerance to set
     */
    public void setTolerance(String tolerance) {
        this.tolerance.sendKeys(tolerance);
    }
    
    
    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Date birthDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthDate);
        this.birthDate.sendKeys(cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DATE) + cal.get(Calendar.YEAR));
    }
}
//END SNIPPET: searchPageSeleniumExample
