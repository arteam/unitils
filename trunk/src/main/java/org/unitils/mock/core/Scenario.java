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
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.DefaultScenarioReport;
import org.unitils.mock.report.impl.MethodFormatUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class Scenario {


    protected List<ObservedInvocation> observedInvocations = new ArrayList<ObservedInvocation>();

    protected List<ObservedInvocation> unverifiedInvocations = new ArrayList<ObservedInvocation>();


    public void reset() {
        observedInvocations.clear();
        unverifiedInvocations.clear();
    }

    public void addObservedMockInvocation(ObservedInvocation mockInvocation) {
        observedInvocations.add(mockInvocation);
        unverifiedInvocations.add(mockInvocation);
    }

    public List<ObservedInvocation> getObservedInvocations() {
        return observedInvocations;
    }


    public void assertNoMoreInvocations() {
        if (!unverifiedInvocations.isEmpty()) {
            ObservedInvocation observedInvocation = unverifiedInvocations.get(0);
            throw new AssertionError(getNoMoreInvocationsErrorMessage(observedInvocation));
        }
    }


    public void assertInvoked(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        ObservedInvocation unverifiedInvocation = getMatchingUnverifiedInvocation(behaviorDefiningInvocation);
        if (unverifiedInvocation == null) {
            throw new AssertionError(getAssertInvokedErrorMessage(behaviorDefiningInvocation));
        }
        unverifiedInvocations.remove(unverifiedInvocation);
    }


    public void assertNotInvoked(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        ObservedInvocation unverifiedInvocation = getMatchingUnverifiedInvocation(behaviorDefiningInvocation);
        if (unverifiedInvocation != null) {
            unverifiedInvocations.remove(unverifiedInvocation);
            throw new AssertionError(getAssertNotInvokedErrorMessage(behaviorDefiningInvocation));
        }
    }


    public String createReport() {
        ScenarioReport scenarioReport = new DefaultScenarioReport();
        return scenarioReport.createReport("Mock report:", this);
    }


    protected ObservedInvocation getMatchingUnverifiedInvocation(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        for (ObservedInvocation observedInvocation : unverifiedInvocations) {
            if (behaviorDefiningInvocation.matches(observedInvocation)) {
                return observedInvocation;
            }
        }
        return null;
    }


    protected String getAssertNotInvokedErrorMessage(ProxyInvocation proxyInvocation) {
        StringBuilder message = new StringBuilder();
        Method method = proxyInvocation.getMethod();
        message.append("Prohibited invocation of ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(" at ");
        message.append(proxyInvocation.getInvokedAt());
        message.append("\n");
        message.append(createReport());
        return message.toString();
    }


    // todo check message
    protected String getAssertInvokedErrorMessage(ProxyInvocation proxyInvocation) {
        StringBuilder message = new StringBuilder();
        Method method = proxyInvocation.getMethod();
        message.append("Expected invocation of ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(", but the invocation didn't occur.");
        message.append("\n");
        message.append(createReport());
        return message.toString();
    }


    protected String getNoMoreInvocationsErrorMessage(ProxyInvocation proxyInvocation) {
        StringBuilder message = new StringBuilder();
        Method method = proxyInvocation.getMethod();
        message.append("No more invocations expected, but ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(" was called from ");
        message.append(proxyInvocation.getInvokedAt());
        message.append("\n");
        message.append(createReport());
        return message.toString();
    }


}