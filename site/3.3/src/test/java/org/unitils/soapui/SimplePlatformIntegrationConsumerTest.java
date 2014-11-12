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
//START SNIPPET: soapuiTestExample
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SimplePlatformIntegrationConsumerTest {
    
    @WebserviceTestSoapUi(testCase = "checkAccessControl TestCase")
    private WebserviceTest ws;

    @Test    
    public void checkAccessControl() {
        ws.triggerTestCase();
    }
}
//END SNIPPET: soapuiTestExample