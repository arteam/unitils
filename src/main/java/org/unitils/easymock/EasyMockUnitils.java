/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock;

import static org.easymock.EasyMock.reportMatcher;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.util.*;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * todo javadoc
 */
public class EasyMockUnitils {


    /**
     * Expects the given object argument but uses a reflection argument matcher to compare
     * the given value with the actual value during the test. The comparator modes are set to
     * ignore defaults and lenient order.
     * <p/>
     * Same as refEq with ignore defaults and lenient order as comparator modes.
     *
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
     * Creates an EasyMock mock object of the given type.
     * <p/>
     * An instance of the mock control is stored, so that it can be set to the replay/verify state when
     * {@link #replay()} or {@link #verify()} is called.
     *
     * @param mockType the class type for the mock, not null
     * @param order    the order setting, not null
     * @param returns  the returns setting, not null
     * @return a mock for the given class or interface, not null
     */
    public static <T> T createMock(Class<T> mockType, InvocationOrder order, Returns returns) {

        return getEasyMockModule().createMock(mockType, order, returns);
    }


    //todo javadoc
    public static <T> T createLenientMock(Class<T> mockType, InvocationOrder invocationOrder, Returns returns, Order order, Dates dates, Defaults defaults) {

        return getEasyMockModule().createLenientMock(mockType, invocationOrder, returns, order, dates, defaults);
    }

    /**
     * Unit tests should call this method after having set their recorded expected behavior on the mock objects.
     * <p/>
     * This method will make sure EasyMock's replay method is called on every mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the
     * {@link #createMock(Class,InvocationOrder,Returns)} and
     * {@link #createLenientMock(Class,InvocationOrder,Returns,Order,Dates,Defaults)} methods.
     * <p/>
     * After each test, the expected behavior will be verified automatically. Verification can also be performed
     * explicitly by calling the {@link #verify()} method.
     */
    public static void replay() {

        getEasyMockModule().replay();
    }


    /**
     * Unit tests should call this method to check whether all recorded expected behavior was actually observed during
     * the test.
     * <p/>
     * This method will make sure EasyMock's verify method is called on every mock mock object that was supplied to the
     * fields annotated with {@link @Mock}, or directly created by the
     * {@link #createMock(Class,InvocationOrder,Returns)} and
     * {@link #createLenientMock(Class,InvocationOrder,Returns,Order,Dates,Defaults)} methods.
     * <p/>
     * After each test, the expected behavior will be verified automatically. Verification can also be performed
     * explicitly by calling this method.
     */
    public static void verify() {

        getEasyMockModule().verify();
    }


    /**
     * Gets the first instance of an EasyMockModule that is stored in the modules repository.
     * This instance implements the actual behavior of the static methods, such as {@link #replay()}.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     * @throws UnitilsException when no such module could be found
     */
    private static EasyMockModule getEasyMockModule() {

        Unitils unitils = Unitils.getInstance();
        EasyMockModule module = unitils.getModulesRepository().getFirstModule(EasyMockModule.class);
        if (module == null) {

            throw new UnitilsException("Unable to find an instance of an EasyMockModule in the modules repository.");
        }
        return module;
    }

}
