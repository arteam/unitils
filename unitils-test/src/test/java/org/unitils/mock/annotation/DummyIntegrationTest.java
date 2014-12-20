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
package org.unitils.mock.annotation;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertNotNull;


/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DummyIntegrationTest extends UnitilsJUnit4 {

    @Dummy
    private TestClass dummy;


    @Test
    public void testDummy() {
        assertNotNull(dummy);
        assertNotNull(dummy.getId());
        assertNotNull(dummy.getTestClass());
    }


    public static class TestClass {

        private Long id = 10L;
        private TestClass testClass;

        public Long getId() {
            return id;
        }

        public TestClass getTestClass() {
            return testClass;
        }
    }
}
