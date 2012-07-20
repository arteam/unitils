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

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.reflect.ClassWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class SpringTestListenerBeforeTestClassTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringTestListener springTestListener;

    private Mock<SpringTestManager> springTestManagerMock;


    @Before
    public void initialize() {
        MyTestExecutionListener.testContext = null;
        MyTestExecutionListener.exceptionToThrow = null;

        springTestListener = new SpringTestListener(springTestManagerMock.getMock());
    }


    @Test
    public void notSpringWhenNoSpringAnnotations() throws Exception {
        springTestListener.beforeTestClass(new ClassWrapper(TestClass1.class));

        springTestManagerMock.assertInvoked().reset();
        springTestManagerMock.assertNotInvoked().setSpringTestContext(null);
    }

    @Test
    public void annotatedWithTestExecutionListener() throws Exception {
        springTestListener.beforeTestClass(new ClassWrapper(TestClass2.class));

        springTestManagerMock.assertInvoked().reset();
        springTestManagerMock.assertInvoked().setSpringTestContext(null);
        assertEquals(TestClass2.class, MyTestExecutionListener.testContext.getTestClass());
    }

    @Test
    public void annotatedWithContextConfiguration() throws Exception {
        springTestListener.beforeTestClass(new ClassWrapper(TestClass3.class));

        springTestManagerMock.assertInvoked().reset();
        springTestManagerMock.assertInvoked().setSpringTestContext(null);
    }

    @Test
    public void exceptionWhenBeforeTestClassFails() throws Exception {
        try {
            MyTestExecutionListener.exceptionToThrow = new RuntimeException("test");

            springTestListener.beforeTestClass(new ClassWrapper(TestClass2.class));
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Exception occurred during before test class.\n" +
                    "Reason: RuntimeException: test", e.getMessage());
        }
    }


    private static class TestClass1 {
    }

    @TestExecutionListeners(MyTestExecutionListener.class)
    private static class TestClass2 {
    }

    @ContextConfiguration(locations = "empty-context.xml")
    private static class TestClass3 {
    }


    private static class MyTestExecutionListener extends AbstractTestExecutionListener {

        public static TestContext testContext;
        public static Exception exceptionToThrow;

        @Override
        public void beforeTestClass(TestContext testContext) throws Exception {
            MyTestExecutionListener.testContext = testContext;
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }
    }
}
