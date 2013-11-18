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
package org.unitils.mock;

import org.unitils.core.Unitils;
import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherService;
import org.unitils.mock.argumentmatcher.Capture;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class ArgumentMatchers {

    protected static ArgumentMatcherService argumentMatcherService = Unitils.getInstanceOfType(ArgumentMatcherService.class);


    /**
     * Matches when the argument is not null.
     * <p/>
     * Note: since null is returned this will produce a NullPointerException when used on a primitive argument.
     * eg.  int argument and  isNull(Integer.class)
     *
     * @param argumentClass The argument type, not null
     * @return null
     */
    @ArgumentMatcher
    public static <T> T notNull(Class<T> argumentClass) {
        argumentMatcherService.registerNotNullArgumentMatcher(argumentClass);
        return null;
    }

    /**
     * Matches when the argument is null.
     * <p/>
     * Note: since null is returned this will produce a NullPointerException when used on a primitive argument.
     * eg.  int argument and  isNull(Integer.class)
     *
     * @param argumentClass The argument type, not null
     * @return null
     */
    @ArgumentMatcher
    public static <T> T isNull(Class<T> argumentClass) {
        argumentMatcherService.registerNullArgumentMatcher(argumentClass);
        return null;
    }

    /**
     * Matches when the argument is the same object value.
     * <p/>
     * Note: do not use this matcher for primitive values, they will never match because of autoboxing
     *
     * @param sameAs The object to compare with
     * @return The sameAs value
     */
    @ArgumentMatcher
    public static <T> T same(T sameAs) {
        argumentMatcherService.registerSameArgumentMatcher(sameAs);
        return sameAs;
    }

    @ArgumentMatcher
    public static <T> T eq(T equalTo) {
        argumentMatcherService.registerEqualsArgumentMatcher(equalTo);
        return equalTo;
    }

    @ArgumentMatcher
    public static <T> T refEq(T equalTo) {
        argumentMatcherService.registerRefEqArgumentMatcher(equalTo);
        return equalTo;
    }

    @ArgumentMatcher
    public static <T> T lenEq(T equalTo) {
        argumentMatcherService.registerLenEqArgumentMatcher(equalTo);
        return equalTo;
    }

    // todo add lenEqs(T... equalTo)  en refEqs  e.G. lenEq("1", "2")

    /**
     * Matches when the object argument is of the given type (or subtype).
     * <p/>
     * Note: since null is returned this will produce a NullPointerException when used on a primitive argument.
     * eg.  int argument and  any(Integer.class)   use the anyInt matcher instead
     *
     * @param argumentClass The argument type, not null
     * @return null
     */
    @ArgumentMatcher
    public static <T> T any(Class<T> argumentClass) {
        argumentMatcherService.registerAnyArgumentMatcher(argumentClass);
        return null;
    }

    // todo td test
    @ArgumentMatcher
    public static <T> T get(Capture<T> capture) {
        argumentMatcherService.registerCaptureArgumentMatcher(capture);
        return null;
    }

    @ArgumentMatcher
    public static boolean anyBoolean() {
        argumentMatcherService.registerAnyArgumentMatcher(Boolean.class);
        return false;
    }

    @ArgumentMatcher
    public static byte anyByte() {
        argumentMatcherService.registerAnyArgumentMatcher(Byte.class);
        return 0;
    }

    @ArgumentMatcher
    public static short anyShort() {
        argumentMatcherService.registerAnyArgumentMatcher(Short.class);
        return 0;
    }

    @ArgumentMatcher
    public static char anyChar() {
        argumentMatcherService.registerAnyArgumentMatcher(Character.class);
        return 0;
    }

    @ArgumentMatcher
    public static int anyInt() {
        argumentMatcherService.registerAnyArgumentMatcher(Integer.class);
        return 0;
    }

    @ArgumentMatcher
    public static long anyLong() {
        argumentMatcherService.registerAnyArgumentMatcher(Long.class);
        return 0;
    }

    @ArgumentMatcher
    public static float anyFloat() {
        argumentMatcherService.registerAnyArgumentMatcher(Float.class);
        return 0;
    }

    @ArgumentMatcher
    public static double anyDouble() {
        argumentMatcherService.registerAnyArgumentMatcher(Double.class);
        return 0;
    }
}