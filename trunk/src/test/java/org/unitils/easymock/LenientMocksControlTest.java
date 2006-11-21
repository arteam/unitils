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
package org.unitils.easymock;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A test for {@link org.unitils.easymock.LenientMocksControl}
 * <p/>
 * todo javadoc  + method javadoc
 */
public class LenientMocksControlTest extends TestCase {


    /* Class under test, with mock type NICE and ignore defaults */
    private LenientMocksControl lenientMocksControl;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        lenientMocksControl = new LenientMocksControl(DEFAULT, IGNORE_DEFAULTS);
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
     * Test for two equal objects without java defaults.
     */
    public void testCheckEquals_equalsDoubleInvocation() {

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
