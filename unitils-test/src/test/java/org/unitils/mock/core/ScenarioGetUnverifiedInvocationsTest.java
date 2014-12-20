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
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ScenarioGetUnverifiedInvocationsTest extends UnitilsJUnit4 {

    private Scenario scenario;

    private Mock<MatchingInvocation> matchingInvocationMock;
    @Dummy
    private ObservedInvocation observedInvocation1;
    @Dummy
    private ObservedInvocation observedInvocation2;
    @Dummy
    private ObservedInvocation observedInvocation3;


    @Before
    public void initialize() {
        scenario = new Scenario();
        scenario.addObservedInvocation(observedInvocation1);
        scenario.addObservedInvocation(observedInvocation2);
        scenario.addObservedInvocation(observedInvocation3);

        matchingInvocationMock.returns(-1).matches(null);
    }


    @Test
    public void getUnverifiedInvocations() {
        matchingInvocationMock.returns(1).matches(observedInvocation2);
        scenario.verifyInvocation(matchingInvocationMock.getMock());

        List<ObservedInvocation> result = scenario.getUnverifiedInvocations();
        assertEquals(asList(observedInvocation1, observedInvocation3), result);
    }

    @Test
    public void emptyWhenNoObservedInvocations() {
        scenario.reset();
        List<ObservedInvocation> result = scenario.getUnverifiedInvocations();
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoAllInvocationsVerified() {
        matchingInvocationMock.returns(1).matches(observedInvocation1);
        matchingInvocationMock.returns(1).matches(observedInvocation2);
        matchingInvocationMock.returns(1).matches(observedInvocation3);
        scenario.verifyInvocation(matchingInvocationMock.getMock());
        scenario.verifyInvocation(matchingInvocationMock.getMock());
        scenario.verifyInvocation(matchingInvocationMock.getMock());

        List<ObservedInvocation> result = scenario.getUnverifiedInvocations();
        assertTrue(result.isEmpty());
    }
}
