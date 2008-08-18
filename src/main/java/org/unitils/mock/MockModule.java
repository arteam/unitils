/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock;

import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.EasyMockModule;
import org.unitils.mock.annotation.AfterCreateMock;
import org.unitils.mock.annotation.Mock;
import org.unitils.mock.annotation.PartialMock;
import org.unitils.mock.core.MockDirector;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.InvocationMatcherBuilder;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

/**
 * Module for testing with mock objects.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockModule implements Module {

    private MockDirector mockDirector;


    /**
     * No initialization needed for this module
     */
    public void init(Properties configuration) {
    }


    /**
     * No after initialization needed for this module
     */
    public void afterInit() {
    }


    public MockDirector getMockDirector() {
        if (mockDirector == null) {
            mockDirector = createMockDirector();
        }
        return mockDirector;
    }


    protected MockDirector createMockDirector() {
        Scenario scenario = new Scenario();
        InvocationMatcherBuilder invocationMatcherBuilder = new InvocationMatcherBuilder();
        return new MockDirector(scenario, invocationMatcherBuilder);
    }


    // todo move to director
    public void logExecutionScenario(Object testObject) {
        if (mockDirector != null) {
            mockDirector.logExecutionScenario(testObject);
        }
    }


    protected void createAndInjectMocksIntoTest(Object testObject) {
        Set<Field> mockFields = getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field mockField : mockFields) {
            Object mockObject = getMockDirector().createMock(mockField.getName(), mockField.getType());
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockField.getType());
        }

        Set<Field> partialMockFields = getFieldsAnnotatedWith(testObject.getClass(), PartialMock.class);
        for (Field mockField : partialMockFields) {
            Object mockObject = getMockDirector().createPartialMock(mockField.getName(), mockField.getType());
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockField.getType());
        }
    }


    /**
     * Calls all {@link AfterCreateMock} annotated methods on the test, passing the given mock.
     * These annotated methods must have following signature <code>void myMethod(Object mock, String name, Class type)</code>.
     * If this is not the case, a runtime exception is called.
     *
     * @param testObject the test, not null
     * @param mockObject the mock, not null
     * @param name       the field(=mock) name, not null
     * @param type       the field(=mock) type
     */
    protected void callAfterCreateMockMethods(Object testObject, Object mockObject, String name, Class<?> type) {
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), AfterCreateMock.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, mockObject, name, type);

            } catch (InvocationTargetException e) {
                throw new UnitilsException("An exception occurred while invoking an after create mock method.", e);
            } catch (Exception e) {
                throw new UnitilsException("Unable to invoke after create mock method. Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }


    /**
     * Creates the listener for plugging in the behavior of this module into the test runs.
     *
     * @return the listener
     */
    public TestListener getTestListener() {
        return new MockTestListener();
    }


    /**
     * Test listener that handles the mock creation and injection.
     */
    protected class MockTestListener extends TestListener {

        /**
         * Before the test is executed this calls {@link EasyMockModule#createAndInjectRegularMocksIntoTest(Object)} to
         * create and inject all mocks on the class.
         */
        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            createAndInjectMocksIntoTest(testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable) {
            logExecutionScenario(testObject);
        }

    }


}
