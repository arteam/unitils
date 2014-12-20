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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.core.ObservedInvocation;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class MockUnitilsGetObservedInvocationsIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> testInterfaceMock;


    @Test
    public void getObservedInvocations() {
        testInterfaceMock.getMock().method();
        testInterfaceMock.getMock().method();
        testInterfaceMock.getMock().method();

        List<ObservedInvocation> result = MockUnitils.getObservedInvocations();
        assertEquals(3, result.size());
    }

    @Test
    public void emptyWhenNoObservedInvocations() {
        List<ObservedInvocation> result = MockUnitils.getObservedInvocations();
        assertTrue(result.isEmpty());
    }


    private static interface TestInterface {

        List<String> method();
    }
}