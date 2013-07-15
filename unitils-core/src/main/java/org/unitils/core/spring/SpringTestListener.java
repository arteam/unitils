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

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListeners;
import org.unitils.core.TestInstance;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.core.reflect.ClassWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Note: after test class is never called
 * todo td should we implement this?
 *
 * @author Tim Ducheyne
 */
public class SpringTestListener extends TestListener {

    protected SpringTestManager springTestManager;

    protected boolean currentTestIsSpringTest;
    protected AccessibleTestContextManager testContextManager;
    protected Object previousTestObject;


    public SpringTestListener(SpringTestManager springTestManager) {
        this.springTestManager = springTestManager;
    }


    @Override
    public void beforeTestClass(ClassWrapper classWrapper) {
        springTestManager.reset();
        currentTestIsSpringTest = isSpringTest(classWrapper);

        if (!currentTestIsSpringTest) {
            // ignore, this is not a spring test
            return;
        }
        Class<?> testClass = classWrapper.getWrappedClass();
        testContextManager = new AccessibleTestContextManager(testClass);
        springTestManager.setSpringTestContext(testContextManager.getContext());

        invokeBeforeTestClass(testClass);
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        if (!currentTestIsSpringTest) {
            // ignore, this is not a spring test
            return;
        }
        Object testObject = testInstance.getTestObject();
        Method testMethod = testInstance.getTestMethod();

        invokePrepareTestInstanceIfNewTestInstance(testObject);
        invokeBeforeTestMethod(testObject, testMethod);
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance, Throwable testThrowable) {
        if (!currentTestIsSpringTest) {
            // ignore, this is not a spring test
            return;
        }
        Object testObject = testInstance.getTestObject();
        Method testMethod = testInstance.getTestMethod();
        invokeAfterTestMethod(testObject, testMethod, testThrowable);
    }


    protected boolean isSpringTest(ClassWrapper classWrapper) {
        return classWrapper.hasAnnotation(TestExecutionListeners.class) || classWrapper.hasAnnotation(ContextConfiguration.class);
    }


    protected void invokeBeforeTestClass(Class<?> testClass) {
        try {
            Method beforeTestClassMethod = TestContextManager.class.getMethod("beforeTestClass");
            beforeTestClassMethod.invoke(testContextManager);

        } catch (NoSuchMethodException e) {
            // ignore, in spring 2.5 there is no beforeTestClass method
        } catch (Exception e) {
            Throwable cause = e;
            if (e instanceof InvocationTargetException) {
                cause = e.getCause();
            }
            throw new UnitilsException("Exception occurred during before test class.", cause);
        }
    }

    protected void invokePrepareTestInstanceIfNewTestInstance(Object testObject) {
        if (testObject == previousTestObject) {
            return;
        }
        try {
            testContextManager.prepareTestInstance(testObject);
            previousTestObject = testObject;

        } catch (Exception e) {
            throw new UnitilsException("Exception occurred during prepare test instance.", e);
        }
    }

    protected void invokeBeforeTestMethod(Object testObject, Method testMethod) {
        try {
            testContextManager.beforeTestMethod(testObject, testMethod);

        } catch (Exception e) {
            throw new UnitilsException("Exception occurred during before test method.", e);
        }
    }

    protected void invokeAfterTestMethod(Object testObject, Method testMethod, Throwable testThrowable) {
        try {
            testContextManager.afterTestMethod(testObject, testMethod, testThrowable);

        } catch (Exception e) {
            throw new UnitilsException("Exception occurred during after test method.", e);
        }
    }


    protected static class AccessibleTestContextManager extends TestContextManager {

        public AccessibleTestContextManager(Class<?> testClass) {
            super(testClass);
        }

        public TestContext getContext() {
            return getTestContext();
        }
    }

}
