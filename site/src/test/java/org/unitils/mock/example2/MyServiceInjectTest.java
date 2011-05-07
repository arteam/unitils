package org.unitils.mock.example2;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import static org.junit.Assert.assertTrue;

// START SNIPPET: inject
public class MyServiceInjectTest extends UnitilsJUnit4 {

    @TestedObject
    private MyService myService;

    @InjectIntoByType
    private Mock<MyDao> myDaoMock;

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
// END SNIPPET: inject

