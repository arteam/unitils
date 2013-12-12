/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.TestRunnerAccessor;

/**
 * Object that holds information about the current test execution. During a test run
 * it always knows which test is running.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestContext {

    /* The class who's tests are currenlty executed */
    private Class<?> testClass;

    /* The instance of the test class which is currenlty executed */
    private Object testObject;

    /* The test method which is currently executed */
    private Method testMethod;
    
    /* The runner executing the current test */
    private TestRunnerAccessor runner;


    public Class<?> getTestClass() {
        return testClass;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    public Object getTestObject() {
        return testObject;
    }

    public void setTestObject(Object testObject) {
        this.testObject = testObject;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    public TestRunnerAccessor getRunner() {
        return runner;
    }

    public void setRunner(TestRunnerAccessor runner) {
        this.runner = runner;
    }
        

}
