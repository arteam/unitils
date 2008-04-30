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

import java.lang.reflect.Method;
import java.util.List;

import org.unitils.mock.core.Invocation;
import org.unitils.mock.core.MethodFormatUtils;
import org.unitils.mock.core.Scenario;

/**
 * Default implementation of a {@link ScenarioView} that just displays all the executed invocations.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class DefaultScenarioView implements ScenarioView {

	/*
	 * @see ScenarioView#createView(Scenario)
	 */
	public String createView(Scenario scenario) {
		final StringBuffer view = new StringBuffer();
		List<Invocation> invocations = scenario.getObservedInvocations();
		view.append("Observed scenario:\n");
		for(Invocation invocation : invocations) {
			Method method = invocation.getProxyMethod();
			view.append("\t")			
				.append(MethodFormatUtils.getCompleteRepresentation(method))
				.append("\n");
		}
		return view.toString();
	}
}
