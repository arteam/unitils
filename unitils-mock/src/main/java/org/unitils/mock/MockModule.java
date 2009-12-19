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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.AfterCreateMock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.PartialMockObject;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.dummy.DummyObjectUtil;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Set;

import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.*;

/**
 * Module for testing with mock objects.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockModule implements Module {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(MockModule.class);

    public static final String PROPERTY_LOG_FULL_SCENARIO_REPORT = "mockModule.logFullScenarioReport";

    public static final String PROPERTY_LOG_OBSERVED_SCENARIO = "mockModule.logObservedScenario";

    public static final String PROPERTY_LOG_DETAILED_OBSERVED_SCENARIO = "mockModule.logDetailedObservedScenario";

    public static final String PROPERTY_LOG_SUGGESTED_ASSERTS = "mockModule.logSuggestedAsserts";

    protected boolean logFullScenarioReport;

    protected boolean logObservedScenario;

    protected boolean logDetailedObservedScenario;

    protected boolean logSuggestedAsserts;


    /**
     * No initialization needed for this module
     */
    public void init(Properties configuration) {
        logFullScenarioReport = PropertyUtils.getBoolean(PROPERTY_LOG_FULL_SCENARIO_REPORT, configuration);
        logObservedScenario = PropertyUtils.getBoolean(PROPERTY_LOG_OBSERVED_SCENARIO, configuration);
        logDetailedObservedScenario = PropertyUtils.getBoolean(PROPERTY_LOG_DETAILED_OBSERVED_SCENARIO, configuration);
        logSuggestedAsserts = PropertyUtils.getBoolean(PROPERTY_LOG_SUGGESTED_ASSERTS, configuration);
    }


    /**
     * No after initialization needed for this module
     */
    public void afterInit() {
    }


    public Scenario getScenario() {
        return MockObject.getCurrentScenario();
    }


    public void logFullScenarioReport() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\n" + scenario.createFullReport());
        }
    }

    public void logObservedScenario() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\nObserved scenario:\n\n" + scenario.createObservedInvocationsReport());
        }
    }

    public void logDetailedObservedScenario() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\nDetailed observed scenario:\n\n" + scenario.createDetailedObservedInvocationsReport());
        }
    }

    public void logSuggestedAsserts() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\nSuggested assert statements:\n\n" + scenario.createSuggestedAssertsReport());
        }
    }


    public <T> Mock<T> createMock(Object testObject, String name, Class<?> type) {
        return new MockObject<T>(name, type, testObject);
    }


    public <T> Mock<T> createPartialMock(Object testObject, String name, Class<?> type) {
        return new PartialMockObject<T>(name, type, testObject);
    }


    protected Class<?> getMockedClass(Field field) {
        try {
            Type type = getGenericType(field);
            return getClassForType(type);
        } catch (UnitilsException e) {
            throw new UnitilsException("Unable to determine type of mock. A mock should be declared using the generic Mock<YourTypeToMock> or PartialMock<YourTypeToMock> types. Field: " + field, e);
        }
    }


    protected void createAndInjectMocksIntoTest(Object testObject) {
        Set<Field> mockFields = getFieldsOfType(testObject.getClass(), Mock.class, false);
        for (Field field : mockFields) {
            Mock<?> mock = getFieldValue(testObject, field);
            if (mock != null) {
                mock.resetBehavior();
                continue;
            }
            mock = createMock(testObject, field.getName(), getMockedClass(field));
            injectMock(testObject, field, mock);
        }
    }


    protected void createAndInjectPartialMocksIntoTest(Object testObject) {
        Set<Field> partialMockFields = getFieldsOfType(testObject.getClass(), PartialMock.class, false);
        for (Field field : partialMockFields) {
            Mock<?> mock = getFieldValue(testObject, field);
            if (mock != null) {
                mock.resetBehavior();
                continue;
            }
            mock = createPartialMock(testObject, field.getName(), getMockedClass(field));
            injectMock(testObject, field, mock);
        }
    }


    protected void injectMock(Object testObject, Field field, Mock<?> mock) {
        setFieldValue(testObject, field, mock);
        callAfterCreateMockMethods(testObject, mock, field.getName(), field.getType());
    }


    protected void createAndInjectDummiesIntoTest(Object testObject) {
        Set<Field> dummyFields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), Dummy.class);
        for (Field dummyField : dummyFields) {
            Object dummy = DummyObjectUtil.createDummy(dummyField.getType());
            setFieldValue(testObject, dummyField, dummy);
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
    // todo should we inject the mock or the proxy??
    protected void callAfterCreateMockMethods(Object testObject, Mock<?> mockObject, String name, Class<?> type) {
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), AfterCreateMock.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, mockObject.getMock(), name, ((MockObject<?>) mockObject).getMockedType());

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
     * Test listener that handles the scenario and mock creation, and makes sure a final syntax check
     * is performed after each test and that scenario reports are logged if required.
     */
    protected class MockTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            createAndInjectPartialMocksIntoTest(testObject);
            createAndInjectMocksIntoTest(testObject);
            createAndInjectDummiesIntoTest(testObject);
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            if (logFullScenarioReport) {
                logFullScenarioReport();
                return;
            }
            if (logObservedScenario) {
                logObservedScenario();
            }
            if (logDetailedObservedScenario) {
                logDetailedObservedScenario();
            }
            if (logSuggestedAsserts) {
                logSuggestedAsserts();
            }
        }


    }


}
