package org.unitils.easymock;

import junit.framework.TestCase;
import org.easymock.internal.MocksControl;
import org.unitils.easymock.annotation.Mock;

/**
 * Test for {@link EasyMockModule}.
 */
public class EasyMockModuleTest extends TestCase {


    private EasyMockModule easyMockModule;


    protected void setUp() throws Exception {
        super.setUp();

        easyMockModule = new EasyMockModule();
    }


    public void testCreateMocksControl() {

        MocksControl mocksControl = easyMockModule.createMocksControl(TestMockType.class, Mock.Order.NONE, Mock.Returns.NICE, Mock.Arguments.LENIENT);

        assertTrue(mocksControl instanceof LenientMocksControl);
    }

    public void testCreateMocksControl2() {

        MocksControl mocksControl = easyMockModule.createMocksControl(TestMockType.class, Mock.Order.STRICT, Mock.Returns.STRICT, Mock.Arguments.NONE);

        assertFalse(mocksControl instanceof LenientMocksControl);
    }


    private class TestMockType {

    }
}
