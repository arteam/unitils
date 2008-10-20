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
import org.unitils.mock.dummy.DummyObjectUtil;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockUnitils {


    public static void assertNoMoreInvocations() {
        getMockModule().assertNoMoreInvocations(getAssertedAt());
    }
    
    
    public static <T> T createDummy(Class<T> type) {
        return DummyObjectUtil.createDummy(type);
    }
    
    
    public static <T> Mock<T> createMock(Class<T> type) {
        return getMockModule().createMock("mock" + type.getSimpleName(), type, false);
    }
    
    
    public static <T> Mock<T> createMock(String name, Class<T> type) {
        return getMockModule().createMock(name, type, false);
    }

    
    public static <T> PartialMock<T> createPartialMock(Class<T> type) {
        return (PartialMock<T>) getMockModule().createMock("mock" + type.getSimpleName(), type, true);
    }
    
    
    public static <T> PartialMock<T> createPartialMock(String name, Class<T> type) {
        return (PartialMock<T>) getMockModule().createMock(name, type, true);
    }
    
    public static void logFullScenarioReport() {
        getMockModule().logFullScenarioReport();
    }
    
    
    public void logObservedScenario() {
        getMockModule().logObservedScenario();
    }
    
    
    public void logDetailedObservedScenario() {
        getMockModule().logDetailedObservedScenario();
    }

    
    public void logSuggestedAsserts() {
        getMockModule().logSuggestedAsserts();
    }
    
    
    private static StackTraceElement getAssertedAt() {
        StackTraceElement[] currentStackTrace = Thread.currentThread().getStackTrace();
        return currentStackTrace[4];
    }


    private static MockModule getMockModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(MockModule.class);
    }
}
