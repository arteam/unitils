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

import java.lang.reflect.Field;
import java.util.Map;

import org.unitils.mock.core.Scenario;

/**
 * A {@link ScenarioReporter} can be used to give feedback about a given {@link Scenario}.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public interface ScenarioReporter {


	/**
	 * Creates the report.
	 * {@link #setScenario(Scenario)} must be called before calling this method.
	 * @return the report.
	 */
	public String createReport(Scenario scenario, Map<Object, Field> objectFieldMap);

}
