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
package org.unitils.easymock.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.AfterCreateMock;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class MockServiceCallAfterCreateMockMethodsTest extends UnitilsJUnit4 {

    private MockService mockService;

    private TestClass testInstance;
    private MyMock mock;


    @Before
    public void initialize() throws Exception {
        mockService = new MockService(null);

        testInstance = new TestClass();
        mock = new MyMock();
    }


    @Test
    public void callAfterCreateMockMethods() {
        mockService.callAfterCreateMockMethods(testInstance, mock, "field", MyMock.class);

        // method1
        assertSame(mock, testInstance.mock1);
        assertEquals("field", testInstance.name1);
        assertEquals(MyMock.class, testInstance.type1);
        // method2
        assertSame(mock, testInstance.mock2);
        assertEquals("field", testInstance.name2);
        assertEquals(MyMock.class, testInstance.type2);
    }

    @Test
    public void ignoredWhenNoAfterCreateMockMethods() {
        mockService.callAfterCreateMockMethods(new NoMethodTestClass(), mock, "field", MyMock.class);
    }

    @Test
    public void exceptionWhenWrongMethodInterface() {
        try {
            mockService.callAfterCreateMockMethods(new WrongMethodInterfaceTestClass(), mock, "field", MyMock.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to invoke after create mock method: public void org.unitils.easymock.core.MockServiceCallAfterCreateMockMethodsTest$WrongMethodInterfaceTestClass.method(java.lang.Object)\n" +
                    "Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)\n" +
                    "Reason: Error while invoking method public void org.unitils.easymock.core.MockServiceCallAfterCreateMockMethodsTest$WrongMethodInterfaceTestClass.method(java.lang.Object)\n" +
                    "Reason: IllegalArgumentException: wrong number of arguments", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenExceptionDuringMethodCall() {
        try {
            mockService.callAfterCreateMockMethods(new ExceptionDuringCallTestClass(), mock, "field", MyMock.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to invoke after create mock method: public void org.unitils.easymock.core.MockServiceCallAfterCreateMockMethodsTest$ExceptionDuringCallTestClass.method(java.lang.Object,java.lang.String,java.lang.Class)\n" +
                    "Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)\n" +
                    "Reason: Error while invoking method public void org.unitils.easymock.core.MockServiceCallAfterCreateMockMethodsTest$ExceptionDuringCallTestClass.method(java.lang.Object,java.lang.String,java.lang.Class)\n" +
                    "Reason: NullPointerException: expected", e.getMessage());
        }
    }


    private static class TestClass {

        protected Object mock1;
        protected String name1;
        protected Class<?> type1;

        protected Object mock2;
        protected String name2;
        protected Class<?> type2;

        @AfterCreateMock
        public void method1(Object mock, String name, Class<?> type) {
            this.mock1 = mock;
            this.name1 = name;
            this.type1 = type;
        }

        @AfterCreateMock
        public void method2(Object mock, String name, Class<?> type) {
            this.mock2 = mock;
            this.name2 = name;
            this.type2 = type;
        }
    }

    private static class NoMethodTestClass {
    }

    private static class WrongMethodInterfaceTestClass {

        @AfterCreateMock
        public void method(Object mock) {
        }
    }

    private static class ExceptionDuringCallTestClass {

        @AfterCreateMock
        public void method(Object mock, String name, Class<?> type) {
            throw new NullPointerException("expected");
        }
    }

    private static class MyMock {
    }
}