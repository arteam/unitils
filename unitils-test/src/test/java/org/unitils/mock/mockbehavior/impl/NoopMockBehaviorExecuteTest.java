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

import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class NoopMockBehaviorExecuteTest {

    private NoopMockBehavior noopMockBehavior;


    @Before
    public void initialize() throws Exception {
        noopMockBehavior = new NoopMockBehavior();
    }


    @Test
    public void doesNothingAndReturnsNull() throws Exception {
        Object result = noopMockBehavior.execute(null);
        assertNull(result);
    }
}

