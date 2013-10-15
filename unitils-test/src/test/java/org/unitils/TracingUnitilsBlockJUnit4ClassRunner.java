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

import org.junit.runners.model.InitializationError;
import org.unitils.core.engine.UnitilsTestListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.unitils.TracingUnitilsBlockJUnit4ClassRunner.Invocation.*;

/**
 * @author Tim Ducheyne
 */
public class TracingUnitilsBlockJUnit4ClassRunner extends UnitilsBlockJUnit4ClassRunner {

    public static enum Invocation {
        LISTENER_BEFORE_CLASS,
        TEST_BEFORE_CLASS,
        LISTENER_BEFORE_TEST_SET_UP,
        TEST_BEFORE,
        LISTENER_BEFORE_TEST_METHOD,
        TEST_METHOD,
        LISTENER_AFTER_TEST_METHOD,
        TEST_AFTER,
        LISTENER_AFTER_TEST_TEAR_DOWN,
        TEST_AFTER_CLASS
    }


    public TracingUnitilsBlockJUnit4ClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    private static List<Invocation> listenerInvocations = new ArrayList<Invocation>();


    public static List<Invocation> getInvocations() {
        return listenerInvocations;
    }

    public static void addInvocations(Invocation invocation) {
        listenerInvocations.add(invocation);
    }


    @Override
    protected UnitilsTestListener getUnitilsTestListener() {
        listenerInvocations.clear();
        return new TracingUnitilsTestListener();
    }


    public static class TracingUnitilsTestListener extends UnitilsTestListener {

        public TracingUnitilsTestListener() {
            super(null, null, null);
        }

        @Override
        public void beforeTestClass(Class<?> testClass) {
            listenerInvocations.add(LISTENER_BEFORE_CLASS);
        }

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            listenerInvocations.add(LISTENER_BEFORE_TEST_SET_UP);
        }

        @Override
        public void beforeTestMethod() {
            listenerInvocations.add(LISTENER_BEFORE_TEST_METHOD);
        }

        @Override
        public void afterTestMethod(Throwable testThrowable) {
            listenerInvocations.add(LISTENER_AFTER_TEST_METHOD);
        }

        @Override
        public void afterTestTearDown() {
            listenerInvocations.add(LISTENER_AFTER_TEST_TEAR_DOWN);
        }
    }
}
