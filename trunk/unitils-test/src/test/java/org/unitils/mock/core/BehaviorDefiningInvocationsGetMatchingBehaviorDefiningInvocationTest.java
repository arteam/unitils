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
import org.unitils.mock.core.proxy.ProxyInvocation;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class BehaviorDefiningInvocationsGetMatchingBehaviorDefiningInvocationTest extends UnitilsJUnit4 {

    private BehaviorDefiningInvocations behaviorDefiningInvocations;

    private Mock<BehaviorDefiningInvocation> behaviorDefiningInvocationMock1;
    private Mock<BehaviorDefiningInvocation> behaviorDefiningInvocationMock2;
    private Mock<BehaviorDefiningInvocation> behaviorDefiningInvocationMock3;

    @Dummy
    private ProxyInvocation proxyInvocation;


    @Before
    public void initialize() {
        behaviorDefiningInvocations = new BehaviorDefiningInvocations();

        behaviorDefiningInvocations.addBehaviorDefiningInvocation(behaviorDefiningInvocationMock1.getMock());
        behaviorDefiningInvocations.addBehaviorDefiningInvocation(behaviorDefiningInvocationMock2.getMock());
        behaviorDefiningInvocations.addBehaviorDefiningInvocation(behaviorDefiningInvocationMock3.getMock());
    }


    @Test
    public void lastInvocationHighestMatchingScore() {
        behaviorDefiningInvocationMock1.returns(10).matches(proxyInvocation);
        behaviorDefiningInvocationMock2.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(30).matches(proxyInvocation);

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertSame(behaviorDefiningInvocationMock3.getMock(), result);
    }

    @Test
    public void firstInvocationHighestMatchingScore() {
        behaviorDefiningInvocationMock1.returns(30).matches(proxyInvocation);
        behaviorDefiningInvocationMock2.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(10).matches(proxyInvocation);

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertSame(behaviorDefiningInvocationMock1.getMock(), result);
    }

    @Test
    public void invocationWithHighestNumberOfNotNullArgumentsWhenEqualMatchingScore() {
        behaviorDefiningInvocationMock1.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock2.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock1.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock2.returns(6).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock3.returns(4).getNrOfNotNullArguments();

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertSame(behaviorDefiningInvocationMock2.getMock(), result);
    }

    @Test
    public void firstOneTimeMatchInvocationWhenEqualMatchingScoreAndEqualNumberOfNotNullArguments() {
        behaviorDefiningInvocationMock1.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock2.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock1.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock2.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock3.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock1.returns(true).isOneTimeMatch();
        behaviorDefiningInvocationMock2.returns(true).isOneTimeMatch();
        behaviorDefiningInvocationMock3.returns(false).isOneTimeMatch();

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertSame(behaviorDefiningInvocationMock1.getMock(), result);
    }

    @Test
    public void lastInvocationWhenEqualMatchingScoreAndEqualNumberOfNotNullArgumentsAndNoOneTimeMatch() {
        behaviorDefiningInvocationMock1.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock2.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(20).matches(proxyInvocation);
        behaviorDefiningInvocationMock1.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock2.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock3.returns(5).getNrOfNotNullArguments();
        behaviorDefiningInvocationMock1.returns(false).isOneTimeMatch();
        behaviorDefiningInvocationMock2.returns(false).isOneTimeMatch();
        behaviorDefiningInvocationMock3.returns(false).isOneTimeMatch();

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertSame(behaviorDefiningInvocationMock3.getMock(), result);
    }

    @Test
    public void nullWhenNoBehaviorDefiningInvocations() {
        behaviorDefiningInvocations.reset();

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void nullWhenNoMatchFound() {
        behaviorDefiningInvocationMock1.returns(-1).matches(proxyInvocation);
        behaviorDefiningInvocationMock2.returns(-1).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(-1).matches(proxyInvocation);

        BehaviorDefiningInvocation result = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void removeInvocationWhenOneTimeMatch() {
        behaviorDefiningInvocationMock3.returns(10).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(true).isOneTimeMatch();

        behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertFalse(behaviorDefiningInvocations.getBehaviorDefiningInvocations().contains(behaviorDefiningInvocationMock3.getMock()));
    }

    @Test
    public void keepInvocationWhenNotOneTimeMatch() {
        behaviorDefiningInvocationMock3.returns(10).matches(proxyInvocation);
        behaviorDefiningInvocationMock3.returns(false).isOneTimeMatch();

        behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        assertTrue(behaviorDefiningInvocations.getBehaviorDefiningInvocations().contains(behaviorDefiningInvocationMock3.getMock()));
    }
}
