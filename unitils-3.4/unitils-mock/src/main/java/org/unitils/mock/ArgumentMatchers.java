/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock;

import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.argumentmatcher.impl.*;

import static org.unitils.mock.core.proxy.StackTraceUtils.getInvocationLineNr;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ArgumentMatchers {


    @ArgumentMatcher
    public static <T> T notNull(Class<T> argumentClass) {
        registerArgumentMatcher(new NotNullArgumentMatcher());
        return null;
    }


    @ArgumentMatcher
    public static <T> T isNull(Class<T> argumentClass) {
        registerArgumentMatcher(new NullArgumentMatcher());
        return null;
    }


    @ArgumentMatcher
    public static <T> T same(T sameAs) {
        registerArgumentMatcher(new SameArgumentMatcher(sameAs));
        return null;
    }


    @ArgumentMatcher
    public static <T> T eq(T equalTo) {
        registerArgumentMatcher(new EqualsArgumentMatcher(equalTo));
        return null;
    }


    @ArgumentMatcher
    public static <T> T refEq(T equalTo) {
        registerArgumentMatcher(new RefEqArgumentMatcher(equalTo));
        return null;
    }


    @ArgumentMatcher
    public static <T> T lenEq(T equalTo) {
        registerArgumentMatcher(new LenEqArgumentMatcher(equalTo));
        return null;
    }

    @ArgumentMatcher
    public static <T> T any(Class<T> type) {
        registerArgumentMatcher(new AnyArgumentMatcher(type));
        return null;
    }

    @ArgumentMatcher
    public static boolean anyBoolean() {
        registerArgumentMatcher(new AnyArgumentMatcher(Boolean.class));
        return false;
    }


    @ArgumentMatcher
    public static byte anyByte() {
        registerArgumentMatcher(new AnyArgumentMatcher(Byte.class));
        return 0;
    }


    @ArgumentMatcher
    public static short anyShort() {
        registerArgumentMatcher(new AnyArgumentMatcher(Short.class));
        return 0;
    }


    @ArgumentMatcher
    public static char anyChar() {
        registerArgumentMatcher(new AnyArgumentMatcher(Character.class));
        return 0;
    }


    @ArgumentMatcher
    public static int anyInt() {
        registerArgumentMatcher(new AnyArgumentMatcher(Integer.class));
        return 0;
    }


    @ArgumentMatcher
    public static long anyLong() {
        registerArgumentMatcher(new AnyArgumentMatcher(Long.class));
        return 0;
    }


    @ArgumentMatcher
    public static float anyFloat() {
        registerArgumentMatcher(new AnyArgumentMatcher(Float.class));
        return 0;
    }


    @ArgumentMatcher
    public static double anyDouble() {
        registerArgumentMatcher(new AnyArgumentMatcher(Double.class));
        return 0;
    }


    protected static <T> void registerArgumentMatcher(org.unitils.mock.argumentmatcher.ArgumentMatcher argumentMatcher) {
        ArgumentMatcherRepository argumentMatcherRepository = ArgumentMatcherRepository.getInstance();
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher, getInvocationLineNr(ArgumentMatchers.class));
    }

}