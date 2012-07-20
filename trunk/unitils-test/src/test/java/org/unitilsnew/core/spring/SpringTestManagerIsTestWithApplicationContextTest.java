/*
 * Copyright 2012,  Unitils.org
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
package org.unitilsnew.core.spring;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class SpringTestManagerIsTestWithApplicationContextTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringTestManager springTestManager = new SpringTestManager();


    @Test
    public void testWithApplicationContext() throws Exception {
        TestContext testContext = createTestContext(TestClass1.class);
        springTestManager.setSpringTestContext(testContext);

        boolean result = springTestManager.isTestWithApplicationContext();
        assertTrue(result);
    }

    @Test
    public void falseWhenNotSpringTest() throws Exception {
        boolean result = springTestManager.isTestWithApplicationContext();
        assertFalse(result);
    }

    @Test
    public void falseWhenNoApplicationContext() throws Exception {
        TestContext testContext = createTestContext(TestClass2.class);
        springTestManager.setSpringTestContext(testContext);

        boolean result = springTestManager.isTestWithApplicationContext();
        assertFalse(result);
    }


    private TestContext createTestContext(Class<?> testClass) {
        SpringTestListener.AccessibleTestContextManager accessibleTestContextManager1 = new SpringTestListener.AccessibleTestContextManager(testClass);
        return accessibleTestContextManager1.getContext();
    }


    @ContextConfiguration(locations = "empty-context.xml")
    private static class TestClass1 {
    }

    private static class TestClass2 {
    }
}
