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
package org.unitils.mock.report.impl;

import org.apache.commons.lang.StringUtils;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioReport;

/**
 * Creates a report of the given scenario. This will first output an overview of the executed scenario, followed by
 * a list of suggested assertions.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultScenarioReport implements ScenarioReport {


    /**
     * Creates a report.
     *
     * @param message    An optional message
     * @param testObject The test instance, null if there is no test object
     * @param scenario   The scenario to output, not null
     * @return the report, not null
     */
    public String createReport(String message, Object testObject, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        if (!StringUtils.isEmpty(message)) {
            result.append(message);
            result.append("\n\n");
        }

        result.append("Observed scenario:\n\n");
        result.append(new OverviewScenarioView().createView(testObject, scenario));
        result.append("\n\nSuggested assert statements:\n\n");
        result.append(new SuggestedAssertsScenarioView().createView(testObject, scenario));
        result.append("\n\nDetailed scenario:\n\n");
        result.append(new DetailedScenarioView().createView(testObject, scenario));

        return result.toString();
    }

}
