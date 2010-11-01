/*
 * Copyright Unitils.org
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
package org.unitils.core;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.reflect.Method;

import static org.unitils.util.AnnotationUtils.getClassLevelAnnotation;

/**
 * Empty test execution listener implementation. Override methods to add behavior.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class TestExecutionListenerAdapter implements TestExecutionListener {

    public final void beforeTestClass(TestContext testContext) throws Exception {
        beforeTestClass(testContext.getTestClass(), testContext);
    }

    public final void prepareTestInstance(TestContext testContext) throws Exception {
        prepareTestInstance(testContext.getTestInstance(), testContext);
    }

    public final void beforeTestMethod(TestContext testContext) throws Exception {
        beforeTestMethod(testContext.getTestInstance(), testContext.getTestMethod(), testContext);
    }

    public final void afterTestMethod(TestContext testContext) throws Exception {
        afterTestMethod(testContext.getTestInstance(), testContext.getTestMethod(), testContext.getTestException(), testContext);
    }

    public final void afterTestClass(TestContext testContext) throws Exception {
        afterTestClass(testContext.getTestClass(), testContext);
    }


    /**
     * Invoked before the generic class setup (e.g. @BeforeClass) is performed.
     *
     * Avoid using this method: Spring did not implement this for JUnit 3
     *
     * @param testClass   The class whose test methods are about to be executed, not null
     * @param testContext The current test context, not null
     */
    public void beforeTestClass(Class<?> testClass, TestContext testContext) throws Exception {
        // todo remove not always called
    }

    /**
     * Invoked before any of the test in a test class are run.
     * This can be overridden to for example add test-class initialization.
     *
     * @param testObject  The test class, not null
     * @param testContext The current test context, not null
     */
    public void prepareTestInstance(Object testObject, TestContext testContext) throws Exception {
        // empty
    }

    /**
     * Invoked before the test setup (eg @Before) is run.
     * This can be overridden to for example further initialize the test-fixture using values that were set during
     * the test setup.
     *
     * @param testObject  The test instance, not null
     * @param testMethod  The test method, not null
     * @param testContext The current test context, not null
     */
    public void beforeTestMethod(Object testObject, Method testMethod, TestContext testContext) throws Exception {
        // empty
    }

    /**
     * Invoked after the test run but before the test tear down (e.g. @After).
     * This can be overridden to for example add assertions for testing the result of the test.
     * It the before method or the test raised an exception, this exception will be passed to the method.
     *
     * @param testObject    The test instance, not null
     * @param testMethod    The test method, not null
     * @param testThrowable The throwable thrown during the test or beforeTestMethod, null if none was thrown
     * @param testContext   The current test context, not null
     */
    public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable, TestContext testContext) throws Exception {
        // empty
    }

    public void afterTestClass(Class<?> testClass, TestContext testContext) throws Exception {
        // todo remove not always called 
    }


    /**
     * Gets the test application context.
     *
     * @param testContext The current test context, not null
     * @return The application context, null if there is no test application context
     */
    protected ApplicationContext getApplicationContext(TestContext testContext) {
        if (!hasContextConfiguration(testContext)) {
            return null;
        }
        return testContext.getApplicationContext();
    }

    /**
     * Check whether the test has an @ContextConfiguration annotation. If there is no such annotation, we don't need
     * to try to load an application context because this would cause a file not found exception.
     *
     * @param testContext The current test context, not null
     * @return True if the test class or a super class has an @ContextConfiguration annotation
     */
    private boolean hasContextConfiguration(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        if (testClass == null) {
            return false;
        }
        return getClassLevelAnnotation(ContextConfiguration.class, testClass) != null;
    }
}
