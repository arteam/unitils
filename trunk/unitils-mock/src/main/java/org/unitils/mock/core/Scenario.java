/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock.core;

import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.DefaultScenarioReport;
import org.unitils.mock.report.impl.DetailedObservedInvocationsReport;
import org.unitils.mock.report.impl.ObservedInvocationsReport;
import org.unitils.mock.report.impl.SuggestedAssertsReport;

import java.util.ArrayList;
import java.util.List;

import static org.unitils.mock.core.Scenario.VerificationStatus.*;
import static org.unitils.util.ReflectionUtils.getSimpleMethodName;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class Scenario {

    protected static enum VerificationStatus {
        UNVERIFIED, VERIFIED, VERIFIED_IN_ORDER
    }

    protected Object testObject;

    protected List<ObservedInvocation> observedInvocations = new ArrayList<ObservedInvocation>();
    protected List<VerificationStatus> invocationVerificationStatuses = new ArrayList<VerificationStatus>();


    public Scenario(Object testObject) {
        this.testObject = testObject;
    }


    public void reset() {
        observedInvocations.clear();
        invocationVerificationStatuses.clear();
    }


    public Object getTestObject() {
        return testObject;
    }

    public void setTestObject(Object testObject) {
        this.testObject = testObject;
    }


    public void addObservedMockInvocation(ObservedInvocation mockInvocation) {
        observedInvocations.add(mockInvocation);
        invocationVerificationStatuses.add(UNVERIFIED);
    }


    public List<ObservedInvocation> getObservedInvocations() {
        return observedInvocations;
    }


    public void assertNoMoreInvocations(StackTraceElement[] assertedAt) {
        List<ObservedInvocation> unexpectedInvocations = new ArrayList<ObservedInvocation>();
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (observedInvocation.getMockBehavior() == null && invocationVerificationStatus == UNVERIFIED) {
                unexpectedInvocations.add(observedInvocation);
            }
        }
        if (unexpectedInvocations.size() != 0) {
            AssertionError assertionError = new AssertionError(getNoMoreInvocationsErrorMessage(unexpectedInvocations, assertedAt[0]));
            assertionError.setStackTrace(assertedAt);
            throw assertionError;
        }
    }


    public void assertInvoked(BehaviorDefiningInvocation assertInvocation) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == UNVERIFIED && assertInvocation.matches(observedInvocation) != -1) {
                // Found a match that's not verified yet. Mark as verified and proceed.
                invocationVerificationStatuses.set(i, VERIFIED);
                return;
            }
        }
        AssertionError assertionError = new AssertionError(getAssertInvokedErrorMessage(assertInvocation, assertInvocation.getInvokedAt()));
        assertionError.setStackTrace(assertInvocation.getInvokedAtTrace());
        throw assertionError;
    }


    public void assertInvokedInOrder(BehaviorDefiningInvocation assertInvocation) {
        ObservedInvocation matchingInvocation = null;
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (matchingInvocation == null && invocationVerificationStatus == UNVERIFIED && assertInvocation.matches(observedInvocation) != -1) {
                // Found a match that's not verified yet. Mark as verified in order.
                invocationVerificationStatuses.set(i, VERIFIED_IN_ORDER);
                matchingInvocation = observedInvocation;
                continue;
            }
            // If we found a match, then check if there's no subsequent observed invocation that's already verified using assertInvokedInOrder()
            if (matchingInvocation != null && invocationVerificationStatus == VERIFIED_IN_ORDER) {
                AssertionError assertionError = new AssertionError(getInvokedOutOfOrderErrorMessage(matchingInvocation, observedInvocation, assertInvocation.getInvokedAt()));
                assertionError.setStackTrace(assertInvocation.getInvokedAtTrace());
                throw assertionError;
            }
        }
        if (matchingInvocation == null) {
            AssertionError assertionError = new AssertionError(getAssertInvokedErrorMessage(assertInvocation, assertInvocation.getInvokedAt()));
            assertionError.setStackTrace(assertInvocation.getInvokedAtTrace());
            throw assertionError;
        }
    }


    public void assertNotInvoked(BehaviorDefiningInvocation assertInvocation) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == UNVERIFIED && assertInvocation.matches(observedInvocation) != -1) {
                AssertionError assertionError = new AssertionError(getAssertNotInvokedErrorMessage(assertInvocation, observedInvocation, assertInvocation.getInvokedAtTrace()));
                assertionError.setStackTrace(assertInvocation.getInvokedAtTrace());
                throw assertionError;
            }
        }
    }


    public String createFullReport() {
        ScenarioReport fullScenarioReport = new DefaultScenarioReport();
        return fullScenarioReport.createReport(this);
    }


    public String createObservedInvocationsReport() {
        ObservedInvocationsReport observedInvocationsReport = new ObservedInvocationsReport(testObject);
        return observedInvocationsReport.createReport(this.getObservedInvocations());
    }


    public String createDetailedObservedInvocationsReport() {
        DetailedObservedInvocationsReport observedInvocationsReport = new DetailedObservedInvocationsReport(testObject);
        return observedInvocationsReport.createReport(this.getObservedInvocations());
    }


    public String createSuggestedAssertsReport() {
        SuggestedAssertsReport suggestedAssertsReport = new SuggestedAssertsReport();
        return suggestedAssertsReport.createReport(testObject, getObservedInvocations());
    }


    protected String getAssertNotInvokedErrorMessage(ProxyInvocation proxyInvocation, ObservedInvocation unexpectedInvocation, StackTraceElement[] assertedAt) {
        StringBuilder message = new StringBuilder();
        message.append("Expected no invocation of ");
        message.append(getSimpleMethodName(proxyInvocation.getMethod()));
        message.append(", but it did occur.\nat ");
        message.append(unexpectedInvocation.getInvokedAt());
        message.append("\n");
        message.append(getAssertLocationIndication(assertedAt[0]));
        message.append("\n\n");
        message.append(createFullReport());
        return message.toString();
    }

    protected String getAssertInvokedErrorMessage(ProxyInvocation proxyInvocation, StackTraceElement invokedAt) {
        StringBuilder message = new StringBuilder();
        message.append("Expected invocation of ");
        message.append(getSimpleMethodName(proxyInvocation.getMethod()));
        message.append(", but it didn't occur.\n");
        message.append(getAssertLocationIndication(invokedAt));
        message.append("\n\n");
        message.append(createFullReport());
        return message.toString();
    }

    protected String getInvokedOutOfOrderErrorMessage(ObservedInvocation matchingInvocation, ObservedInvocation outOfOrderInvocation, StackTraceElement assertedAt) {
        StringBuilder message = new StringBuilder();
        message.append("Invocation of ");
        message.append(getSimpleMethodName(matchingInvocation.getMethod()));
        message.append(" was expected to be performed after ");
        message.append(getSimpleMethodName(outOfOrderInvocation.getMethod()));
        message.append(" but actually occurred before it.\n");
        message.append(getAssertLocationIndication(assertedAt));
        message.append("\n\n");
        message.append(createFullReport());
        return message.toString();
    }

    protected String getNoMoreInvocationsErrorMessage(List<ObservedInvocation> unexpectedInvocations, StackTraceElement assertedAt) {
        StringBuilder message = new StringBuilder();
        message.append("No more invocations expected, yet observed following calls:\n");
        message.append(new ObservedInvocationsReport(testObject).createReport(unexpectedInvocations));
        message.append(getAssertLocationIndication(assertedAt));
        message.append("\n\n");
        message.append(createFullReport());
        return message.toString();
    }


    protected String getAssertLocationIndication(StackTraceElement assertedAt) {
        return "asserted at " + assertedAt.toString();
    }


}