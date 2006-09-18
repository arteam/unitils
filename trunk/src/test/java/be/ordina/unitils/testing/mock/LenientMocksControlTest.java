package be.ordina.unitils.testing.mock;

import static be.ordina.unitils.testing.util.ReflectionComparatorModes.IGNORE_DEFAULTS;
import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import static org.easymock.internal.MocksControl.MockType.NICE;

import java.util.ArrayList;
import java.util.List;

/**
 * A test for {@link LenientMocksControl}
 * <p/>
 * todo javadoc
 */
public class LenientMocksControlTest extends TestCase {


    /* Class under test, with mock type NICE and ignore defaults */
    private LenientMocksControl lenientMocksControl;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        lenientMocksControl = new LenientMocksControl(NICE, IGNORE_DEFAULTS);
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
    public void testCheckEquals_equalsNoArguments() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        mock.someBehavior();
        replay(mock);

        mock.someBehavior();

        verify(mock);
    }

    /**
     * Test for two equal objects with all java defaults.
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
    public void testCheckEquals_notEqualsNotCalled() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList())).andReturn("Result");
        replay(mock);

        try {
            verify(mock);
            fail();
        } catch (AssertionError e) {
            //expected
            e.printStackTrace();
        }
    }


    /**
     * Test for two equal objects without java defaults.
     */
    public void testCheckEquals_notEqualsDifferentArguments() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList())).andReturn("Result");
        replay(mock);

        try {
            mock.someBehavior(true, 999, "Test", new ArrayList());
            fail();
        } catch (AssertionError e) {
            //expected
            e.printStackTrace();
        }
    }


    //todo javadoc
    private static class MockedClass {

        public void someBehavior() {
        }

        public String someBehavior(boolean b, int i, Object object, List list) {
            return null;
        }
    }
}
