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

import java.util.List;

import org.unitils.mock.core.Scenario;

/**
 * Default implementation of a {@link ScenarioReport} where a list of {@link ScenarioView} can be inserted to compose a report of the {@link Scenario}.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class DefaultScenarioReport implements ScenarioReport {
	private Scenario scenario;
	private List<ScenarioView> scenarioViews;
	
	/*
	 * @see ScenarioReport#createReport()
	 */
	public String createReport() {
		StringBuffer report = new StringBuffer();
		for(ScenarioView scenarioView: scenarioViews) {
			report.append(scenarioView.createView(scenario));
		}
		return report.toString();
	}

	/*
	 * @see ScenarioReport#setScenario(Scenario)
	 */
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	
	/**
	 * Sets the {@link ScenarioView} objects that this {@link ScenarioReport} will contain.
	 * @param scenarioViews the views of the report. Not null.
	 */
	public void setScenarioViews(List<ScenarioView> scenarioViews) {
		this.scenarioViews = scenarioViews;
	}
}
