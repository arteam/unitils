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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class MockUnitilsCreateMockIntegrationTest {


    @Test
    public void createMock() {
        Mock<TestInterface> result = MockUnitils.createMock("mockName", TestInterface.class, this);
        assertEquals("mockName", result.toString());
        assertTrue(result.getMock().method().isEmpty());
    }

    @Test
    public void createMockWithDefaultName() {
        Mock<TestInterface> result = MockUnitils.createMock(TestInterface.class, this);
        assertEquals("testInterface", result.toString());
        assertTrue(result.getMock().method().isEmpty());
    }

    @Test
    public void createForCoverage() {
        new MockUnitils();
    }


    private static interface TestInterface {

        List<String> method();
    }
}