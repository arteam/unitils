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
 * todo javadoc
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

    public void afterTestMethod(Object testObject, Method testMethod) {
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