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
package org.unitils.mock.core;

import static org.unitils.mock.core.Scenario.VerificationStatus.*;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.DefaultScenarioReport;
import org.unitils.mock.report.impl.ObservedInvocationsReport;
import static org.unitils.util.ReflectionUtils.getSimpleMethodName;

import java.util.ArrayList;
import java.util.List;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class Scenario {

    protected static enum VerificationStatus {
        UNVERIFIED, VERIFIED, VERIFIED_IN_ORDER
    }

    protected List<ObservedInvocation> observedInvocations = new ArrayList<ObservedInvocation>();

    protected List<VerificationStatus> invocationVerificationStatuses = new ArrayList<VerificationStatus>();

    protected Object testObject;

    // todo move to mock object as static thread local
    protected SyntaxMonitor syntaxMonitor = new SyntaxMonitor();

    public Scenario() {
        this(null);
    }

    public Scenario(Object testObject) {
        this.testObject = testObject;
    }

    public Object getTestObject() {
        return testObject;
    }

    public SyntaxMonitor getSyntaxMonitor() {
        return syntaxMonitor;
    }

    public void addObservedMockInvocation(ObservedInvocation mockInvocation) {
        observedInvocations.add(mockInvocation);
        invocationVerificationStatuses.add(UNVERIFIED);
    }

    public List<ObservedInvocation> getObservedInvocations() {
        return observedInvocations;
    }


    public void assertNoMoreInvocations(StackTraceElement assertedAt) {
        List<ObservedInvocation> unexpectedInvocations = new ArrayList<ObservedInvocation>();
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (observedInvocation.getMockBehavior() == null && invocationVerificationStatus == UNVERIFIED) {
                unexpectedInvocations.add(observedInvocation);
            }
        }
        if (unexpectedInvocations.size() != 0) {
            throw new AssertionError(getNoMoreInvocationsErrorMessage(unexpectedInvocations, assertedAt));
        }
    }


    public void assertInvoked(BehaviorDefiningInvocation assertInvocation, StackTraceElement assertedAt) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == UNVERIFIED && assertInvocation.matches(observedInvocation)) {
                // Found a match that's not verified yet. Mark as verified and proceed.
                invocationVerificationStatuses.set(i, VERIFIED);
                return;
            }
        }
        throw new AssertionError(getAssertInvokedErrorMessage(assertInvocation, assertedAt));
    }


    public void assertInvokedInOrder(BehaviorDefiningInvocation behaviorDefiningInvocation, StackTraceElement assertedAt) {
        ObservedInvocation matchingInvocation = null;
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (matchingInvocation == null && invocationVerificationStatus == UNVERIFIED && behaviorDefiningInvocation.matches(observedInvocation)) {
                // Found a match that's not verified yet. Mark as verified in order.
                invocationVerificationStatuses.set(i, VERIFIED_IN_ORDER);
                matchingInvocation = observedInvocation;
                continue;
            }
            // If we found a match, then check if there's no subsequent observed invocation that's already verified using assertInvokedInOrder()
            if (matchingInvocation != null && invocationVerificationStatus == VERIFIED_IN_ORDER) {
                throw new AssertionError(getInvokedOutOfOrderErrorMessage(behaviorDefiningInvocation, matchingInvocation, observedInvocation, assertedAt));
            }
        }
        if (matchingInvocation == null) {
            throw new AssertionError(getAssertInvokedErrorMessage(behaviorDefiningInvocation, assertedAt));
        }
    }


    public void assertNotInvoked(BehaviorDefiningInvocation behaviorDefiningInvocation, StackTraceElement assertedAt) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == UNVERIFIED && behaviorDefiningInvocation.matches(observedInvocation)) {
                throw new AssertionError(getAssertNotInvokedErrorMessage(behaviorDefiningInvocation, observedInvocation, assertedAt));
            }
        }
    }


    public String createReport() {
        ScenarioReport scenarioReport = new DefaultScenarioReport();
        return scenarioReport.createReport(this);
    }


    protected String getAssertNotInvokedErrorMessage(ProxyInvocation proxyInvocation, ObservedInvocation unexpectedInvocation, StackTraceElement assertedAt) {
        StringBuilder message = new StringBuilder();
        message.append("Expected no invocation of ");
        message.append(getSimpleMethodName(proxyInvocation.getMethod()));
        message.append(", but it did occur.\nat ");
        message.append(unexpectedInvocation.getInvokedAt());
        message.append("\n");
        message.append(getAssertLocationIndication(assertedAt));
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }


    // todo check message
    protected String getAssertInvokedErrorMessage(ProxyInvocation proxyInvocation, StackTraceElement invokedAt) {
        StringBuilder message = new StringBuilder();
        message.append("Expected invocation of ");
        message.append(getSimpleMethodName(proxyInvocation.getMethod()));
        message.append(", but it didn't occur.\n");
        message.append(getAssertLocationIndication(invokedAt));
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }


    protected String getInvokedOutOfOrderErrorMessage(BehaviorDefiningInvocation behaviorDefiningInvocation, ObservedInvocation matchingInvocation,
                                                      ObservedInvocation outOfOrderInvocation, StackTraceElement assertedAt) {
        StringBuilder message = new StringBuilder();
        message.append("Invocation of ");
        message.append(getSimpleMethodName(matchingInvocation.getMethod()));
        message.append(" was expected to be performed after ");
        message.append(getSimpleMethodName(outOfOrderInvocation.getMethod()));
        message.append(" but actually occurred before it.\n");
        message.append(getAssertLocationIndication(assertedAt));
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }


    protected String getNoMoreInvocationsErrorMessage(List<ObservedInvocation> unexpectedInvocations, StackTraceElement assertedAt) {
        StringBuilder message = new StringBuilder();
        message.append("No more invocations expected, yet observed following calls:\n");
        message.append(new ObservedInvocationsReport().createReport(unexpectedInvocations));
        message.append("\n");
        message.append(getAssertLocationIndication(assertedAt));
        message.append("\n");
        message.append(createReport());
        return message.toString();
    }


    protected String getAssertLocationIndication(StackTraceElement assertedAt) {
        return "asserted at " + assertedAt.toString();
    }


}