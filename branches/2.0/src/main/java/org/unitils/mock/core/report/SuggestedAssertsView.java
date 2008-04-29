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
import org.unitils.mock.core.MethodUtils;
import org.unitils.mock.core.Scenario;

/**
 * A {@link ScenarioView} that will present a list of suggestions based on the provided {@link Scenario}.
 *  
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class SuggestedAssertsView implements ScenarioView {

	/*
	 * @see ScenarioView#createView(Scenario)
	 */
	public String createView(Scenario scenario) {
		final StringBuffer view = new StringBuffer();
		List<Invocation> invocations = scenario.getObservedInvocations();
		view.append("Suggested assert statements:\n");
		for(Invocation invocation : invocations) {
			Method method = invocation.getMethod();
			if(Void.TYPE.equals(method.getReturnType()) ) {
				view.append("\tassertInvoked(")
					.append(MethodUtils.getClassName(method))
					.append(").")
					.append(MethodUtils.getMethodNameWithParams(method))
					.append("\n");
			}
		}
		return view.toString();	}

}
