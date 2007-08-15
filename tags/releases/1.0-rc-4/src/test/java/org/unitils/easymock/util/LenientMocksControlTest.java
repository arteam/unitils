/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.easymock.util;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.unitils.easymock.EasyMockUnitils.refEq;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A test for {@link LenientMocksControl}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientMocksControlTest extends TestCase {

    /* Class under test, with mock type LENIENT and ignore defaults */
    private LenientMocksControl lenientMocksControl;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        lenientMocksControl = new LenientMocksControl(DEFAULT, IGNORE_DEFAULTS);
    }


    /**
     * Test for a mocked method call that is invoked with the expected arguments.
     */
    public void testLenientMocksControl_equals() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "Test", new ArrayList<Object>())).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList<Object>());

        assertEquals("Result", result);
        verify(mock);
    }


    /**
     * Test for a mocked method call that has no arguments.
     */
    public void testLenientMocksControl_equalsNoArguments() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        mock.someBehavior();
        replay(mock);

        mock.someBehavior();

        verify(mock);
    }


    /**
     * Test for a invoking a mocked method call more than once.
     */
    public void testLenientMocksControl_equalsDoubleInvocation() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 111, "Test1", Arrays.asList("1"))).andReturn("Result1");
        expect(mock.someBehavior(false, 222, "Test2", Arrays.asList("2"))).andReturn("Result2");
        replay(mock);

        String result1 = mock.someBehavior(true, 111, "Test1", Arrays.asList("1"));
        String result2 = mock.someBehavior(false, 222, "Test2", Arrays.asList("2"));

        verify(mock);
        assertEquals("Result1", result1);
        assertEquals("Result2", result2);
    }


    /**
     * Test for ignoring a default value for an argument.
     */
    public void testLenientMocksControl_equalsIgnoreDefaults() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(false, 0, null, null)).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList<Object>());

        assertEquals("Result", result);
        verify(mock);
    }


    /**
     * Test for a mocked method that is expected but was never called.
     */
    public void testLenientMocksControl_notEqualsNotCalled() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList<Object>())).andReturn("Result");
        replay(mock);

        try {
            verify(mock);
            fail();
        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Test for a mocked method call that is invoked with the different arguments.
     */
    public void testLenientMocksControl_notEqualsDifferentArguments() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList<Object>())).andReturn("Result");
        replay(mock);

        try {
            mock.someBehavior(true, 999, "Test", new ArrayList<Object>());
            fail();
        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Test for using argument matchers (refEq and EasyMocks eq).
     */
    public void testLenientMocksControl_mixingArgumentMatchers() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(eq(true), refEq(999), eq("Test"), refEq(new ArrayList<Object>()))).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList<Object>());

        assertEquals("Result", result);
        verify(mock);
    }


    /**
     * Test for a invoking a mocked method call with a enum argument.
     */
    public void testLenientMocksControl_equalsEnumArgument() {

        MockedClass mock = lenientMocksControl.createMock(MockedClass.class);
        expect(mock.someBehavior(MockedClass.TestEnum.TEST1)).andStubReturn("Result1");
        expect(mock.someBehavior(MockedClass.TestEnum.TEST2)).andStubReturn("Result2");
        replay(mock);

        String result1 = mock.someBehavior(MockedClass.TestEnum.TEST1);
        String result2 = mock.someBehavior(MockedClass.TestEnum.TEST2);

        verify(mock);
        assertEquals("Result1", result1);
        assertEquals("Result2", result2);
    }


    /**
     * The test class that is going to be mocked.
     */
    private static class MockedClass {

        public enum TestEnum {
            TEST1, TEST2
        }

        public void someBehavior() {
        }

        public String someBehavior(boolean b, int i, Object object, List<?> list) {
            return null;
        }

        public String someBehavior(TestEnum testEnum) {
            return null;
        }
    }

}
