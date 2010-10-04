/*
 * Copyright 2006-2007,  Unitils.org
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
import org.unitils.core.UnitilsException;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the mock module functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockModuleTest {

    /* Class under test */
    private MockModule mockModule = new MockModule();

    private RegularMockType regularMockType = new RegularMockType();
    private GenericMockType genericMockType = new GenericMockType();
    private MockTypeNotSpecified mockTypeNotSpecified = new MockTypeNotSpecified();


    @Test
    public void createAndInjectMocksIntoTest() {
        mockModule.createAndInjectMocksIntoTest(regularMockType);
        assertNotNull(regularMockType.mock.getMock());
    }


    @Test
    public void genericMockType() {
        mockModule.createAndInjectMocksIntoTest(genericMockType);
        assertNotNull(genericMockType.mock.getMock());
    }


    @Test(expected = UnitilsException.class)
    public void mockTypeNotSpecified() {
        mockModule.createAndInjectMocksIntoTest(mockTypeNotSpecified);
        assertNotNull(mockTypeNotSpecified.mock.getMock());
    }


    private static class RegularMockType {

        public Mock<Properties> mock;

    }

    private static class GenericMockType {

        public Mock<Map<String, List<String>>> mock;

    }

    private static class MockTypeNotSpecified {

        public Mock mock;

    }

}