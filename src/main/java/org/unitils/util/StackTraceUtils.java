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

import org.unitils.core.UnitilsException;
import static org.unitils.util.ReflectionUtils.getClassWithName;

/**
 * Class offering utilities involving the call stack
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class StackTraceUtils {


    /**
     * @param invokedClass The class for which an invocation can be found in the current call stack
     * @return the line nr of the invocation in that class, an exception if not found
     */
    public static int getInvocationLineNr(Class<?> invokedClass) {
        StackTraceElement[] invocationStackTrace = getInvocationStackTrace(invokedClass);
        return invocationStackTrace[0].getLineNumber();
    }


    /**
     * @param invokedInterface Class/interface to which an invocation can be found in the current call stack
     * @return Stacktrace that indicates the most recent method call in the stack that calls a method from the given class
     */
    public static StackTraceElement[] getInvocationStackTrace(Class<?> invokedInterface) {
        StackTraceElement[] currentStackTrace = Thread.currentThread().getStackTrace();
        for (int i = currentStackTrace.length - 1; i >= 0; i--) {
            String className = currentStackTrace[i].getClassName();
            Class<?> clazz = getClassWithName(className);
            if (invokedInterface.isAssignableFrom(clazz) || className.contains("$$")) {
                StackTraceElement[] result = new StackTraceElement[currentStackTrace.length - i];
                System.arraycopy(currentStackTrace, i, result, 0, currentStackTrace.length - i);
                return result;
            }
        }
        throw new UnitilsException("No invocation of a method of " + invokedInterface.getName() + " found in the current stacktrace");
    }

}
