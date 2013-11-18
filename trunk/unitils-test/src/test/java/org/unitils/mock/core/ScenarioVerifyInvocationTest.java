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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ScenarioVerifyInvocationTest extends UnitilsJUnit4 {

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
    public void verifyInvocation() {
        matchingInvocationMock.returns(1).matches(observedInvocation2);

        ObservedInvocation result = scenario.verifyInvocation(matchingInvocationMock.getMock());
        assertSame(observedInvocation2, result);
        assertEquals(asList(observedInvocation1, observedInvocation3), scenario.getUnverifiedInvocations());
    }

    @Test
    public void nullWhenNoMatchingInvocationFound() {
        ObservedInvocation result = scenario.verifyInvocation(matchingInvocationMock.getMock());
        assertNull(result);
    }

    @Test
    public void skipInvocationsThatWereAlreadyVerified() {
        matchingInvocationMock.returns(1).matches(observedInvocation2);

        ObservedInvocation result1 = scenario.verifyInvocation(matchingInvocationMock.getMock());
        ObservedInvocation result2 = scenario.verifyInvocation(matchingInvocationMock.getMock());
        assertSame(observedInvocation2, result1);
        assertNull(result2);
    }
}
