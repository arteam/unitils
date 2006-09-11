package be.ordina.unitils.testing.mock;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.easymock.internal.MocksControl;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.classextension.EasyMock.*;
import static be.ordina.unitils.testing.util.ReflectionComparatorModes.*;

/**
 * todo javadoc
 */
public class LenientMocksControlTest extends TestCase {


    private LenientMocksControl lenientMocksControl;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        lenientMocksControl = new LenientMocksControl(MocksControl.MockType.NICE, IGNORE_DEFAULTS);
    }

    /**
     * Test for two equal objects without java defaults.
     */
    public void testCheckEquals_equalsIgnoreDefaults() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(false, 0, null, null)).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList());

        assertEquals("Result", result);
        verify(mock);
    }


    /**
     * Test for two equal objects without java defaults.
     */
    public void testCheckEquals_equals() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "Test", new ArrayList())).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList());

        assertEquals("Result", result);
        verify(mock);
    }


    /**
     * Test for two equal objects without java defaults.
     */
    public void testCheckEquals_notEquals() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList())).andReturn("Result");
        replay(mock);

        try {
            mock.someBehavior(true, 999, "Test", new ArrayList());
            fail();
        } catch (AssertionFailedError e) {
            //expected
            e.printStackTrace();
        }
    }


    private static class MockedClass {

        public String someBehavior(boolean b, int i, Object object, List list) {
            return null;
        }
    }
}
