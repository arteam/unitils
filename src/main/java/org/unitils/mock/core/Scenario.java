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

import org.unitils.mock.proxy.ProxyInvocation;
import static org.unitils.mock.proxy.ProxyUtil.getProxiedMethodStackTraceElement;
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.DefaultScenarioReport;
import static org.unitils.util.ReflectionUtils.getSimpleMethodName;

import java.lang.reflect.Method;
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

    protected SyntaxMonitor syntaxMonitor = new SyntaxMonitor();

    public SyntaxMonitor getSyntaxMonitor() {
        return syntaxMonitor;
    }


    public void reset() {
        observedInvocations.clear();
        invocationVerificationStatuses.clear();
        syntaxMonitor.reset();
    }

    public void addObservedMockInvocation(ObservedInvocation mockInvocation) {
        observedInvocations.add(mockInvocation);
        invocationVerificationStatuses.add(VerificationStatus.UNVERIFIED);
    }

    public List<ObservedInvocation> getObservedInvocations() {
        return observedInvocations;
    }


    public void assertNoMoreInvocations() {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (observedInvocation.getMockBehavior() == null && invocationVerificationStatus == VerificationStatus.UNVERIFIED) {
                throw new AssertionError(getNoMoreInvocationsErrorMessage(observedInvocation));
            }
        }
    }


    public void assertInvoked(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == VerificationStatus.UNVERIFIED && behaviorDefiningInvocation.matches(observedInvocation)) {
                // Found a match that's not verified yet. Mark as verified and proceed.
                invocationVerificationStatuses.set(i, VerificationStatus.VERIFIED);
                return;
            }
        }
        new AssertionError(getAssertInvokedErrorMessage(behaviorDefiningInvocation));
    }


    public void assertInvokedInOrder(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        ObservedInvocation matchingInvocation = null;
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (matchingInvocation == null && invocationVerificationStatus == VerificationStatus.UNVERIFIED && behaviorDefiningInvocation.matches(observedInvocation)) {
                // Found a match that's not verified yet. Mark as verified in order.
                invocationVerificationStatuses.set(i, VerificationStatus.VERIFIED_IN_ORDER);
                matchingInvocation = observedInvocation;
                continue;
            }
            // If we found a match, then check if there's no subsequent observed invocation that's already verified using assertInvokedInOrder()
            if (matchingInvocation != null && invocationVerificationStatus == VerificationStatus.VERIFIED_IN_ORDER) {
                throw new AssertionError(getInvokedOutOfOrderErrorMessage(behaviorDefiningInvocation, matchingInvocation, observedInvocation));
            }
        }
        if (matchingInvocation == null) {
            throw new AssertionError(getAssertInvokedErrorMessage(behaviorDefiningInvocation));
        }
    }


    public void assertNotInvoked(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == VerificationStatus.UNVERIFIED && behaviorDefiningInvocation.matches(observedInvocation)) {
                throw new AssertionError(getAssertNotInvokedErrorMessage(behaviorDefiningInvocation, observedInvocation));
            }
        }
    }


    public void assertNotExpectingInvocation() {
        syntaxMonitor.assertNotExpectingInvocation();
    }


    public String createReport() {
        ScenarioReport scenarioReport = new DefaultScenarioReport();
        return scenarioReport.createReport(this);
    }


    protected String getAssertNotInvokedErrorMessage(ProxyInvocation proxyInvocation, ObservedInvocation unexpectedInvocation) {
        StringBuilder message = new StringBuilder();
        message.append("Unexpected invocation of ");
        message.append(getSimpleMethodName(proxyInvocation.getMethod()));
        message.append("\n");
        message.append("occurred at ");
        message.append(unexpectedInvocation.getInvokedAt());
        message.append("\n");
        message.append(getAssertLocationIndication());
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }


    // todo check message
    protected String getAssertInvokedErrorMessage(ProxyInvocation proxyInvocation) {
        StringBuilder message = new StringBuilder();
        message.append("Expected invocation of ");
        message.append(getSimpleMethodName(proxyInvocation.getMethod()));
        message.append(", but the invocation didn't occur.\n");
        message.append(getAssertLocationIndication());
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }


    protected String getInvokedOutOfOrderErrorMessage(BehaviorDefiningInvocation behaviorDefiningInvocation, ObservedInvocation matchingInvocation,
                                                      ObservedInvocation outOfOrderInvocation) {
        StringBuilder message = new StringBuilder();
        message.append("Invocation of ");
        message.append(getSimpleMethodName(matchingInvocation.getMethod()));
        message.append(" was expected to be performed after ");
        message.append(getSimpleMethodName(outOfOrderInvocation.getMethod()));
        message.append(" but actually occurred before it.\n");
        message.append(getAssertLocationIndication());
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }


    protected String getNoMoreInvocationsErrorMessage(ObservedInvocation unexpectedInvocation) {
        StringBuilder message = new StringBuilder();
        Method method = unexpectedInvocation.getMethod();
        message.append("No more invocations expected, but ");
        message.append(getSimpleMethodName(method));
        message.append("\nwas called at ");
        message.append(unexpectedInvocation.getInvokedAt());
        message.append("\n\n");
        message.append(createReport());
        return message.toString();
    }

    protected String getAssertLocationIndication() {
        return "at " + getProxiedMethodStackTraceElement(Thread.currentThread().getStackTrace());
    }


}