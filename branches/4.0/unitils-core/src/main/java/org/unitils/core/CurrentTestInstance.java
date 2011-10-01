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

import org.springframework.test.context.TestContext;

import java.lang.reflect.Method;

/**
 * Class for holding the current test instance.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CurrentTestInstance extends CurrentTestClass {


    public CurrentTestInstance(TestContext testContext) {
        super(testContext);
    }


    public Object getTestObject() {
        return testContext.getTestInstance();
    }

    public Method getTestMethod() {
        return testContext.getTestMethod();
    }

    public Throwable getTestThrowable() {
        return testContext.getTestException();
    }
}