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
import org.unitils.core.util.ObjectToFormat;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class MockUnitilsCreateDummyIntegrationTest {


    @Test
    public void createDummy() {
        TestInterface result = MockUnitils.createDummy("dummyName", TestInterface.class);
        assertEquals("Proxy<dummyName>", ((ObjectToFormat) result).$formatObject());
        assertTrue(result.method().isEmpty());
    }

    @Test
    public void createDummyWithDefaultName() {
        TestInterface result = MockUnitils.createDummy(TestInterface.class);
        assertEquals("Proxy<testInterface>", ((ObjectToFormat) result).$formatObject());
        assertTrue(result.method().isEmpty());
    }


    public static interface TestInterface {

        List<String> method();
    }
}