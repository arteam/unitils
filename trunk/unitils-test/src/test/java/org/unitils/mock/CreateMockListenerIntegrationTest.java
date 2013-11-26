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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class CreateMockListenerIntegrationTest implements CreateMockListener {

    private Mock<?> createdMock;
    private String createdMockName;
    private Class<?> createdMockType;


    @Test
    public void createMock() {
        Mock<TestInterface> result = MockUnitils.createMock("mockName", TestInterface.class, this);
        assertSame(result, createdMock);
        assertEquals("mockName", createdMockName);
        assertEquals(TestInterface.class, createdMockType);
    }

    @Test
    public void createPartialMock() {
        Mock<TestInterface> result = MockUnitils.createPartialMock("mockName", TestInterface.class, this);
        assertSame(result, createdMock);
        assertEquals("mockName", createdMockName);
        assertEquals(TestInterface.class, createdMockType);
    }


    public void mockCreated(Mock<?> mock, String name, Class<?> type) {
        this.createdMock = mock;
        this.createdMockName = name;
        this.createdMockType = type;
    }


    private static interface TestInterface {
    }
}