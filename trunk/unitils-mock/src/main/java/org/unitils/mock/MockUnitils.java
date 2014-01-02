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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Unitils;
import org.unitils.mock.core.*;
import org.unitils.mock.report.ScenarioReport;

import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockUnitils {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(MockUnitils.class);


    public static void assertNoMoreInvocations() {
        getMockService().assertNoMoreInvocations();
    }

    public static <T> Mock<T> createMock(Class<T> type, Object testObject) {
        return createMock(null, type, testObject);
    }

    public static <T> Mock<T> createMock(String name, Class<T> type, Object testObject) {
        return getMockFactory().createMock(name, type, testObject);
    }


    public static <T> PartialMock<T> createPartialMock(Class<T> type, Object testObject) {
        return createPartialMock(null, type, testObject);
    }

    public static <T> PartialMock<T> createPartialMock(String name, Class<T> type, Object testObject) {
        return getMockFactory().createPartialMock(name, type, testObject);
    }

    public static <T> PartialMock<T> createPartialMock(T mockPrototype, Object testObject) {
        return createPartialMock(null, mockPrototype, testObject);
    }

    public static <T> PartialMock<T> createPartialMock(String name, T mockPrototype, Object testObject) {
        return getMockFactory().createPartialMock(name, mockPrototype, testObject);
    }


    public static <T> T createDummy(Class<T> type) {
        return createDummy(null, type);
    }

    public static <T> T createDummy(String name, Class<T> type) {
        return getDummyFactory().createDummy(name, type);
    }


    public static void logScenarioReport() {
        String report = getScenarioReport().createReport();
        logger.info(report);
    }

    public static List<ObservedInvocation> getObservedInvocations() {
        return getScenario().getObservedInvocations();
    }


    protected static Scenario getScenario() {
        return Unitils.getInstanceOfType(Scenario.class);
    }

    protected static MockService getMockService() {
        return Unitils.getInstanceOfType(MockService.class);
    }

    protected static MockFactory getMockFactory() {
        return Unitils.getInstanceOfType(MockFactory.class);
    }

    protected static DummyFactory getDummyFactory() {
        return Unitils.getInstanceOfType(DummyFactory.class);
    }

    protected static ScenarioReport getScenarioReport() {
        return Unitils.getInstanceOfType(ScenarioReport.class);
    }
}
