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

import org.unitils.mock.core.Invocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioView;

import java.util.List;

/**
 * Default implementation of a {@link ScenarioView} that just displays all the executed invocations.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class FullOverviewScenarioView implements ScenarioView {


    /**
     * Creates a string representation of the given scenario.
     *
     * @param testObject The test instance, null if there is no test object
     * @param scenario   The sceneario, not null
     * @return The string representation, not null
     */
    public String createView(Object testObject, Scenario scenario) {
        StringBuilder result = new StringBuilder();

        List<Invocation> invocations = scenario.getObservedInvocations();
        for (Invocation invocation : invocations) {
            result.append(invocation.getMockObject().getName());
            result.append('.');
            result.append(invocation.getMethod().getName());
            result.append("()");
            result.append("          at ");
            result.append(invocation.getInvokedAt());
            result.append("\n");
        }
        return result.toString();
    }

}
