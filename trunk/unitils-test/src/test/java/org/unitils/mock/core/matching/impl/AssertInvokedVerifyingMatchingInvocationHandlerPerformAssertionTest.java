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
package org.unitils.mock.core.matching.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.Scenario;

/**
 * @author Tim Ducheyne
 */
public class AssertInvokedVerifyingMatchingInvocationHandlerPerformAssertionTest extends UnitilsJUnit4 {

    private AssertInvokedVerifyingMatchingInvocationHandler assertInvokedVerifyingMatchingInvocationHandler;

    private Mock<Scenario> scenarioMock;
    @Dummy
    private BehaviorDefiningInvocation behaviorDefiningInvocation;


    @Before
    public void initialize() {
        assertInvokedVerifyingMatchingInvocationHandler = new AssertInvokedVerifyingMatchingInvocationHandler(scenarioMock.getMock(), null);
    }


    @Test
    public void performAssertion() {
        assertInvokedVerifyingMatchingInvocationHandler.performAssertion(scenarioMock.getMock(), behaviorDefiningInvocation);
        scenarioMock.assertInvoked().assertInvoked(behaviorDefiningInvocation);
    }
}
