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
package org.unitils.mock.core.report;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.unitils.mock.core.Scenario;

/**
 * Default implementation of a {@link ScenarioReporter} where a list of {@link ScenarioView} can be inserted to compose a report of the {@link Scenario}.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class DefaultScenarioReporter implements ScenarioReporter {
	
	private List<ScenarioReporter> scenarioReporters = Arrays.asList(new FullOverviewScenarioReporter(), new SuggestedAssertsScenarioReporter());
	
	/*
	 * @see ScenarioReport#createReport()
	 */
	public String createReport(Scenario scenario, Map<Object, Field> objectFieldMap) {
		StringBuffer report = new StringBuffer();
		for(ScenarioReporter scenarioReporter : scenarioReporters) {
			report.append(scenarioReporter.createReport(scenario, objectFieldMap) + "\n");
		}
		return report.toString();
	}

}
