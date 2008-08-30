/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock.proxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * todo move
 * <p/>
 * Helper class to format the methods and their corresponding params in the output.
 * This probably needs to be (re)moved; it was just made as a single point of reference for all method-related output.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MethodFormatUtil {

    public static String getMethodNameWithParams(Method method, List<?> params) {
        StringBuffer outputString = new StringBuffer();
        outputString.append(method.getName());
        outputString.append("()");
        return outputString.toString();
    }


    public static String getCompleteMethodRepresentation(Method method, List<?> params) {
        StringBuffer outputString = new StringBuffer();
        outputString.append(getClassName(method));
        outputString.append('.');
        outputString.append(getMethodNameWithParams(method, params));
        return outputString.toString();
    }


    public static String getClassName(Method method) {
        return method.getDeclaringClass().getSimpleName();
    }


    public static String getMethodNameWithParams(Method method) {
        return getMethodNameWithParams(method, null);
    }


    public static String getCompleteRepresentation(Method method) {
        return getCompleteMethodRepresentation(method, null);
    }


}
