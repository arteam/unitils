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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ScenarioVerifyInvocationInSequenceTest extends UnitilsJUnit4 {

    private Scenario scenario;

    @Dummy
    private ObservedInvocation observedInvocation1;
    @Dummy
    private ObservedInvocation observedInvocation2;
    @Dummy
    private ObservedInvocation observedInvocation3;
    @Dummy
    private ObservedInvocation unknownObservedInvocation;


    @Before
    public void initialize() {
        scenario = new Scenario();
        scenario.addObservedInvocation(observedInvocation1);
        scenario.addObservedInvocation(observedInvocation2);
        scenario.addObservedInvocation(observedInvocation3);
    }


    @Test
    public void nullWhenInSequence() {
        ObservedInvocation result1 = scenario.verifyInvocationInSequence(observedInvocation1);
        ObservedInvocation result2 = scenario.verifyInvocationInSequence(observedInvocation2);
        ObservedInvocation result3 = scenario.verifyInvocationInSequence(observedInvocation3);
        assertNull(result1);
        assertNull(result2);
        assertNull(result3);
    }

    @Test
    public void lastObservedInvocationWhenNotInSequence() {
        ObservedInvocation result1 = scenario.verifyInvocationInSequence(observedInvocation3);
        ObservedInvocation result2 = scenario.verifyInvocationInSequence(observedInvocation2);
        ObservedInvocation result3 = scenario.verifyInvocationInSequence(observedInvocation1);
        assertNull(result1);
        assertSame(result2, observedInvocation3);
        assertSame(result3, observedInvocation2);
    }

    @Test
    public void nullWhenObservedInvocationNotFound() {
        ObservedInvocation result = scenario.verifyInvocationInSequence(unknownObservedInvocation);
        assertNull(result);
    }

    @Test
    public void nullWhenNullObservedInvocation() {
        ObservedInvocation result = scenario.verifyInvocationInSequence(null);
        assertNull(result);
    }

    @Test
    public void inSequenceWhenVerifyingObservedInvocationMoreThanOnce() {
        scenario.verifyInvocationInSequence(observedInvocation1);
        ObservedInvocation result = scenario.verifyInvocationInSequence(observedInvocation1);
        assertNull(result);
    }
}
