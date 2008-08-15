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
package org.unitils.mock.report;

import org.unitils.mock.core.Scenario;

/**
 * Creates a report of the given {@link Scenario}.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScenarioReport {


    /**
     * Creates a report.
     *
     * @param message    An optional message
     * @param testObject The test instance, null if there is no test object
     * @param scenario   The scenario to output, not null
     * @return the report, not null
     */
    String createReport(String message, Object testObject, Scenario scenario);

}
