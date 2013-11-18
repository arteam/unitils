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
package org.unitils.mock.report;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.impl.DetailedObservedInvocationsReport;
import org.unitils.mock.report.impl.ObservedInvocationsReport;

import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class ScenarioReportCreateReportTest extends UnitilsJUnit4 {

    private ScenarioReport scenarioReport;

    private Mock<Scenario> scenarioMock;
    private Mock<ObservedInvocationsReport> observedInvocationsReportMock;
    private Mock<DetailedObservedInvocationsReport> detailedObservedInvocationsReportMock;
    @Dummy
    private ObservedInvocation observedInvocation;
    @Dummy
    private Properties testObject;


    @Before
    public void initialize() {
        scenarioReport = new ScenarioReport(scenarioMock.getMock(), observedInvocationsReportMock.getMock(), detailedObservedInvocationsReportMock.getMock());

        scenarioMock.returns(testObject).getTestObject();
    }


    @Test
    public void invocationsObserved() {
        scenarioMock.returns(asList(observedInvocation)).getObservedInvocations();
        observedInvocationsReportMock.returns("observed report").createReport(asList(observedInvocation), testObject);
        detailedObservedInvocationsReportMock.returns("detailed observed report").createReport(asList(observedInvocation), testObject);

        String result = scenarioReport.createReport();
        assertEquals("Observed scenario:\n" +
                "\n" +
                "observed report\n" +
                "Detailed scenario:\n" +
                "\n" +
                "detailed observed report", result);
    }

    @Test
    public void noInvocationsObserved() {
        String result = scenarioReport.createReport();
        assertEquals("No invocations observed.\n" +
                "\n", result);
    }
}
