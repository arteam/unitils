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

import static org.easymock.EasyMock.reportMatcher;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.util.Calls;
import org.unitils.easymock.util.Dates;
import org.unitils.easymock.util.Defaults;
import org.unitils.easymock.util.InvocationOrder;
import org.unitils.easymock.util.Order;
import org.unitils.easymock.util.ReflectionArgumentMatcher;
import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * Utility facade for handling EasyMock things such as replay or manually creating a mock.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class EasyMockUnitils {


    /**
     * Expects the given object argument but uses a reflection argument matcher to compare
     * the given value with the actual value during the test. The comparator modes are set to
     * ignore defaults and lenient order.
     * <p/>
     * Same as refEq with ignore defaults and lenient order as comparator modes.
     * 
     * @param <T> The type of the object to compare with 
     * @param object the value
     * @return null
     */
    public static <T> T lenEq(T object) {
        return refEq(object, IGNORE_DEFAULTS, LENIENT_ORDER);
    }


    /**
     * Expects the given object argument but uses a reflection argument matcher with the given comparator modes
     * to compare the given value with the actual value during the test.
     * 
     * @param <T>    the type of the object to compare with 
     * @param object the value
     * @param modes  the comparator modes
     * @return null
     */
    public static <T> T refEq(T object, ReflectionComparatorMode... modes) {
        ReflectionArgumentMatcher<T> reflectionArgumentMatcher = new ReflectionArgumentMatcher<T>(object, modes);
        reportMatcher(reflectionArgumentMatcher);
        return object;
    }


    /**
     * Creates a regular EasyMock mock object of the given type.
     * <p/>
     * Same as {@link #createRegularMock(Class,InvocationOrder,Calls)} with a default invocation order
     * and default calls value. These defaults can be set in the unitils configuration.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     * 
     * @param <T> the type of the mock 
     * @param mockType the type of the mock, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createRegularMock(Class<T> mockType) {
        return createRegularMock(mockType, InvocationOrder.DEFAULT, Calls.DEFAULT);
    }


    /**
     * Creates a regular EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     * 
     * @param <T>             the type of the mock 
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createRegularMock(Class<T> mockType, InvocationOrder invocationOrder, Calls calls) {
        return getEasyMockModule().createRegularMock(mockType, invocationOrder, calls);
    }


    /**
     * Creates a lenient mock object of the given type. The {@link org.unitils.easymock.util.LenientMocksControl} is used
     * for creating the mock.
     * <p/>
     * Same as {@link #createMock(Class,InvocationOrder,Calls,Order,Dates,Defaults)} with a default invocation order,
     * default calls, default order, default dates and default defaults value. These defaults can be set in the
     * unitils configuration.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     * 
     * @param <T> the type of the mock 
     * @param mockType the type of the mock, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createMock(Class<T> mockType) {
        return createMock(mockType, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.DEFAULT);
    }

    /**
     * Creates a lenient mock object of the given type. The {@link org.unitils.easymock.util.LenientMocksControl} is used
     * for creating the mock.
     * <p/>
     * Same as {@link #createMock(Class,InvocationOrder,Calls,Order,Dates,Defaults)} with a default invocation order,
     * default calls, default order, default dates and default defaults value. These defaults can be set in the
     * unitils configuration.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     * 
     * @param <T>             the type of the mock 
     * @param mockType        the type of the mock, not null
     * @param invocationOrder the invocation order setting, not null
     * @param calls           the calls setting, not null
     * @param order           the order setting, not null
     * @param dates           the dates setting, not null
     * @param defaults        the defaults setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createMock(Class<T> mockType, InvocationOrder invocationOrder, Calls calls, Order order, Dates dates, Defaults defaults) {
        return getEasyMockModule().createMock(mockType, invocationOrder, calls, order, dates, defaults);
    }

    /**
     * Unit tests should call this method after having set their recorded expected behavior on the mock objects.
     * <p/>
     * This method makes sure EasyMock's replay method is called on every mock object that was supplied to the
     * fields annotated with {@link org.unitils.easymock.annotation.Mock}, or directly created by the
     * {@link #createRegularMock(Class,InvocationOrder,Calls)} and
     * {@link #createMock(Class,InvocationOrder,Calls,Order,Dates,Defaults)} methods.
     * <p/>
     * After each test, the expected behavior is verified automatically, or explicitly by calling {@link #verify()}.
     */
    public static void replay() {
        getEasyMockModule().replay();
    }


    /**
     * Unit tests can call this method to check whether all recorded expected behavior was actually observed during
     * the test.
     * <p/>
     * This method makes sure {@link org.easymock.internal.MocksControl#verify} method is called for every mock mock object
     * that was injected to a field annotated with {@link Mock}, or directly created by the
     * {@link #createRegularMock(Class,InvocationOrder,Calls)} or
     * {@link #createMock(Class,InvocationOrder,Calls,Order,Dates,Defaults)} methods.
     * <p/>
     * By default, the expected behavior is verified automatically. This can be disabled however by setting the property
     * EasyMockModule.autoVerifyAfterTest.enabled to false. In that case, verification can also be performed explicitly
     * by calling this method.
     */
    public static void verify() {
        getEasyMockModule().verify();
    }


    /**
     * Gets the instance EasyMockModule that is registered in the modules repository.
     * This instance implements the actual behavior of the static methods in this class, such as {@link #replay()}.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     * @throws UnitilsException when no such module could be found
     */
    private static EasyMockModule getEasyMockModule() {
        Unitils unitils = Unitils.getInstance();
        EasyMockModule module = unitils.getModulesRepository().getModuleOfType(EasyMockModule.class);
        if (module == null) {
            throw new UnitilsException("Unable to find an instance of an EasyMockModule in the modules repository.");
        }
        return module;
    }

}
