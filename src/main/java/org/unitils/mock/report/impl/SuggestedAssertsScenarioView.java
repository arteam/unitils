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

import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioView;
import static org.unitils.util.ReflectionUtils.getAllFields;
import static org.unitils.util.ReflectionUtils.getFieldValue;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * A view that will return a list of suggested assert statements that one can use in a test for the given scenario.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SuggestedAssertsScenarioView implements ScenarioView {


    /**
     * Creates a string representation of the given scenario.
     *
     * @param scenario The sceneario, not null
     * @return The string representation, not null
     */
    public String createView(Scenario scenario) {
        StringBuilder result = new StringBuilder();

        for (ObservedInvocation mockInvocation : scenario.getObservedInvocations()) {
            // do not output mocked methods (methods that return values)
            if (Void.TYPE.equals(mockInvocation.getMethod().getReturnType())) {
                result.append(getSuggestedAssertStatement(null, mockInvocation));
                result.append("\n");
            }
        }
        return result.toString();
    }


    /**
     * Creates an assert statement for the given method invocation and arguments.
     *
     * @param testObject         The test instance, null if there is no test object
     * @param observedInvocation The invocation, not null
     * @return The string representation of the assert statement, not null
     */
    protected String getSuggestedAssertStatement(Object testObject, ObservedInvocation observedInvocation) {
        StringBuilder result = new StringBuilder();

        result.append("assertInvoked(");
        result.append(observedInvocation.getMockName());
        result.append(").");
        result.append(observedInvocation.getMethod().getName());
        result.append("(");
        boolean firstArgument = true;
        for (Object argument : observedInvocation.getArguments()) {
            String testObjectFieldName = getFieldName(testObject, argument);
            if (!firstArgument) {
                result.append(", ");
            } else {
                firstArgument = false;
            }
            if (testObjectFieldName != null) {
                result.append(testObjectFieldName);
            } else {
                result.append(getSuggestedArgument(argument));
            }
        }
        result.append(")");
        return result.toString();
    }


    /**
     * Creates an appropriate value so that the assert statement will be able to match the given argument value
     * that was observed in the scenario.
     *
     * @param argument The actual argument value, not null
     * @return The string representation of the value to use in the assert statement, not null
     */
    protected String getSuggestedArgument(Object argument) {
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
        // use null for object values, this will ignore the actual value
        return "null";
    }


    /**
     * Checks whether the given argument value is a value of a field in the test object and, if so, returns the
     * name of that field.
     *
     * @param testObject The test instance, null if there is no test object
     * @param value      The value to look for, not null
     * @return The field name, null if no field was found for the value
     */
    protected String getFieldName(Object testObject, Object value) {
        if (testObject == null) {
            return null;
        }
        Set<Field> fields = getAllFields(testObject.getClass());
        for (Field field : fields) {
            Object fieldValue = getFieldValue(testObject, field);
            if (value == fieldValue) {
                return field.getName();
            }
        }
        return null;
    }
}
