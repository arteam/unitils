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

import static org.apache.commons.lang.StringUtils.uncapitalize;
import org.unitils.core.util.ObjectFormatter;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioView;

import java.lang.reflect.Method;
import java.util.*;

/**
 * A view that displays the observed invocations and the location where they were invoked.
 * The arguments are shown inline when the lenght is small enough, else the argument is named using the
 * type (eg Person => person1) and the actual value is displayed below the invocations.
 * <p/>
 * Example: <pre><code>
 * 1.  mock.method1()) -> string1  ..... at MyTest.testMethod(MyTest.java:60)
 * 2.  mock.method1("bla", 4) -> null  ..... at MyTest.testMethod(MyTest.java:62)
 * 3.  mock.anotherMethod(myClass1)  ..... at MyTest.testMethod(MyTest.java:64)
 *
 * string1 -> "1234567891234567890"
 * myClass1 -> MyClass<aField="hello">
 * <code></pre>
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OverviewScenarioView implements ScenarioView {

    /**
     * Formatter for arguments and return values
     */
    protected ObjectFormatter objectFormatter = new ObjectFormatter(10);

    /**
     * The maximum length of an inline value
     */
    protected int maximumValueLenght = 20;


    /**
     * Creates a string representation of the given scenario as described in the class javadoc.
     *
     * @param scenario The sceneario, not null
     * @return The string representation, not null
     */
    public String createView(Scenario scenario) {
        StringBuilder result = new StringBuilder();

        Map<Class<?>, Integer> largeValueIndexes = new HashMap<Class<?>, Integer>();
        List<String> formattedLargeValues = new ArrayList<String>();

        // append all invocations
        int invocationIndex = 1;
        for (ObservedInvocation observedInvocation : scenario.getObservedInvocations()) {
            result.append(invocationIndex++);
            result.append(".  ");
            if (invocationIndex > 10) {
                result.setLength(result.length() - 1);
            }
            result.append(formatObservedInvocation(observedInvocation, largeValueIndexes, formattedLargeValues));
            result.append(formatInvocationDetails(observedInvocation));
            result.append("\n");
        }

        // append the values that were to long to be displayed inline
        result.append(formatLargeValues(formattedLargeValues));
        return result.toString();
    }


    /**
     * Creates a string representation of the given invocation.
     * If arguments and result values are small enough, they are displayed inline, else they are added
     * per type to the given large values map. Inline the value is replaced by a name generated by
     * the {@link #formatLargeValueName} method.
     *
     * @param observedInvocation   The invocation to format, not null
     * @param largeValueIndexes    The current indexes to use for the large value names (per value type), not null
     * @param formattedLargeValues The large values as strings, not null
     * @return The string representation, not null
     */
    protected String formatObservedInvocation(ObservedInvocation observedInvocation, Map<Class<?>, Integer> largeValueIndexes, List<String> formattedLargeValues) {
        StringBuilder result = new StringBuilder();

        // append the mock and method name
        Method method = observedInvocation.getMethod();
        result.append(observedInvocation.getMockName());
        result.append('.');
        result.append(method.getName());

        // append the arguments
        result.append('(');
        Class<?>[] argumentTypes = method.getParameterTypes();
        if (argumentTypes.length > 0) {
            Iterator<?> arguments = observedInvocation.getArguments().iterator();
            for (Class<?> argumentType : argumentTypes) {
                String argumentAsString = objectFormatter.format(arguments.next());
                result.append(formatValue(argumentAsString, argumentType, largeValueIndexes, formattedLargeValues));
                result.append(", ");
            }
            result.setLength(result.length() - 2);
        }
        result.append(")");

        // append the result value, if there is one (void methods do not have mock behavior)
        Class<?> resultType = method.getReturnType();
        if (!Void.TYPE.equals(resultType)) {
            result.append(" -> ");
            String resultAsString = objectFormatter.format(observedInvocation.getResult());
            result.append(formatValue(resultAsString, resultType, largeValueIndexes, formattedLargeValues));
        }
        return result.toString();
    }


    /**
     * Creates a string representation of the details of the given invocation. This will give information about
     * where the invocation occurred.
     *
     * @param observedInvocation The invocation to format, not null
     * @return The string representation, not null
     */
    protected String formatInvocationDetails(ObservedInvocation observedInvocation) {
        StringBuilder result = new StringBuilder();
        result.append("  ..... at ");
        result.append(observedInvocation.getInvokedAt());
        return result.toString();
    }


    /**
     * Format the values that were to long to be displayed inline
     *
     * @param formattedLargeValues The large values as strings, not null
     * @return The string representation, not null
     */
    protected String formatLargeValues(List<String> formattedLargeValues) {
        StringBuilder result = new StringBuilder();

        if (!formattedLargeValues.isEmpty()) {
            result.append("\n");
            for (String formattedLargeValue : formattedLargeValues) {
                result.append(formattedLargeValue);
                result.append("\n");
            }
        }
        return result.toString();
    }


    /**
     * Formats the given value. If the value is small enough (lenght <=20), the value itself is returned, else
     * the value is added to the large values map and a name is returned generated by
     * the {@link #formatLargeValueName} method.
     * <p/>
     * E.g. string1, myClass1
     *
     * @param value                The value to format, not null
     * @param type                 The type of the large value, not null
     * @param largeValueIndexes    The current indexes to use for the large value names (per value type), not null
     * @param formattedLargeValues The large values as strings, not null
     * @return The value or the replaced name, not null
     */
    protected String formatValue(String value, Class<?> type, Map<Class<?>, Integer> largeValueIndexes, List<String> formattedLargeValues) {
        if (value.length() <= maximumValueLenght) {
            return value;
        }

        Integer index = largeValueIndexes.get(type);
        if (index == null) {
            index = 0;
        }
        largeValueIndexes.put(type, ++index);

        String largeValueName = formatLargeValueName(type, index);
        formattedLargeValues.add(formatLargeValue(largeValueName, value));
        return largeValueName;
    }


    /**
     * Creates a string representation for a large value.
     * This will return the name of the large value + the actual value.
     * <p/>
     * E.g. string1 -> "1234567891234567890"
     *
     * @param name  The name of the large value, not null
     * @param value The value, not null
     * @return The string representation, not null
     */
    protected String formatLargeValue(String name, String value) {
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append(" -> ");
        result.append(value);
        return result.toString();
    }


    /**
     * Creates a name to replace a large value.
     * The name is derived from the given type and index. E.g. string1, myClass1
     *
     * @param type  The type of the large value, not null
     * @param index The current index
     * @return The name, not null
     */
    protected String formatLargeValueName(Class<?> type, int index) {
        String result = uncapitalize(type.getSimpleName());
        return result + index;
    }
}
