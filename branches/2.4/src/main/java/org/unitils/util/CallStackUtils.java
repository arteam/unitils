/*
 * Copyright 2006-2007,  Unitils.org
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

/**
 * Class offering utilities involving the call stack
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class CallStackUtils {


    /**
     * @param invokedClass Class to which an invocation can be found in the current call stack
     * @return Stacktrace that indicates the most recent method call in the stack that calls a method from the given class
     */
    public static StackTraceElement[] getInvocationStackTrace(Class<?> invokedClass) {
        StackTraceElement[] currentStackTrace = Thread.currentThread().getStackTrace();
        for (int i = currentStackTrace.length - 1; i >= 0; i--) {
            if (invokedClass.getName().equals(currentStackTrace[i].getClassName())) {
                int invokedAtIndex = i + 1;
                StackTraceElement[] result = new StackTraceElement[currentStackTrace.length - invokedAtIndex];
                System.arraycopy(currentStackTrace, invokedAtIndex, result, 0, currentStackTrace.length - invokedAtIndex);
                return result;
            }
        }
        throw new IllegalArgumentException("Invoked class " + invokedClass.getName() + " not found in stacktrace");
    }

}
