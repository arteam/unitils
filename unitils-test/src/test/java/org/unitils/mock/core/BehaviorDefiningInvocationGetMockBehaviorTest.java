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
import org.unitils.mock.mockbehavior.MockBehavior;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class BehaviorDefiningInvocationGetMockBehaviorTest extends UnitilsJUnit4 {

    private BehaviorDefiningInvocation behaviorDefiningInvocation;

    @Dummy
    private MockBehavior mockBehavior;
    @Dummy
    private MatchingInvocation matchingInvocation;

    @Before
    public void initialize() {
        behaviorDefiningInvocation = new BehaviorDefiningInvocation(matchingInvocation, null, false);
    }


    @Test
    public void getMockBehavior() {
        behaviorDefiningInvocation = new BehaviorDefiningInvocation(matchingInvocation, mockBehavior, false);

        MockBehavior result = behaviorDefiningInvocation.getMockBehavior();
        assertSame(mockBehavior, result);
    }

    @Test
    public void getSetMockBehavior() {
        behaviorDefiningInvocation.setMockBehavior(mockBehavior);

        MockBehavior result = behaviorDefiningInvocation.getMockBehavior();
        assertSame(mockBehavior, result);
    }

    @Test
    public void nullWhenNoMockBehavior() {
        MockBehavior result = behaviorDefiningInvocation.getMockBehavior();
        assertNull(result);
    }
}
