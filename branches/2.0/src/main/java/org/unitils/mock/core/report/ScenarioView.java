/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core.report;

import org.unitils.mock.core.Scenario;

/**
 * A {@link ScenarioView} can be used to generate a part of a {@link ScenarioReport}, based on a given {@link Scenario}.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScenarioView {

	/**
	 * Creates the view for the given {@link Scenario}.
	 * @param scenario to create the view.
	 * @return the view.
	 */
	public String createView(Scenario scenario);

}
