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
import org.unitils.core.UnitilsException;

import static org.junit.Assert.*;
import static org.unitilsnew.core.spring.SpringTestListener.AccessibleTestContextManager;

/**
 * Note: there does not seem to be a way to mock a TestContext
 *
 * @author Tim Ducheyne
 */
public class SpringTestContextWrapperGetApplicationContextTest {

    /* Tested object */
    private SpringTestContextWrapper springTestContextWrapper;


    @Test
    public void getApplicationContext() throws Exception {
        TestContext testContext = createTestContext(TestClass1.class);
        springTestContextWrapper = new SpringTestContextWrapper(testContext);

        ApplicationContext result = springTestContextWrapper.getApplicationContext();

        assertNotNull(result);
    }

    @Test
    public void nullWhenThereIsNoContextConfiguration() throws Exception {
        TestContext testContext = createTestContext(TestClass2.class);
        springTestContextWrapper = new SpringTestContextWrapper(testContext);

        ApplicationContext result = springTestContextWrapper.getApplicationContext();

        assertNull(result);
    }

    @Test
    public void applicationContextIsCached() throws Exception {
        TestContext testContext = createTestContext(TestClass1.class);
        springTestContextWrapper = new SpringTestContextWrapper(testContext);

        ApplicationContext result1 = springTestContextWrapper.getApplicationContext();
        ApplicationContext result2 = springTestContextWrapper.getApplicationContext();

        assertSame(result1, result2);
    }

    @Test
    public void exceptionWhenUnableToLoadApplicationContext() throws Exception {
        TestContext testContext = createTestContext(TestClass3.class);
        springTestContextWrapper = new SpringTestContextWrapper(testContext);

        try {
            springTestContextWrapper.getApplicationContext();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to load spring application context.\n" +
                    "Reason: IllegalStateException: Failed to load ApplicationContext", e.getMessage());
        }
    }


    private TestContext createTestContext(Class<?> testClass) {
        AccessibleTestContextManager accessibleTestContextManager1 = new AccessibleTestContextManager(testClass);
        return accessibleTestContextManager1.getContext();
    }


    @ContextConfiguration(locations = "empty-context.xml")
    private static class TestClass1 {
    }

    private static class TestClass2 {
    }

    @ContextConfiguration(locations = "xxx")
    @SuppressWarnings("SpringContextConfigurationInspection")
    private static class TestClass3 {
    }
}
