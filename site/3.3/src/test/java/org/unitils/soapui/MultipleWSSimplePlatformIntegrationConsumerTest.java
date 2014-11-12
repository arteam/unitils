package org.unitils.soapui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.soapui.WebserviceTest;
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
public class MultipleWSSimplePlatformIntegrationConsumerTest {


    //START SNIPPET: soapuiMultipleWS
    @WebserviceTestSoapUi(testCase = "checkAccessControl1 TestCase")
    private WebserviceTest ws1;

    @WebserviceTestSoapUi(testCase = "checkAccessControl2 TestCase")
    private WebserviceTest ws2;

    @WebserviceTestSoapUi(testCase = "checkAccessControl3 TestCase")
    private WebserviceTest ws3;

    //.... etc.


    @Test    
    public void checkAccessControl1() {
        ws1.triggerTestCase();
    }
    @Test    
    public void checkAccessControl2() {
        ws2.triggerTestCase();
    }
    @Test    
    public void checkAccessControl3() {
        ws3.triggerTestCase();
    }

    //END SNIPPET: soapuiMultipleWS

}
