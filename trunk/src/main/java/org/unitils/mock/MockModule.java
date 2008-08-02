/*
 * Copyright 2006-2007,  Unitils.org
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

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.EasyMockModule;
import org.unitils.mock.annotation.AfterCreateMock;
import org.unitils.mock.annotation.Mock;
import org.unitils.mock.annotation.PartialMock;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.MockObjectProxy;
import org.unitils.mock.core.MockObjectProxyMethodInterceptor;
import org.unitils.mock.core.ProxyUtils;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.report.DefaultScenarioReporter;
import org.unitils.mock.core.report.ScenarioReporter;
import org.unitils.util.ReflectionUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockModule implements Module {

	/* The logger instance for this class */
    private static Log logger = LogFactory.getLog(MockModule.class);
	
    /* The current execution scenario */
    private Scenario scenario;


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


    /**
     * Creates a mock object of the given type, associated to the {@link Scenario} of the current test.
     *
     * @param <T>             the type of the mock
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public <T> T createMock(String name, Class<T> mockType) {
    	return createMock(name, mockType, false);
    }
    
    
    /**
     * Creates a mock object of the given type, associated to the {@link Scenario} of the current test.
     *
     * @param <T>             the type of the mock
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public <T> T createPartialMock(String name, Class<T> mockType) {
    	return createMock(name, mockType, true);
    }


	protected <T> T createMock(String name, Class<T> mockType, boolean invokeOriginalMethodIfNoBehavior) {
		MockObject<T> mockObject = new MockObject<T>(name, mockType, invokeOriginalMethodIfNoBehavior, getScenario());
    	
		MockObjectProxyMethodInterceptor<T> mockObjectProxyMethodInterceptor = new MockObjectProxyMethodInterceptor<T>(mockObject);
		return ProxyUtils.createProxy(mockObjectProxyMethodInterceptor, mockType, MockObjectProxy.class);
	}


    protected void createAndInjectMocksIntoTest(Object testObject) {
    	Set<Field> mockFields = getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field mockField : mockFields) {
        	String name = mockField.getName();
            Class<?> mockType = mockField.getType();
            Object mockObject = createMock(name, mockType);
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockType);
        }
        
        Set<Field> partialMockFields = getFieldsAnnotatedWith(testObject.getClass(), PartialMock.class);
        for (Field mockField : partialMockFields) {
        	String name = mockField.getName();
            Class<?> mockType = mockField.getType();
            Object mockObject = createPartialMock(name, mockType);
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockType);
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
                throw new UnitilsException("Unable to invoke after create mock method. Ensure that this method has following signature: " +
                        "void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }
    
    
    protected Map<Object, Field> getTestObjectFieldMap(Object testObject) {
    	Map<Object, Field> objectFieldMap = new IdentityHashMap<Object, Field>();
    	Set<Field> allTestObjectFields = ReflectionUtils.getAllFields(testObject.getClass());
    	for (Field testObjectField : allTestObjectFields) {
    		Object fieldValue = ReflectionUtils.getFieldValue(testObject, testObjectField);
    		if (fieldValue != null) {
    			objectFieldMap.put(fieldValue, testObjectField);
    		}
    	}
    	return objectFieldMap;
    }
    
    
    public void logExecutionScenario(Object testObject) {
		ScenarioReporter scenarioReport = new DefaultScenarioReporter();
		logger.info("\n\n" + scenarioReport.createReport(getScenario(), getTestObjectFieldMap(testObject)));
	}
    
    
    public Scenario getScenario() {
    	if (scenario == null) {
    		scenario = new Scenario();
    	}
		return scenario;
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
			if (scenario != null) {
				logExecutionScenario(testObject);
			}
		}

    }


}
