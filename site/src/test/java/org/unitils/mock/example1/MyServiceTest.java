package org.unitils.mock.example1;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

// START SNIPPET: creation
public class MyServiceTest extends UnitilsJUnit4 {

    private Mock<MyService> myServiceMock;

    // END SNIPPET: creation
// START SNIPPET: programmatic
    @Before
    public void initialize() {
        myServiceMock = new MockObject<MyService>(MyService.class, this);
    }
// END SNIPPET: programmatic

    @Test
    public void behavior() {
        // START SNIPPET: returns
        myServiceMock.returns("a value").someMethod();
        // END SNIPPET: returns

        // START SNIPPET: raises
        myServiceMock.raises(RuntimeException.class).someMethod();
        myServiceMock.raises(new RuntimeException()).someMethod();
        // END SNIPPET: raises

        // START SNIPPET: performs
        myServiceMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                // do something else
                return "a value";
            }
        }).someMethod();
        // END SNIPPET: performs

        // START SNIPPET: once
        myServiceMock.onceRaises(RuntimeException.class).someMethod();
        myServiceMock.onceReturns("a value").someMethod();
        myServiceMock.returns("another value").someMethod();
        // END SNIPPET: once
    }

    @Test
    public void assertions() {
        // START SNIPPET: assertInvoked1
        myServiceMock.assertInvoked().someMethod();
        // END SNIPPET: assertInvoked1

        // START SNIPPET: assertNotInvoked
        myServiceMock.assertNotInvoked().someMethod();
        // END SNIPPET: assertNotInvoked

        // START SNIPPET: assertInvokedOnce
        myServiceMock.assertInvoked().someMethod();
        myServiceMock.assertNotInvoked().someMethod();
        // END SNIPPET: assertInvokedOnce

        // START SNIPPET: assertInvokedInSequence
        myServiceMock.assertInvokedInSequence().someMethod();
        myServiceMock.assertInvokedInSequence().anotherMethod();
        // END SNIPPET: assertInvokedInSequence

        // START SNIPPET: assertNoMoreInvocations
        myServiceMock.assertInvoked().someMethod();
        MockUnitils.assertNoMoreInvocations();
        // END SNIPPET: assertNoMoreInvocations
    }
// START SNIPPET: creation
}
// END SNIPPET: creation
