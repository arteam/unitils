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
package org.unitilsnew;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

class JUnit4TestExecutor implements TestExecutor {

    protected Result result;

    protected JUnit4TestExecutor() {
        super();
    }

    public void runTests(Class<?>... testClasses) throws Exception {
        result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());

        for (Class<?> testClass : testClasses) {
            UnitilsJUnit4TestClassRunner testClassRunner = new TestUnitilsJUnit4TestClassRunner(testClass);
            testClassRunner.run(runNotifier);
        }
    }

    public void runTests(String testGroup, Class<?>... testClasses) throws Exception {
        runTests(testClasses);
    }

    public int getRunCount() {
        return result.getRunCount();
    }

    public int getFailureCount() {
        return result.getFailureCount();
    }

    public int getIgnoreCount() {
        return result.getIgnoreCount();
    }

    /**
     * Overridden test class runner to be able to use the {@link TracingTestListener} as test listener.
     */
    class TestUnitilsJUnit4TestClassRunner extends UnitilsJUnit4TestClassRunner {

        public TestUnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

    }
}