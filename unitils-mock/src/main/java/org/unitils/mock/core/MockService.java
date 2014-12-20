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
package org.unitils.mock.core;

import org.unitils.mock.Mock;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.ObservedInvocationsReport;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class MockService {

    protected Scenario scenario;
    protected ObservedInvocationsReport observedInvocationsReport;
    protected ScenarioReport scenarioReport;
    protected StackTraceService stackTraceService;


    public MockService(Scenario scenario, ObservedInvocationsReport observedInvocationsReport, ScenarioReport scenarioReport, StackTraceService stackTraceService) {
        this.scenario = scenario;
        this.observedInvocationsReport = observedInvocationsReport;
        this.scenarioReport = scenarioReport;
        this.stackTraceService = stackTraceService;
    }


    public void assertNoMoreInvocations() {
        List<ObservedInvocation> unverifiedInvocations = scenario.getUnverifiedInvocations();
        if (unverifiedInvocations.isEmpty()) {
            return;
        }
        String assertionErrorMessage = getNoMoreInvocationsErrorMessage(unverifiedInvocations);
        throw new MockAssertionError(assertionErrorMessage);
    }

    public void assertNoMoreMockInvocations(Mock<?> mock) {
        Object proxy = mock.getMock();
        List<ObservedInvocation> unverifiedInvocations = scenario.getUnverifiedProxyInvocations(proxy);
        if (unverifiedInvocations.isEmpty()) {
            return;
        }
        String assertionErrorMessage = getNoMoreMockInvocationsErrorMessage(mock, unverifiedInvocations);
        StackTraceElement[] invokedAt = stackTraceService.getInvocationStackTrace(Mock.class, false);
        MockAssertionError assertionError = new MockAssertionError(assertionErrorMessage);
        assertionError.setStackTrace(invokedAt);
        throw assertionError;
    }


    protected String getNoMoreInvocationsErrorMessage(List<ObservedInvocation> unexpectedInvocations) {
        Object testObject = scenario.getTestObject();
        String report = scenarioReport.createReport();

        StringBuilder message = new StringBuilder();
        message.append("No more invocations expected, yet observed following calls:\n");
        message.append(observedInvocationsReport.createReport(unexpectedInvocations, testObject));
        message.append("\n");
        message.append(report);
        return message.toString();
    }

    protected String getNoMoreMockInvocationsErrorMessage(Mock<?> mock, List<ObservedInvocation> unexpectedInvocations) {
        Object testObject = scenario.getTestObject();
        String report = scenarioReport.createReport();

        StringBuilder message = new StringBuilder();
        message.append("No more invocations expected for mock ");
        message.append(mock);
        message.append(", yet observed following calls:\n");
        message.append(observedInvocationsReport.createReport(unexpectedInvocations, testObject));
        message.append("\n");
        message.append(report);
        return message.toString();
    }
}