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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class Scenario {

    /**
     * Insertion ordered map that keeps track of the registered invocations and whether or not they have already been checked for invocation with the various assertX() methods in this class.
     */
    protected List<Invocation> observedInvocations = new ArrayList<Invocation>();

    protected List<Invocation> unverifiedInvocations = new ArrayList<Invocation>();


    public void addObservedInvocation(Invocation invocation) {
        observedInvocations.add(invocation);
        unverifiedInvocations.add(invocation);
    }

    public List<Invocation> getObservedInvocations() {
        return observedInvocations;
    }


    public List<Invocation> getUnverifiedInvocations() {
        return unverifiedInvocations;
    }


    public Invocation verifyMatchingInvocation(InvocationMatcher invocationMatcher) {
        Iterator<Invocation> iterator = unverifiedInvocations.iterator();
        while (iterator.hasNext()) {
            Invocation invocation = iterator.next();
            if (invocationMatcher.matches(invocation)) {
                iterator.remove();
                return invocation;
            }
        }
        return null;
    }

}
