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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.report.impl.ObservedInvocationsReport;
import org.unitils.mock.report.impl.ObservedInvocationsReportFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.unitils.mock.MockUnitils.getObservedInvocations;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObservedInvocationsReportFieldNamesIntegrationTest extends UnitilsJUnit4 {

    private ObservedInvocationsReport observedInvocationsReport;

    private Mock<TestInterface> testMock;
    private List<String> myTestField = new ArrayList<String>();


    @Before
    public void initialize() {
        ObservedInvocationsReportFactory observedInvocationsReportFactory = new ObservedInvocationsReportFactory(null, null, null);
        observedInvocationsReport = observedInvocationsReportFactory.create();
    }


    @Test
    public void fieldOfTestObjectAsReturnedValue() {
        testMock.returns(myTestField).testMethod(null);
        testMock.getMock().testMethod(null);

        String result = observedInvocationsReport.createReport(getObservedInvocations(), this);
        assertTrue(result.contains("myTestField"));
    }

    @Test
    public void fieldOfTestObjectAsReturnedArgument() {
        testMock.returns(null).testMethod(myTestField);
        testMock.getMock().testMethod(myTestField);

        String result = observedInvocationsReport.createReport(getObservedInvocations(), this);
        assertTrue(result.contains("myTestField"));
    }


    public static interface TestInterface {

        Object testMethod(Object value);
    }
}