package org.unitils.soapui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.soapui.annotation.WebserviceTestPlaceHolder;
import org.unitils.soapui.annotation.WebserviceTestSoapUi;


/**
 * Just an example with SoapUi.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4.2
 * 
 */

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PlaceholderTest {


    //START SNIPPET: soapuiPlaceHolder
    @WebserviceTestPlaceHolder
    private WebserviceTest ws;

    //....

    @Test  
    @WebserviceTestSoapUi(testCase = "checkAccessControl1 TestCase")  
    public void checkAccessControl1() {
        ws.triggerTestCase();
    }
    @Test
    @WebserviceTestSoapUi(testCase = "checkAccessControl2 TestCase")    
    public void checkAccessControl2() {
        ws.triggerTestCase();
    }
    @Test    
    @WebserviceTestSoapUi(testCase = "checkAccessControl3 TestCase")
    public void checkAccessControl3() {
        ws.triggerTestCase();
    }
    //END SNIPPET: soapuiPlaceHolder

}
