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
package org.unitilsnew.core;

import org.unitilsnew.core.reflect.ClassWrapper;

import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * Listener for test events. The events must follow following ordering:
 * <ul>
 * <li>[Unitils] beforeTestClass   - TestClass1</li>
 * <li>[Test]    testBeforeClass   - TestClass1  (not for JUnit3)</li>
 * <li>[Unitils] beforeTestSetUp   - TestClass1</li>
 * <li>[Test]    testSetUp         - TestClass1</li>
 * <li>[Unitils] beforeTestMethod  - TestClass1 - test1</li>
 * <li>[Test]    testMethod        - TestClass1 - test1</li>
 * <li>[Unitils] afterTestMethod   - TestClass1 - test1</li>
 * <li>[Test]    testTearDown      - TestClass1</li>
 * <li>[Unitils] afterTestTearDown - TestClass1</li>
 * <li>[Unitils] beforeTestSetUp   - TestClass1</li>
 * <li>[Test]    testSetUp         - TestClass1</li>
 * <li>[Unitils] beforeTestMethod  - TestClass1 - test2</li>
 * <li>[Test]    testMethod        - TestClass1 - test2</li>
 * <li>[Unitils] afterTestMethod   - TestClass1 - test2</li>
 * <li>[Test]    testTearDown      - TestClass1</li>
 * <li>[Unitils] afterTestTearDown - TestClass1</li>
 * <li>[Test]    testAfterClass    - TestClass1 (not for JUnit3)</li>
 * </ul>
 * <p/>
 * The after methods will always when be called the before counterpart has run (or begun). For example if
 * an exception occurs during the beforeTestSetup method, the afterTestTearDown method will still be called.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class TestListener {

    public TestPhase getTestPhase() {
        return EXECUTION;
    }

    /**
     * Invoked before the generic class setup (e.g. @BeforeClass) is performed.
     *
     * @param classWrapper The test class, not null
     */
    public void beforeTestClass(ClassWrapper classWrapper) {
    }

    /**
     * Invoked before the test setup (eg @Before) is run.
     * This can be overridden to for example initialize the test-fixture.
     *
     * @param testInstance The test instance, not null
     */
    public void beforeTestSetUp(TestInstance testInstance) {
    }

    /**
     * Invoked before the test but after the test setup (eg @Before) is run.
     * This can be overridden to for example further initialize the test-fixture using values that were set during
     * the test setup.
     *
     * @param testInstance The test instance, not null
     */
    public void beforeTestMethod(TestInstance testInstance) {
    }

    /**
     * Invoked after the test run but before the test tear down (e.g. @After).
     * This can be overridden to for example add assertions for testing the result of the test.
     * It the before method or the test raised an exception, this exception will be passed to the method.
     *
     * @param testInstance  The test instance, not null
     * @param testThrowable The throwable thrown during the test or beforeTestMethod, null if none was thrown
     */
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
    }

    /**
     * Invoked after the test tear down (eg @After).
     * This can be overridden to for example perform extra cleanup after the test.
     *
     * @param testInstance  The test instance, not null
     * @param testThrowable The throwable thrown during the test or beforeTestMethod, null if none was thrown
     */
    public void afterTestTearDown(TestInstance testInstance, Throwable testThrowable) {
    }

}
