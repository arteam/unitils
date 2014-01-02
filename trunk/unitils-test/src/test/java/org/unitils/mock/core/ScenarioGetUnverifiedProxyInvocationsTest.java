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
public class ScenarioGetUnverifiedProxyInvocationsTest extends UnitilsJUnit4 {

    private Scenario scenario;

    private Mock<MatchingInvocation> matchingInvocationMock;
    private Mock<ObservedInvocation> observedInvocationMock1;
    private Mock<ObservedInvocation> observedInvocationMock2;
    private Mock<ObservedInvocation> observedInvocationMock3;
    @Dummy
    private Object proxy;


    @Before
    public void initialize() {
        scenario = new Scenario();
        scenario.addObservedInvocation(observedInvocationMock1.getMock());
        scenario.addObservedInvocation(observedInvocationMock2.getMock());
        scenario.addObservedInvocation(observedInvocationMock3.getMock());

        matchingInvocationMock.returns(-1).matches(null);
    }


    @Test
    public void getUnverifiedInvocations() {
        observedInvocationMock1.returns(proxy).getProxy();
        observedInvocationMock2.returns(proxy).getProxy();
        matchingInvocationMock.returns(1).matches(observedInvocationMock2.getMock());
        scenario.verifyInvocation(matchingInvocationMock.getMock());

        List<ObservedInvocation> result = scenario.getUnverifiedProxyInvocations(proxy);
        assertEquals(asList(observedInvocationMock1.getMock()), result);
    }

    @Test
    public void emptyWhenNoObservedInvocations() {
        scenario.reset();
        List<ObservedInvocation> result = scenario.getUnverifiedProxyInvocations(proxy);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoAllInvocationsVerified() {
        observedInvocationMock1.returns(proxy).getProxy();
        observedInvocationMock2.returns(proxy).getProxy();
        matchingInvocationMock.returns(1).matches(observedInvocationMock1.getMock());
        matchingInvocationMock.returns(1).matches(observedInvocationMock2.getMock());
        scenario.verifyInvocation(matchingInvocationMock.getMock());
        scenario.verifyInvocation(matchingInvocationMock.getMock());

        List<ObservedInvocation> result = scenario.getUnverifiedProxyInvocations(proxy);
        assertTrue(result.isEmpty());
    }
}
