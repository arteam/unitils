/*
 * Copyright 2006 the original author or authors.
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

import java.lang.reflect.Method;

/**
 * Listener for test events. The events must follow following ordering:
 * <ul>
 * <li>[Unitils] beforeAll</li>
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
 * <li>[Unitils] afterTestClass    - TestClass1</li>
 * <li>[Unitils] afterAll</li>
 * </ul>
 * <p/>
 * todo exception behavior javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class TestListener {


    public void beforeAll() {
        // empty
    }

    public void beforeTestClass(Class<?> testClass) {
        // empty
    }

    public void beforeTestSetUp(Object testObject) {
        // empty
    }

    public void beforeTestMethod(Object testObject, Method testMethod) {
        // empty
    }

    /**
     * @param testObject    The test object, not null
     * @param testMethod    The test method, not null
     * @param testThrowable The throwable thrown during the test or beforeTestMethod, null if none was thrown
     */
    public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable) {
        // empty
    }

    public void afterTestTearDown(Object testObject) {
        // empty
    }

    public void afterTestClass(Class<?> testClass) {
        // empty
    }

    public void afterAll() {
        // empty
    }

}
