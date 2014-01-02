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
package org.unitils.mock.core.matching.impl;

import org.unitils.mock.Mock;
import org.unitils.mock.core.MatchingInvocation;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioReport;

import static org.unitils.core.util.ReflectionUtils.getSimpleMethodName;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertInvokedInSequenceVerifyingMatchingInvocationHandler extends AssertVerifyingMatchingInvocationHandler {

    protected Scenario scenario;


    public AssertInvokedInSequenceVerifyingMatchingInvocationHandler(Scenario scenario, MockFactory mockFactory, ScenarioReport scenarioReport) {
        super(mockFactory, scenarioReport);
        this.scenario = scenario;
    }


    protected String performAssertion(MatchingInvocation matchingInvocation) {
        ObservedInvocation observedInvocation = scenario.verifyInvocation(matchingInvocation);
        if (observedInvocation == null) {
            return getAssertInvokedErrorMessage(matchingInvocation);
        }
        ObservedInvocation outOfSequenceObservedInvocation = scenario.verifyInvocationInSequence(observedInvocation);
        if (outOfSequenceObservedInvocation != null) {
            return getInvokedOutOfSequenceErrorMessage(matchingInvocation, observedInvocation, outOfSequenceObservedInvocation);
        }
        return null;
    }

    protected Object performChainedAssertion(Mock<?> mock) {
        return mock.assertInvokedInSequence();
    }


    protected String getAssertInvokedErrorMessage(MatchingInvocation matchingInvocation) {
        String simpleMethodName = getSimpleMethodName(matchingInvocation.getMethod());

        StringBuilder message = new StringBuilder();
        message.append("Expected invocation of ");
        message.append(simpleMethodName);
        message.append(", but it didn't occur.");
        return message.toString();
    }

    protected String getInvokedOutOfSequenceErrorMessage(MatchingInvocation matchingInvocation, ObservedInvocation observedInvocation, ObservedInvocation outOfSequenceInvocation) {
        String simpleMethodName = getSimpleMethodName(matchingInvocation.getMethod());
        String outOfSequenceSimpleMethodName = getSimpleMethodName(outOfSequenceInvocation.getMethod());
        StackTraceElement invokedAt = observedInvocation.getInvokedAt();
        StackTraceElement outOfSequenceInvokedAt = outOfSequenceInvocation.getInvokedAt();

        StringBuilder message = new StringBuilder();
        message.append("Invocation of ");
        message.append(simpleMethodName);
        message.append(" invoked at ");
        message.append(invokedAt);
        message.append("\nwas expected to be performed after ");
        message.append(outOfSequenceSimpleMethodName);
        message.append(" invoked at ");
        message.append(outOfSequenceInvokedAt);
        message.append("\nbut actually occurred before it.");
        return message.toString();
    }
}