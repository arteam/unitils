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
package org.unitils.mock.mockbehavior.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.BehaviorDefiningInvocation;

import java.util.Map;

/**
 * @author Tim Ducheyne
 */
public class ChainedMockBehaviorInstallChainTest extends UnitilsJUnit4 {

    private ChainedMockBehavior chainedMockBehavior;

    private Mock<BehaviorDefiningInvocation> behaviorDefiningInvocationMock;
    private Mock<Mock<Map>> mockMock;
    @Dummy
    private Map mockProxy;


    @Before
    public void initialize() {
        chainedMockBehavior = new ChainedMockBehavior(mockMock.getMock(), behaviorDefiningInvocationMock.getMock());

        mockMock.returns(mockProxy).getMock();
    }


    @Test
    public void installChain() {
        chainedMockBehavior.installChain();
        behaviorDefiningInvocationMock.assertInvoked().setMockBehavior(new ValueReturningMockBehavior(mockProxy));
    }
}
