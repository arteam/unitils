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
public class MultipleEndpointsTest {


    //START SNIPPET: soapuiMultipleEndpoints
    @WebserviceTestPlaceHolder
    private WebserviceTest ws;

    @Test
    @WebserviceTestSoapUi(testCase = "checkAccessControl TestCase",  endPointPostfix = "v1")
    public void checkAccessControlOtherEndpointV1() {
        ws.triggerTestCase();
    }

    @Test
    @WebserviceTestSoapUi(testCase = "checkAccessControl TestCase", endPointPostfix = "v2")
    public void checkAccessControlOtherEndpointV2() {
        ws.triggerTestCase();
    }

    //END SNIPPET: soapuiMultipleEndpoints

}
