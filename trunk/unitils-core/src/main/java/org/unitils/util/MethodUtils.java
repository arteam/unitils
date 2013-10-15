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
package org.unitils.util;

import java.lang.reflect.Method;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MethodUtils {

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#equals} method
     */
    public static boolean isEqualsMethod(Method method) {
        return "equals".equals(method.getName()) && 1 == method.getParameterTypes().length && Object.class.equals(method.getParameterTypes()[0]);
    }

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#hashCode} method
     */
    public static boolean isHashCodeMethod(Method method) {
        return "hashCode".equals(method.getName()) && 0 == method.getParameterTypes().length;
    }

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#toString} method
     */
    public static boolean isToStringMethod(Method method) {
        return "toString".equals(method.getName()) && 0 == method.getParameterTypes().length;
    }

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#clone} method
     */
    public static boolean isCloneMethod(Method method) {
        return "clone".equals(method.getName()) && 0 == method.getParameterTypes().length;
    }

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#finalize} method
     */
    public static boolean isFinalizeMethod(Method method) {
        return "finalize".equals(method.getName()) && 0 == method.getParameterTypes().length;
    }

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link org.unitils.core.util.ObjectToFormat#$formatObject()} method
     */
    public static boolean isFormatObjectMethod(Method method) {
        return "$formatObject".equals(method.getName()) && 0 == method.getParameterTypes().length;
    }
}
