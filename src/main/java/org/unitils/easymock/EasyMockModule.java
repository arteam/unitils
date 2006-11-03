/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock;

import org.apache.commons.configuration.Configuration;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MocksControl;
import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import org.easymock.internal.ReplayState;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.AfterCreateMock;
import org.unitils.easymock.annotation.LenientMock;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.util.*;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationEnumDefaults;
import static org.unitils.util.ModuleUtils.getValueReplaceDefault;
import org.unitils.util.ReflectionUtils;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 */
public class EasyMockModule implements Module {

    /* All created mocks controls */
    private List<MocksControl> mocksControls;

    //todo javadoc
    private Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> defaultEnumValues;


    /**
     * Initializes the module
     */
    public void init(Configuration configuration) {

        this.mocksControls = new ArrayList<MocksControl>();
        defaultEnumValues = getAnnotationEnumDefaults(EasyMockModule.class, configuration, Mock.class, LenientMock.class);
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
     * @param returns         the returns setting, not null
     * @return a mock for the given class or interface, not null
     */
    public <T> T createMock(Class<T> mockType, InvocationOrder invocationOrder, Returns returns) {

        // Get anotation arguments and replace default values if needed
        invocationOrder = getValueReplaceDefault(Mock.class, invocationOrder, defaultEnumValues);
        returns = getValueReplaceDefault(Mock.class, returns, defaultEnumValues);

        MocksControl mocksControl;
        if (Returns.NICE == returns) {
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
     * If returns is set to NICE, a nice mock is created, else a default mock is created
     * If arguments is lenient a lenient control is create, else an EasyMock control is created
     * If order is set to strict, invocation order checking is enabled
     *
     * @param mockType        the class/interface, not null
     * @param invocationOrder the order setting, not null
     * @param returns         the returns setting, not null
     * @param order           todo
     * @param dates           todo
     * @param defaults        todo
     * @return a mockcontrol for the given class or interface, not null
     */
    public <T> T createLenientMock(Class<T> mockType, InvocationOrder invocationOrder, Returns returns, Order order, Dates dates, Defaults defaults) {

        // Get anotation arguments and replace default values if needed
        invocationOrder = getValueReplaceDefault(LenientMock.class, invocationOrder, defaultEnumValues);
        returns = getValueReplaceDefault(LenientMock.class, returns, defaultEnumValues);
        order = getValueReplaceDefault(LenientMock.class, order, defaultEnumValues);
        dates = getValueReplaceDefault(LenientMock.class, dates, defaultEnumValues);
        defaults = getValueReplaceDefault(LenientMock.class, defaults, defaultEnumValues);

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
        if (Returns.NICE == returns) {
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
     * Implements the setting of the recorded behavior.
     * todo javadoc
     */
    public void replay() {

        for (MocksControl mocksControl : mocksControls) {
            mocksControl.replay();
        }
    }


    /**
     * Implements the verification of the recorded behavior.
     * todo javadoc
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
     * Creates and sets a mock for all {@link @Mock} and {@link @LenientMock} annotated fields.
     * <p/>
     * The
     * todo javadoc
     * method is called for creating the mocks. Ones the mock is created, all methods annotated with {@link @AfterCreateMock} will be called passing the created mock.
     *
     * @param testObject the test, not null
     */
    protected void createAndInjectMocksIntoTest(Object testObject) {

        List<Field> mockFields = getFieldsAnnotatedWith(testObject.getClass(), Mock.class);
        for (Field mockField : mockFields) {

            Class<?> mockType = mockField.getType();

            Mock mockAnnotation = mockField.getAnnotation(Mock.class);
            Object mockObject = createMock(mockType, mockAnnotation.invocationOrder(), mockAnnotation.returns());
            setFieldValue(testObject, mockField, mockObject);

            callAfterCreateMockMethods(testObject, mockObject, mockField.getName(), mockType);
        }
    }


    //todo javadoc
    protected void createAndInjectLenientMocksIntoTest(Object testObject) {

        List<Field> mockFields = getFieldsAnnotatedWith(testObject.getClass(), LenientMock.class);
        for (Field mockField : mockFields) {

            Class<?> mockType = mockField.getType();

            LenientMock mockAnnotation = mockField.getAnnotation(LenientMock.class);
            Object mockObject = createLenientMock(mockType, mockAnnotation.invocationOrder(), mockAnnotation.returns(), mockAnnotation.order(), mockAnnotation.dates(), mockAnnotation.defaults());
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
                ReflectionUtils.invokeMethod(testObject, method, mockObject, name, type);
            } catch (Exception e) {
                throw new UnitilsException("Unable to invoke after create mock method. Ensure that this method has following signature: " +
                        "void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }


    /**
     * Test listener that handles the mock creation and injection.
     */
    private class EasyMockTestListener extends TestListener {

        /**
         * Before the test is executed this calls {@link EasyMockModule#createAndInjectMocksIntoTest(Object)} to
         * create and inject all mocks on the class.
         */
        @Override
        public void beforeTestSetUp(Object testObject) {

            // Clear all previously created mocks controls
            mocksControls.clear();

            createAndInjectMocksIntoTest(testObject);
            createAndInjectLenientMocksIntoTest(testObject);
        }

        /**
         * After each test is executed this calls {@link EasyMockModule#verify()} to verify the recorded behavior
         * of all created mocks.
         */
        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {

            verify();
        }
    }

}

