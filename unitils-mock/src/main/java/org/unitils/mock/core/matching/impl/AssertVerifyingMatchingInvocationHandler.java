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
import org.unitils.mock.core.MockAssertionError;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.report.ScenarioReport;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class AssertVerifyingMatchingInvocationHandler implements MatchingInvocationHandler {

    protected MockFactory mockFactory;
    protected ScenarioReport scenarioReport;


    public AssertVerifyingMatchingInvocationHandler(MockFactory mockFactory, ScenarioReport scenarioReport) {
        this.mockFactory = mockFactory;
        this.scenarioReport = scenarioReport;
    }


    public Object handleInvocation(MatchingInvocation matchingInvocation) {
        String assertionErrorMessage = performAssertion(matchingInvocation);
        if (assertionErrorMessage != null) {
            throw createAssertionError(assertionErrorMessage, matchingInvocation);
        }
        return createChainedMock(matchingInvocation);
    }


    protected AssertionError createAssertionError(String assertionErrorMessage, MatchingInvocation matchingInvocation) {
        String report = scenarioReport.createReport();
        StackTraceElement assertedAt = matchingInvocation.getInvokedAt();

        StringBuilder message = new StringBuilder(assertionErrorMessage);
        message.append("\nAsserted at ");
        message.append(assertedAt);
        message.append("\n\n");
        message.append(report);
        throw new MockAssertionError(message.toString(), matchingInvocation.getInvokedAtTrace());
    }

    protected Object createChainedMock(MatchingInvocation matchingInvocation) {
        Mock<?> mock = mockFactory.createChainedMock(matchingInvocation);
        if (mock == null) {
            return null;
        }
        return performChainedAssertion(mock);
    }

    protected abstract String performAssertion(MatchingInvocation matchingInvocation);

    protected abstract Object performChainedAssertion(Mock<?> mock);
}
