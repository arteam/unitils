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

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Factory;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.MockObject;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProxyUtils {


    /**
     * @param object The object to check
     * @return The proxied type, null if the object is not a proxy
     */
    public static Class<?> getProxiedTypeIfProxy(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Factory) {
            Callback[] callbacks = ((Factory) object).getCallbacks();
            if (callbacks == null || callbacks.length == 0) {
                return null;
            }
            if (callbacks[0] instanceof CglibProxyMethodInterceptor) {
                return ((CglibProxyMethodInterceptor) callbacks[0]).getProxiedType();
            }
        }
        return null;
    }

    /**
     * @param instance The instance to check, not null
     * @return True if the given instance is a jdk or cglib proxy
     */
    public static boolean isProxy(Object instance) {
        if (instance == null) {
            return false;
        }
        Class<?> clazz = instance.getClass();
        return isProxyClassName(clazz.getName()) || Proxy.isProxyClass(clazz);
    }

    /**
     * @param className The class name to check, not null
     * @return True if the given class name is cglib proxy class name
     */
    public static boolean isProxyClassName(String className) {
        return className.contains("$$EnhancerByCGLIB$$");
    }


    /**
     * note: don't remove, used through reflection from {@link org.unitils.core.util.ObjectFormatter}
     *
     * @param object The object to check
     * @return The proxied type, null if the object is not a proxy or mock
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static String getMockName(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof MockObject) {
            return ((MockObject) object).getName();
        }
        if (object instanceof Factory) {
            Callback callback = ((Factory) object).getCallback(0);
            if (callback instanceof CglibProxyMethodInterceptor) {
                return ((CglibProxyMethodInterceptor) callback).getMockName();
            }
        }
        return null;
    }


    /**
     * First finds a trace element in which a cglib proxy method was invoked. Then it returns the rest of the stack trace following that
     * element. The stack trace starts with the element rh  r is the method call that was proxied by the proxy method.
     *
     * @return The proxied method trace, not null
     */
    public static StackTraceElement[] getProxiedMethodStackTrace() {
        List<StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();

        boolean foundProxyMethod = false;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (foundProxyMethod) {
                stackTrace.add(stackTraceElement);

            } else if (isProxyClassName(stackTraceElement.getClassName())) {
                // found the proxy method element, the next element is the proxied method element
                foundProxyMethod = true;
            }
        }
        if (stackTrace.isEmpty()) {
            throw new UnitilsException("No invocation of a cglib proxy method found in stacktrace: " + Arrays.toString(stackTraceElements));
        }
        return stackTrace.toArray(new StackTraceElement[stackTrace.size()]);
    }
}
