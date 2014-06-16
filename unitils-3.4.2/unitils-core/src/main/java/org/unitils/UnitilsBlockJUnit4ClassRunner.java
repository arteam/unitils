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
package org.unitils;

import java.lang.reflect.Method;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.junit.AfterTestMethodStatement;
import org.unitils.core.junit.AfterTestTearDownStatement;
import org.unitils.core.junit.BeforeTestClassStatement;
import org.unitils.core.junit.BeforeTestMethodStatement;
import org.unitils.core.junit.BeforeTestSetUpStatement;

/**
 * @author Tim Ducheyne
 */
public class UnitilsBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    protected Object test;
    protected TestListener unitilsTestListener;


    public UnitilsBlockJUnit4ClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.unitilsTestListener = getUnitilsTestListener();
    }


    @Override
    protected Statement classBlock(RunNotifier notifier) {
        Class<?> testClass = getTestClass().getJavaClass();

        Statement statement = super.classBlock(notifier);
        statement = new BeforeTestClassStatement(testClass, unitilsTestListener, statement);
        //statement = new AfterTestClassStatement(unitilsTestListener, statement);
        return statement;
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        this.test = test;

        Statement statement = super.methodInvoker(method, test);
        statement = new BeforeTestMethodStatement(unitilsTestListener, statement, method.getMethod(), test);
        statement = new AfterTestMethodStatement(unitilsTestListener, statement, method.getMethod(), test);
        return statement;
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Method testMethod = method.getMethod();

        Statement statement = super.methodBlock(method);
        statement = new BeforeTestSetUpStatement(test, testMethod, unitilsTestListener, statement);
        statement = new AfterTestTearDownStatement(unitilsTestListener, statement, test, testMethod);
        return statement;
    }


    protected TestListener getUnitilsTestListener() {
        return Unitils.getInstance().getTestListener();
    }
}

