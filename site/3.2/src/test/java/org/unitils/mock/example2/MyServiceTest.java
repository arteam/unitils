package org.unitils.mock.example2;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import static org.junit.Assert.assertTrue;

// START SNIPPET: serviceTest
public class MyServiceTest extends UnitilsJUnit4 {

    /* Tested object */
    private MyService myService;

    private Mock<MyDao> myDaoMock;

    @Before
    public void initialize() {
        myService = new MyService();
        myService.setMyDao(myDaoMock.getMock());
        // END SNIPPET: serviceTest
        // START SNIPPET: getMock
        myService.setMyDao(myDaoMock.getMock());
        // END SNIPPET: getMock
        // START SNIPPET: serviceTest
    }

    @Test
    public void testMethod() {
        // define behavior
        myDaoMock.returns("something").getSomething();

        // do the actual test
        boolean result = myService.doService();

        // assert results and invocations
        assertTrue(result);
        myDaoMock.assertInvoked().storeSomething("something");
    }
}
// END SNIPPET: serviceTest

