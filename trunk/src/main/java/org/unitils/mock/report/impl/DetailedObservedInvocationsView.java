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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unitils.core.util.ObjectFormatter;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;

/**
 * A view that displays the details of the observed invocations. The details include:
 * <ul>
 * <li>the location where the invocation was invoked</li>
 * <li>the mock behavior that was executed</li>
 * <li>the location where this mock behavior was defined</li>
 * </ul>
 * Example: <pre><code>
 * 1. mock.method1() -> string1
 *
 * - string1 -> "1234567891234567890"
 * - Observed at MyTest.testMethod(MyTest.java:75)
 * - Behavior defined at MyTest.myTest(MyTest.java:37)
 *
 *
 * 2. mock.method1("value", 4) -> null
 *
 * - Observed at MyTest.testMethod(MyTest.java:77)
 * - No behavior defined, returned default value.
 * <code></pre>
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DetailedObservedInvocationsView extends ObservedInvocationsView {

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
     * @return The string representation, not null
     */
    @Override
    public String createView(List<ObservedInvocation> observedInvocations) {
        StringBuilder result = new StringBuilder();

        Map<Class<?>, Integer> largeValueIndexes = new HashMap<Class<?>, Integer>();
        
        // append all invocations
        int invocationIndex = 1;
        for (ObservedInvocation observedInvocation : observedInvocations) {
            Map<String, String> formattedLargeValues = new HashMap<String, String>();
            result.append(invocationIndex++);
            result.append(". ");
            result.append(formatObservedInvocation(observedInvocation, largeValueIndexes, formattedLargeValues));
            result.append("\n");
            result.append(formatLargeValues(formattedLargeValues));
            result.append(formatInvokedAt(observedInvocation));
            result.append(formatBehaviorDetails(observedInvocation));
            result.append("\n");
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
    protected String formatInvokedAt(ObservedInvocation observedInvocation) {
        StringBuilder result = new StringBuilder();
        result.append("- Observed at ");
        result.append(observedInvocation.getInvokedAt());
        result.append("\n");
        return result.toString();
    }


    /**
     * Creates a string representation of the behavior details of the given invocation. This will give information about
     * where the mock behavior was recorded.
     *
     * @param observedInvocation The invocation to format, not null
     * @return The string representation, not null
     */
    protected String formatBehaviorDetails(ObservedInvocation observedInvocation) {
        StringBuilder result = new StringBuilder();

        BehaviorDefiningInvocation behaviorDefiningInvocation = observedInvocation.getBehaviorDefiningInvocation();
        if (behaviorDefiningInvocation != null) {
            result.append("- Behavior defined at ");
            result.append(behaviorDefiningInvocation.getInvokedAt());
            result.append("\n");
            return result.toString();
        }

        MockBehavior mockBehavior = observedInvocation.getMockBehavior();
        if (mockBehavior != null) {
            if (mockBehavior instanceof OriginalBehaviorInvokingMockBehavior) {
                result.append("- No behavior defined, executed original method behavior.");
            } else if (mockBehavior instanceof DefaultValueReturningMockBehavior) {
                result.append("- No behavior defined, returned default value.");
            } else {
                result.append("- No behavior defined, executed default behavior.");
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Format the values that were to long to be displayed inline
     *
     * @param formattedLargeValues The large values as strings, not null
     * @return The string representation, not null
     */
    protected String formatLargeValues(Map<String, String> formattedLargeValues) {
        StringBuilder result = new StringBuilder();

        if (!formattedLargeValues.isEmpty()) {
            result.append("\n");
            for (String largeValueName : formattedLargeValues.keySet()) {
                result.append("- ");
                result.append(largeValueName);
                result.append(" -> ");
                result.append(formattedLargeValues.get(largeValueName));
                result.append("\n");
            }
        }
        return result.toString();
    }
}