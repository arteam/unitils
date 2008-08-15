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
import java.util.List;
import java.util.Map;

import org.unitils.mock.core.Invocation;
import org.unitils.mock.core.Scenario;

/**
 * A {@link ScenarioView} that will present a list of suggestions based on the provided {@link Scenario}.
 *  
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class SuggestedAssertsScenarioReporter implements ScenarioReporter {

	/*
	 * @see ScenarioView#createView(Scenario)
	 */
	public String createReport(Scenario scenario, Map<Object, Field> testObjectFieldMap) {
		final StringBuffer view = new StringBuffer();
		List<Invocation> invocations = scenario.getObservedInvocations();
		view.append("Suggested assert statements:\n");
		for(Invocation invocation : invocations) {
			if(Void.TYPE.equals(invocation.getMethod().getReturnType()) ) {
				view.append("\t" + getSuggestedAssertStatement(invocation, testObjectFieldMap) + "\n");
			}
		}
		return view.toString();	
	}
	
	
	private String getSuggestedAssertStatement(Invocation invocation, Map<Object, Field> testObjectFieldMap) {
		StringBuffer suggestedAssertStatement = new StringBuffer();
		suggestedAssertStatement.append("assertInvoked(")
			.append(invocation.getMockObject().getName())
			.append(").")
			.append(invocation.getMethod().getName())
			.append("(");
		boolean firstArgument = true;
		for (Object argument : invocation.getArguments()) {
			Field testObjectField = testObjectFieldMap.get(argument);
			if (!firstArgument) {
				suggestedAssertStatement.append(", ");
			} else {
				firstArgument = false;
			}
			if (testObjectField != null) {
				suggestedAssertStatement.append(testObjectField.getName());
			} else {
				suggestedAssertStatement.append(getSuggestedArgumentMatcher(argument));
			}
		}
		suggestedAssertStatement.append(")")
			.append("\n");
		return suggestedAssertStatement.toString();
	}

	
	private String getSuggestedArgumentMatcher(Object argument) {
		if (argument == null) {
			return "null";
		}
		if (argument instanceof String) {
			return "\"" + argument + "\"";
		}
		if (argument instanceof Number) {
			return argument.toString();
		}
		if (argument instanceof Character) {
			return "'" + argument + "'";
		}
		return "null";
	}

}
