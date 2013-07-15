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
package org.unitils.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.unitils.core.UnitilsException;
import org.unitils.core.reflect.ClassWrapper;

/**
 * @author Tim Ducheyne
 */
public class SpringTestContextWrapper {

    protected TestContext wrappedTestContext;
    protected ApplicationContext applicationContext;


    public SpringTestContextWrapper(TestContext wrappedTestContext) {
        this.wrappedTestContext = wrappedTestContext;
    }


    public TestContext getWrappedTestContext() {
        return wrappedTestContext;
    }


    /**
     * Gets the test application context.
     *
     * @return The application context, null if there is no test application context
     */
    public ApplicationContext getApplicationContext() {
        if (applicationContext != null) {
            return applicationContext;
        }
        applicationContext = loadApplicationContext();
        return applicationContext;
    }


    protected ApplicationContext loadApplicationContext() {
        if (!hasContextConfiguration()) {
            return null;
        }
        try {
            return wrappedTestContext.getApplicationContext();
        } catch (Exception e) {
            throw new UnitilsException("Unable to load spring application context.", e);
        }
    }

    /**
     * Check whether the test has an @ContextConfiguration annotation. If there is no such annotation, we don't need
     * to try to load an application context because this would cause a file not found exception.
     *
     * @return True if the test class or a super class has an @ContextConfiguration annotation
     */
    protected boolean hasContextConfiguration() {
        Class<?> testClass = wrappedTestContext.getTestClass();
        ClassWrapper classWrapper = new ClassWrapper(testClass);
        return classWrapper.hasAnnotation(ContextConfiguration.class);
    }
}
