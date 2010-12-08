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
package org.unitils.util;

import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestMethodFinder {


    public static Method findCurrentTestMethod(Class<?> testClass) {
        String testClassName = testClass.getName();

        StackTraceElement[] currentStackTrace = Thread.currentThread().getStackTrace();
        for (int i = currentStackTrace.length - 1; i >= 0; i--) {
            String className = currentStackTrace[i].getClassName();
            if (testClassName.equals(className)) {
                String methodName = currentStackTrace[i].getMethodName();
                try {
                    return testClass.getDeclaredMethod(methodName);
                } catch (NoSuchMethodException e) {
                    throw new UnitilsException("Unable to get current test method for test " + testClass, e);
                }
            }
        }
        throw new UnitilsException("Unable to get current test method for test " + testClass + ". Test not found on current thread.");
    }
}
