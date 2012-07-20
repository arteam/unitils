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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class SpringTestManagerGetApplicationContextTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringTestManager springTestManager = new SpringTestManager();


    @Test
    public void testWithApplicationContext() throws Exception {
        TestContext testContext = createTestContext(TestClass1.class);
        springTestManager.setSpringTestContext(testContext);

        ApplicationContext result = springTestManager.getApplicationContext();
        assertNotNull(result);
    }

    @Test
    public void nullWhenNotSpringTest() throws Exception {
        ApplicationContext result = springTestManager.getApplicationContext();
        assertNull(result);
    }

    @Test
    public void nullWhenNoApplicationContext() throws Exception {
        TestContext testContext = createTestContext(TestClass2.class);
        springTestManager.setSpringTestContext(testContext);

        ApplicationContext result = springTestManager.getApplicationContext();
        assertNull(result);
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
