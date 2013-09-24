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
package org.unitils.mock.core.proxy;

import org.unitils.core.UnitilsException;

import static java.lang.System.arraycopy;
import static org.springframework.util.ClassUtils.isCglibProxyClassName;
import static org.unitils.util.ReflectionUtils.getClassWithName;

/**
 * Class offering utilities involving the call stack
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class StackTraceService {


    /**
     * @param invokedClass The class for which an invocation can be found in the current call stack
     * @return the line nr of the invocation in that class, -1 if not found
     */
    public int getInvocationLineNr(Class<?> invokedClass) {
        StackTraceElement[] invocationStackTrace = getInvocationStackTrace(invokedClass, false);
        if (invocationStackTrace == null) {
            return -1;
        }
        return invocationStackTrace[0].getLineNumber();
    }

    /**
     * @param invokedClass Class/interface to which an invocation can be found in the current call stack
     * @return Stack trace that indicates the most recent method call in the stack that calls a method from the given class, null if not found
     */
    public StackTraceElement[] getInvocationStackTrace(Class<?> invokedClass) {
        return getInvocationStackTrace(invokedClass, true);
    }

    public StackTraceElement[] getInvocationStackTrace(Class<?> invokedClass, boolean included) {
        StackTraceElement[] currentStackTrace = getCurrentStackTrace();
        for (int i = currentStackTrace.length - 1; i >= 0; i--) {
            String className = currentStackTrace[i].getClassName();
            Class<?> clazz;
            try {
                clazz = getClassWithName(className);
            } catch (UnitilsException e) {
                // unable to load class, this should never happen for the class we are looking for
                continue;
            }
            if (invokedClass.isAssignableFrom(clazz) || isCglibProxyClassName(className)) {
                int index = included ? i : i + 1;
                return getStackTraceStartingFrom(currentStackTrace, index);
            }
        }
        return null;
    }

    /**
     * Gets the sub-stack trace starting from the given index (included)
     *
     * @param stackTraceElements The start stack trace, not null
     * @param index              The index to start from
     * @return The sub stack trace, not null
     */
    public StackTraceElement[] getStackTraceStartingFrom(StackTraceElement[] stackTraceElements, int index) {
        if (index <= 0) {
            return stackTraceElements;
        }
        int length = stackTraceElements.length;
        if (index >= length) {
            return new StackTraceElement[0];
        }
        StackTraceElement[] result = new StackTraceElement[length - index];
        arraycopy(stackTraceElements, index, result, 0, length - index);
        return result;
    }


    protected StackTraceElement[] getCurrentStackTrace() {
        return Thread.currentThread().getStackTrace();
    }
}
