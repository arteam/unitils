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

import static org.unitils.util.AnnotationUtils.getClassLevelAnnotation;

/**
 * Class for holding the current test instance.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CurrentTestClass {

    protected TestContext testContext;


    public CurrentTestClass(TestContext testContext) {
        this.testContext = testContext;
    }


    public Class<?> getTestClass() {
        return testContext.getTestClass();
    }

    public TestContext getTestContext() {
        return testContext;
    }


    /**
     * Gets the test application context.
     *
     * @return The application context, null if there is no test application context
     */
    public ApplicationContext getApplicationContext() {
        if (!hasContextConfiguration()) {
            return null;
        }
        return testContext.getApplicationContext();
    }

    /**
     * Check whether the test has an @ContextConfiguration annotation. If there is no such annotation, we don't need
     * to try to load an application context because this would cause a file not found exception.
     *
     * @return True if the test class or a super class has an @ContextConfiguration annotation
     */
    protected boolean hasContextConfiguration() {
        Class<?> testClass = testContext.getTestClass();
        if (testClass == null) {
            return false;
        }
        return getClassLevelAnnotation(ContextConfiguration.class, testClass) != null;
    }
}