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

import java.util.ArrayList;
import java.util.List;

import static org.unitils.mock.core.Scenario.VerificationStatus.*;

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


    public void addObservedInvocation(ObservedInvocation mockInvocation) {
        observedInvocations.add(mockInvocation);
        invocationVerificationStatuses.add(UNVERIFIED);
    }

    public List<ObservedInvocation> getObservedInvocations() {
        return observedInvocations;
    }


    public List<ObservedInvocation> getUnverifiedInvocations() {
        List<ObservedInvocation> unverifiedInvocations = new ArrayList<ObservedInvocation>();
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == UNVERIFIED) {
                unverifiedInvocations.add(observedInvocation);
            }
        }
        return unverifiedInvocations;
    }

    public ObservedInvocation verifyInvocation(MatchingInvocation matchingInvocation) {
        for (int i = 0; i < observedInvocations.size(); i++) {
            ObservedInvocation observedInvocation = observedInvocations.get(i);
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == UNVERIFIED && matchingInvocation.matches(observedInvocation) != -1) {
                // Found a match that's not verified yet. Mark as verified and proceed.
                invocationVerificationStatuses.set(i, VERIFIED);
                return observedInvocation;
            }
        }
        return null;
    }

    public ObservedInvocation verifyInvocationInSequence(ObservedInvocation observedInvocation) {
        int index = observedInvocations.indexOf(observedInvocation);
        if (index == -1) {
            return null;
        }
        invocationVerificationStatuses.set(index, VERIFIED_IN_ORDER);

        ObservedInvocation matchingInvocation = null;
        for (int i = index + 1; i < observedInvocations.size(); i++) {
            VerificationStatus invocationVerificationStatus = invocationVerificationStatuses.get(i);
            if (invocationVerificationStatus == VERIFIED_IN_ORDER) {
                return observedInvocations.get(i);
            }
        }
        return matchingInvocation;
    }
}