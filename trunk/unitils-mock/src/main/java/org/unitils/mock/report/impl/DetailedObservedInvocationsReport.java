/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.core.util.ObjectFormatter;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A view that displays the details of the observed invocations. The details include:
 * <ul>
 * <li>the location where the invocation was invoked</li>
 * <li>the mock behavior that was executed</li>
 * <li>the location where this mock behavior was defined</li>
 * </ul>
 * Example: <pre><code>
 * 1. mock.method1() -> string1
 * <p/>
 * - string1 -> "1234567891234567890"
 * - Observed at MyTest.testMethod(MyTest.java:75)
 * - Behavior defined at MyTest.myTest(MyTest.java:37)
 * <p/>
 * <p/>
 * 2. mock.method1("value", 4) -> null
 * <p/>
 * - Observed at MyTest.testMethod(MyTest.java:77)
 * - No behavior defined, returned default value.
 * <code></pre>
 *
 * @author Tim Ducheyne
 * @author Kenny Claes
 * @author Filip Neven
 */
public class DetailedObservedInvocationsReport extends ObservedInvocationsReport {


    public DetailedObservedInvocationsReport(ObjectFormatter objectFormatter, int maxInlineParameterLength) {
        super(objectFormatter, maxInlineParameterLength);
    }


    @Override
    protected String formatObservedInvocations(List<ObservedInvocation> observedInvocations, Map<Object, FormattedObject> allLargeObjects, Map<Class<?>, Integer> largeObjectNameIndexes, Map<Object, String> fieldValuesAndNames) {
        StringBuilder result = new StringBuilder();
        int invocationIndex = 0;
        for (ObservedInvocation observedInvocation : observedInvocations) {
            List<FormattedObject> currentLargeObjects = new ArrayList<FormattedObject>();
            result.append(++invocationIndex);
            result.append(". ");
            result.append(formatObservedInvocation(observedInvocation, currentLargeObjects, allLargeObjects, largeObjectNameIndexes, fieldValuesAndNames));
            result.append("\n");
            result.append(formatLargeObjects(currentLargeObjects));
            result.append(formatObservedAt(observedInvocation));
            result.append(formatBehaviorDetails(observedInvocation));
            result.append("\n");
        }
        return result.toString();
    }


    /**
     * Creates a string representation of the details of the given invocation. This will give information about
     * where the invocation occurred.
     *
     * @param proxyInvocation The invocation to format, not null
     * @return The string representation, not null
     */
    protected String formatObservedAt(ProxyInvocation proxyInvocation) {
        StringBuilder result = new StringBuilder();
        result.append("- Observed at ");
        result.append(proxyInvocation.getInvokedAt());
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

        MockBehavior defaultMockBehavior = observedInvocation.getMockBehavior();
        if (defaultMockBehavior != null) {
            result.append("- ");
            result.append(formatDefaultMockBehavior(defaultMockBehavior));
            result.append("\n");
        }
        return result.toString();
    }

    protected String formatDefaultMockBehavior(MockBehavior defaultMockBehavior) {
        if (defaultMockBehavior instanceof DefaultValueReturningMockBehavior) {
            return "No behavior defined, returned default value.";
        }
        if (defaultMockBehavior instanceof OriginalBehaviorInvokingMockBehavior) {
            return "No behavior defined, executed original method behavior.";
        }
        return "No behavior defined, executed default behavior.";
    }

    /**
     * Format the values that were to long to be displayed inline
     *
     * @param largeObjects The large value representations, not null
     * @return The string representation, not null
     */
    protected String formatLargeObjects(List<FormattedObject> largeObjects) {
        StringBuilder result = new StringBuilder();

        List<String> usedNames = new ArrayList<String>();
        if (!largeObjects.isEmpty()) {
            for (FormattedObject largeObject : largeObjects) {
                String name = largeObject.getName();
                if (usedNames.contains(name)) {
                    // skip doubles
                    continue;
                }
                usedNames.add(name);
                String representation = largeObject.getRepresentation();
                result.append("- ");
                result.append(name);
                result.append(" -> ");
                result.append(representation);
                result.append("\n");
            }
        }
        return result.toString();
    }
}