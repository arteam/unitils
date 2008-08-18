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

import org.unitils.core.Unitils;
import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.core.MockDirector;
import org.unitils.mock.core.Scenario;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockUnitils {


    public static <T> MockBehaviorDefiner<T> mock(T mock) {
        return getMockDirector().mock(mock);
    }


    public static <T> T assertInvoked(T mock) {
        return getMockDirector().assertInvoked(mock);
    }


    public static <T> T assertNotInvoked(T mock) {
        return getMockDirector().assertNotInvoked(mock);
    }


    public static void assertNoMoreInvocations() {
        getMockDirector().assertNoMoreInvocations();
    }


    /**
     * Creates a mock object of the given type, associated to the given {@link Scenario}.
     *
     * @param name     A name for the mock, not null
     * @param mockType The type of the mock, not null
     * @return A mock for the given class or interface, not null
     */
    public static <T> T createMock(String name, Class<T> mockType) {
        return getMockDirector().createMock(name, mockType);
    }


    /**
     * Creates a mock object of the given type, associated to the given {@link Scenario}.
     *
     * @param name     A name for the mock, not null
     * @param mockType The type of the mock, not null
     * @return A mock for the given class or interface, not null
     */
    public static <T> T createPartialMock(String name, Class<T> mockType) {
        return getMockDirector().createPartialMock(name, mockType);
    }


    @ArgumentMatcher
    public static <T> T notNull(Class<T> argumentClass) {
        return getMockDirector().notNull(argumentClass);
    }


    @ArgumentMatcher
    public static <T> T isNull(Class<T> argumentClass) {
        return getMockDirector().isNull(argumentClass);
    }


    @ArgumentMatcher
    public static <T> T same(T sameAs) {
        return getMockDirector().same(sameAs);
    }


    @ArgumentMatcher
    public static <T> T eq(T equalTo) {
        return getMockDirector().eq(equalTo);
    }


    @ArgumentMatcher
    public static <T> T refEq(T equalTo) {
        return getMockDirector().refEq(equalTo);
    }


    @ArgumentMatcher
    public static <T> T lenEq(T equalTo) {
        return getMockDirector().lenEq(equalTo);
    }


    protected static MockDirector getMockDirector() {
        return getMockModule().getMockDirector();
    }


    protected static MockModule getMockModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(MockModule.class);
    }
}
