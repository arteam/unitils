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
 * An interface for classes that can create a string representation of a {@link Scenario}.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScenarioView {

    /**
     * Creates a string representation of the given scenario.
     *
     * @param testObject The test instance, null if there is no test object
     * @param scenario   The sceneario, not null
     * @return The string representation, not null
     */
    String createView(Object testObject, Scenario scenario);

}
