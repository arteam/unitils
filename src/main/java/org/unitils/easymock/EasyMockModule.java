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

import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import org.easymock.internal.ReplayState;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.AfterCreateMock;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.annotation.RegularMock;
import org.unitils.easymock.util.*;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationEnumDefaults;
import static org.unitils.util.ModuleUtils.getValueReplaceDefault;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Module for testing with mock objects using EasyMock.
 * <p/>
 * Mock creation is simplified by automatically inserting EasyMock generated mocks for fields annotated with the
 * {@link @Mock} annotation.
 * <p/>
 * All methods annotated with {@link @AfterCreateMock} will be called when a mock object was created. This provides
 * you with a hook method for custom handling of the mock (e.g. adding the mocks to a service locator repository).
 * A method can only be called if it has following signature <code>void myMethod(Object mock, String name, Class type)</code>.
 * <p/>
 * Mocks can also be created explicitly
 * todo javadoc
 * <p/>
 * Switching to the replay state and verifying expectations of all mocks (including the mocks created with
 * the createMock() method can be done by calling
 * the {@link #replay()} and {@link #verify()} methods.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class EasyMockModule implements Module {

    /* Property key for configuring whether verify() is automatically called on every mock object after each test method execution */
    public static final String PROPKEY_AUTO_VERIFY_AFTER_TEST_ENABLED = "EasyMockModule.autoVerifyAfterTest.enabled";

    /* All created mocks controls */
    private List<MocksControl> mocksControls;

    /* Map holding the default configuration of the mock annotations */
    private Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> defaultEnumValues;

    /* Indicates whether verify() is automatically called on every mock object after each test method execution */
    private boolean autoVerifyAfterTestEnabled;

    /**
     * Initializes the module
     */
    public void init(Properties configuration) {
        mocksControls = new ArrayList<MocksControl>();
        defaultEnumValues = getAnnotationEnumDefaults(EasyMockModule.class, configuration, RegularMock.class, Mock.class);
        autoVerifyAfterTestEnabled = PropertyUtils.getBoolean(PROPKEY_AUTO_VERIFY_AFTER_TEST_ENABLED, configuration);
    }


    /**
     * Creates the listener for plugging in the behavior of this module into the test runs.
     *
     * @return the listener
     */
    public TestListener createTestListener() {
        return new EasyMockTestListener();
    }


    /**
     * Creates an EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()}  is called.
     *
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public <T> T createRegularMock(Class<T> mockType, InvocationOrder invocationOrder, Calls calls) {
        // Get anotation arguments and replace default values if needed
        invocationOrder = getValueReplaceDefault(RegularMock.class, invocationOrder, defaultEnumValues);
        calls = getValueReplaceDefault(RegularMock.class, calls, defaultEnumValues);

        MocksControl mocksControl;
        if (Calls.LENIENT == calls) {
            mocksControl = new MocksClassControl(NICE);

        } else {
            mocksControl = new MocksClassControl(DEFAULT);
        }
        // Check order
        if (InvocationOrder.STRICT == invocationOrder) {
            mocksControl.checkOrder(true);
        }
        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }


    /**
     * todo javadoc
     * <p/>
     * Creates an EasyMock mock instance of the given type (class/interface). The type of mock is determined
     * as follows:
     * <p/>
     * If returns is set to LENIENT, a nice mock is created, else a default mock is created
     * If arguments is lenient a lenient control is create, else an EasyMock control is created
     * If order is set to strict, invocation order checking is enabled
     *
     * @param mockType        the class/interface, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @param order           todo
     * @param dates           todo
     * @param defaults        todo
     * @return a mockcontrol for the given class or interface, not null
     */
    public <T> T createMock(Class<T> mockType, InvocationOrder invocationOrder, Calls calls, Order order, Dates dates, Defaults defaults) {
        // Get anotation arguments and replace default values if needed
        invocationOrder = getValueReplaceDefault(Mock.class, invocationOrder, defaultEnumValues);
        calls = getValueReplaceDefault(Mock.class, calls, defaultEnumValues);
        order = getValueReplaceDefault(Mock.class, order, defaultEnumValues);
        dates = getValueReplaceDefault(Mock.class, dates, defaultEnumValues);
        defaults = getValueReplaceDefault(Mock.class, defaults, defaultEnumValues);

        List<ReflectionComparatorMode> comparatorModes = new ArrayList<ReflectionComparatorMode>();
        if (Order.LENIENT == order) {
            comparatorModes.add(LENIENT_ORDER);
        }
        if (Dates.LENIENT == dates) {
            comparatorModes.add(LENIENT_DATES);
        }
        if (Defaults.IGNORE_DEFAULTS == defaults) {
            comparatorModes.add(IGNORE_DEFAULTS);
        }

        LenientMocksControl mocksControl;
        if (Calls.LENIENT == calls) {
            mocksControl = new LenientMocksControl(NICE, comparatorModes.toArray(new ReflectionComparatorMode[0]));

        } else {
            mocksControl = new LenientMocksControl(DEFAULT, comparatorModes.toArray(new ReflectionComparatorMode[0]));
        }
        // Check order
        if (InvocationOrder.STRICT == invocationOrder) {
            mocksControl.checkOrder(true);
        }
        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }


    /**
     *
     */
    public void replay() {
        for (MocksControl mocksControl : mocksControls) {
            mocksControl.replay();
        }
    }


    /**
     * This method makes sure {@link org.easymock.internal.MocksControl#verify method is called for every mock mock object
     * that was injected to a field annotated with {@link @Mock}, or directly created by calling
     * {@link #createRegularMock(Class,InvocationOrder,Calls)} or
     * {@link #createMock(Class,InvocationOrder,Calls,Order,Dates,Defaults)}.
     * <p/>
     * If there are mocks that weren't already switched to the replay state using {@link MocksControl#replay()}} or by
     * calling {@link org.unitils.easymock.EasyMockUnitils#replay()}, this method is called first.
     */
    public void verify() {
        for (MocksControl mocksControl : mocksControls) {
            if (!(mocksControl.getState() instanceof ReplayState)) {
                mocksControl.replay();
            }
            mocksControl.verify();
        }
    }


    /**
     * Creates and sets a mock for all {@link @RegularMock} annotated fields.
     * <p/>
     * The
     * todo javadoc
     * method is called for creating the mocks. Ones the mock is created, all methods annotated with {@link @AfterCreateMock} will be called passing the created mock.
     *
     * @param testObject the test, not null
     */
    protected void createAndInjectRegularMocksIntoTest(Object testObject) {
        List<Field> mockFields = getFieldsAnnotatedWith(testObject.getClass(), RegularMock.class);
        for (Field mockField : mockFields) {

            Class<?> mockType = mockField.getType();

            RegularMock regularMockAnnotation = mockField.getAnnotation(RegularMock.class);
            Object mockObject = createRegularMock(mockType, regularMockAnnotation.invocationOrder(), regularMockAnnotation.calls());
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockType);
        }
    }


    //todo javadoc
    protected void createAndInjectMocksIntoTest(Object testObject) {
        List<Field> mockFields = getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field mockField : mockFields) {

            Class<?> mockType = mockField.getType();

            Mock mockAnnotation = mockField.getAnnotation(Mock.class);
            Object mockObject = createMock(mockType, mockAnnotation.invocationOrder(), mockAnnotation.calls(), mockAnnotation.order(), mockAnnotation.dates(), mockAnnotation.defaults());
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockType);
        }
    }


    /**
     * Calls all {@link @AfterCreateMock} annotated methods on the test, passing the given mock.
     * These annotated methods must have following signature <code>void myMethod(Object mock, String name, Class type)</code>.
     * If this is not the case, a runtime exception is called.
     *
     * @param testObject the test, not null
     * @param mockObject the mock, not null
     * @param name       the field(=mock) name, not null
     * @param type       the field(=mock) type
     */
    protected void callAfterCreateMockMethods(Object testObject, Object mockObject, String name, Class type) {
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), AfterCreateMock.class);
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


    /**
     * Test listener that handles the mock creation and injection.
     */
    protected class EasyMockTestListener extends TestListener {

        /**
         * Before the test is executed this calls {@link EasyMockModule#createAndInjectRegularMocksIntoTest(Object)} to
         * create and inject all mocks on the class.
         */
        @Override
        public void beforeTestSetUp(Object testObject) {
            // Clear all previously created mocks controls
            mocksControls.clear();

            createAndInjectRegularMocksIntoTest(testObject);
            createAndInjectMocksIntoTest(testObject);
        }

        /**
         * After each test is executed this calls {@link EasyMockModule#verify()} to verify the recorded behavior
         * of all created mocks.
         */
        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            if (autoVerifyAfterTestEnabled) {
                verify();
            }
        }
    }

}

