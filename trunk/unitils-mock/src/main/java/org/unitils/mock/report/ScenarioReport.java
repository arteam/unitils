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

import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.impl.DetailedObservedInvocationsReport;
import org.unitils.mock.report.impl.ObservedInvocationsReport;

import java.util.List;

/**
 * Creates a report of the scenario. This will first output an overview of
 * the executed scenario, followed by a list of suggested assertions and finally
 * a detailed listing of the executed scenario.
 *
 * @author Tim Ducheyne
 * @author Kenny Claes
 * @author Filip Neven
 */
public class ScenarioReport {

    protected Scenario scenario;
    protected ObservedInvocationsReport observedInvocationsReport;
    protected DetailedObservedInvocationsReport detailedObservedInvocationsReport;


    public ScenarioReport(Scenario scenario, ObservedInvocationsReport observedInvocationsReport, DetailedObservedInvocationsReport detailedObservedInvocationsReport) {
        this.scenario = scenario;
        this.observedInvocationsReport = observedInvocationsReport;
        this.detailedObservedInvocationsReport = detailedObservedInvocationsReport;
    }

    /**
     * Creates a report.
     *
     * @return the report, not null
     */
    public String createReport() {
        List<ObservedInvocation> observedInvocations = scenario.getObservedInvocations();
        if (observedInvocations.isEmpty()) {
            return "No invocations observed.\n\n";
        }
        Object testObject = scenario.getTestObject();
        String summaryReport = observedInvocationsReport.createReport(observedInvocations, testObject);
        String detailedReport = detailedObservedInvocationsReport.createReport(observedInvocations, testObject);

        StringBuilder result = new StringBuilder();
        result.append("Observed scenario:\n\n");
        result.append(summaryReport);
        result.append("\nDetailed scenario:\n\n");
        result.append(detailedReport);
        return result.toString();
    }
}
