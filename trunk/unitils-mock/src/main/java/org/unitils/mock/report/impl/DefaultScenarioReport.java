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

import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioReport;

import java.util.List;

/**
 * Creates a report of the given scenario. This will first output an overview of
 * the executed scenario, followed by a list of suggested assertions and finally
 * a detailed listing of the executed scenario.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultScenarioReport implements ScenarioReport {

    /**
     * Creates a report.
     *
     * @param scenario The scenario to output, not null
     * @return the report, not null
     */
    public String createReport(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        List<ObservedInvocation> observedInvocations = scenario.getObservedInvocations();
        if (observedInvocations.isEmpty()) {
            // Make sure we don't return a report containing 3 titles and no content
            return "No invocations observed.\n";
        }
        result.append("Observed scenario:\n\n");
        result.append(new ObservedInvocationsReport(scenario.getTestObject()).createReport(observedInvocations));
        result.append("\n");

        result.append("\nDetailed scenario:\n\n");
        result.append(new DetailedObservedInvocationsReport(scenario.getTestObject()).createReport(observedInvocations));

        return result.toString();
    }

}
