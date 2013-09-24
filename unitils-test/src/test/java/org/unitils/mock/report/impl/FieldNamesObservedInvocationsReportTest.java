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
package org.unitils.mock.report.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests the usage of test fields in mock invocations. The names of the fields should be shown in the report (same as for large value).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class FieldNamesObservedInvocationsReportTest {

    /* class under test */
    private ObservedInvocationsReport observedInvocationsReport;

    private Mock<TestInterface> testMock;

    private List<String> myTestField = new ArrayList<String>();


    @Before
    public void initialize() {
        observedInvocationsReport = new ObservedInvocationsReport(this);

        // todo td implement
//        testMock = new MockObject<TestInterface>("testMock", TestInterface.class, this);
    }


    @Test
    public void fieldOfTestObjectAsReturnedValue() {
        testMock.returns(myTestField).testMethod(null);
        testMock.getMock().testMethod(null);

        // todo td implement
//        String result = observedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;
        assertTrue(result.contains("myTestField"));
    }

    @Test
    public void fieldOfTestObjectAsReturnedArgument() {
        testMock.returns(null).testMethod(myTestField);
        testMock.getMock().testMethod(myTestField);

        // todo td implement
//        String result = observedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;
        assertTrue(result.contains("myTestField"));
    }


    public static interface TestInterface {

        Object testMethod(Object value);

    }
}