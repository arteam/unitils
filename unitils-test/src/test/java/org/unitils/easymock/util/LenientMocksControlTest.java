/*
 * Copyright 2013,  Unitils.org
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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.refEq;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientMocksControlTest {

    private LenientMocksControl lenientMocksControl;

    private MockedClass mock;


    @Before
    public void initialize() throws Exception {
        lenientMocksControl = new LenientMocksControl(DEFAULT, IGNORE_DEFAULTS);
        mock = lenientMocksControl.createMock(MockedClass.class);
    }


    @Test
    public void equalArguments() {
        expect(mock.someBehavior(true, 999, "Test", new ArrayList<Object>())).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList<Object>());

        assertEquals("Result", result);
        verify(mock);
    }

    @Test
    public void equalsNoArguments() {
        mock.someBehavior();
        replay(mock);

        mock.someBehavior();

        verify(mock);
    }

    @Test
    public void equalsDoubleInvocation() {
        expect(mock.someBehavior(true, 111, "Test1", Arrays.asList("1"))).andReturn("Result1");
        expect(mock.someBehavior(false, 222, "Test2", Arrays.asList("2"))).andReturn("Result2");
        replay(mock);

        String result1 = mock.someBehavior(true, 111, "Test1", Arrays.asList("1"));
        String result2 = mock.someBehavior(false, 222, "Test2", Arrays.asList("2"));

        verify(mock);
        assertEquals("Result1", result1);
        assertEquals("Result2", result2);
    }

    @Test
    public void equalsIgnoreDefaults() {
        expect(mock.someBehavior(false, 0, null, null)).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList<Object>());

        assertEquals("Result", result);
        verify(mock);
    }

    @Test
    public void exceptionWhenNotCalled() {
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList<Object>())).andReturn("Result");
        replay(mock);
        try {
            verify(mock);
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Expectation failure on verify:\n" +
                    "    someBehavior(true, 999, \"XXXX\", []): expected: 1, actual: 0", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDifferentArguments() {
        expect(mock.someBehavior(true, 999, "XXXX", new ArrayList<Object>())).andReturn("Result");
        replay(mock);
        try {
            mock.someBehavior(true, 999, "Test", new ArrayList<Object>());
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call someBehavior(true, 999, \"Test\", []):\n" +
                    "    someBehavior(true, 999, \"XXXX\", []): expected: 1, actual: 0", e.getMessage());
        }
    }

    @Test
    public void argumentMatchers() {
        expect(mock.someBehavior(eq(true), refEq(999), eq("Test"), refEq(new ArrayList<Object>()))).andReturn("Result");
        replay(mock);

        String result = mock.someBehavior(true, 999, "Test", new ArrayList<Object>());

        assertEquals("Result", result);
        verify(mock);
    }

    @Test
    public void exceptionWhenMixingArgumentMatchers() {
        try {
            expect(mock.someBehavior(eq(true), refEq(999), "Test", new ArrayList<Object>())).andReturn("Result");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("This mocks control does not support mixing of no-argument matchers and per-argument matchers.\n" +
                    "Either no matchers are defined (the reflection argument matcher is then used by default) or all matchers are defined explicitly (Eg by using refEq()).", e.getMessage());
        }
    }

    @Test
    public void equalsEnumArgument() {
        expect(mock.someBehavior(MockedClass.TestEnum.TEST1)).andStubReturn("Result1");
        expect(mock.someBehavior(MockedClass.TestEnum.TEST2)).andStubReturn("Result2");
        replay(mock);

        String result1 = mock.someBehavior(MockedClass.TestEnum.TEST1);
        String result2 = mock.someBehavior(MockedClass.TestEnum.TEST2);

        verify(mock);
        assertEquals("Result1", result1);
        assertEquals("Result2", result2);
    }


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
