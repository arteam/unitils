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

import org.easymock.internal.MocksControl;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.AfterCreateMock;
import org.unitils.easymock.util.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.unitils.core.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.core.util.ReflectionUtils.invokeMethod;

/**
 * @author Tim Ducheyne
 */
public class MockService {

    protected MocksControlFactory mocksControlFactory;
    protected List<MocksControl> mocksControls = new ArrayList<MocksControl>();


    public MockService(MocksControlFactory mocksControlFactory) {
        this.mocksControlFactory = mocksControlFactory;
    }


    public void clearMocks() {
        mocksControls.clear();
    }

    /**
     * Replays all mock controls.
     */
    public void replay() {
        for (MocksControl mocksControl : mocksControls) {
            mocksControl.replay();
        }
    }

    /**
     * Resets all mock controls.
     */
    public void reset() {
        for (MocksControl mocksControl : mocksControls) {
            mocksControl.reset();
        }
    }

    /**
     * This method makes sure {@link org.easymock.internal.MocksControl#verify} method is called for every mock mock object
     * that was injected to a field annotated with {@link org.unitils.easymock.annotation.Mock}, or directly created by calling
     * {@link #createRegularMock(Class, InvocationOrder, Calls)} or
     * {@link #createMock(Class, InvocationOrder, Calls, Order, Dates, Defaults)}.
     * <p/>
     * If there are mocks that weren't already switched to the replay state using {@link MocksControl#replay()}} or by
     * calling {@link org.unitils.easymock.EasyMockUnitils#replay()}, this method is called first.
     */
    public void verify() {
        for (MocksControl mocksControl : mocksControls) {
            try {
                mocksControl.verify();
            } catch (IllegalStateException e) {
                throw new UnitilsException("Unable to verify mocks control. Be sure to call replay before using the mock.", e);
            }
        }
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
     * @param <T>             the type of the mock
     * @param mockType        the type of the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @param order           todo
     * @param dates           todo
     * @param defaults        todo
     * @return a mockcontrol for the given class or interface, not null
     */
    public <T> T createMock(Class<T> mockType, InvocationOrder invocationOrder, Calls calls, Order order, Dates dates, Defaults defaults) {
        MocksControl mocksControl = mocksControlFactory.createMocksControl(mockType, invocationOrder, calls, order, dates, defaults);
        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }

    /**
     * Creates an EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()}  is called.
     *
     * @param <T>             the type of the mock
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public <T> T createRegularMock(Class<T> mockType, InvocationOrder invocationOrder, Calls calls) {
        MocksControl mocksControl = mocksControlFactory.createRegularMocksControl(mockType, invocationOrder, calls);
        mocksControls.add(mocksControl);
        return mocksControl.createMock(mockType);
    }

    /**
     * Calls all {@link org.unitils.easymock.annotation.AfterCreateMock} annotated methods on the test, passing the given mock.
     * These annotated methods must have following signature <code>void myMethod(Object mock, String name, Class type)</code>.
     * If this is not the case, a runtime exception is called.
     *
     * @param testObject The test object, not null
     * @param mock       The mock object, not null
     * @param fieldName  The field name, not null
     * @param mockType   The class type of the mock, not null
     */
    public void callAfterCreateMockMethods(Object testObject, Object mock, String fieldName, Class<?> mockType) {
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), AfterCreateMock.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, mock, fieldName, mockType);

            } catch (Exception e) {
                throw new UnitilsException("Unable to invoke after create mock method: " + method + "\n" +
                        "Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }
}