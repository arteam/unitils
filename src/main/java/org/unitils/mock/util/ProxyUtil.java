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
package org.unitils.mock.util;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProxyUtil {


    /**
     * Creates a proxy object for the given <code>Class</code>.
     * A <code>MethodInterceptor</code> must be passed to hook into the proxy.
     *
     * @param interceptor     The interceptor to hook into the proxy, not null.
     * @param targetClass     The class of the proxy to be created, not null.
     * @param extraInterfaces The extra interfaces that the proxy should implement.
     * @return The proxy object, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(MethodInterceptor interceptor, Class<T> targetClass, Class<?>... extraInterfaces) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setInterfaces(extraInterfaces);
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        Class<T> enhancedTargetClass = enhancer.createClass();

        Objenesis objenesis = new ObjenesisStd();
        Factory proxy = (Factory) objenesis.newInstance(enhancedTargetClass);
        proxy.setCallbacks(new Callback[]{interceptor});
        return (T) proxy;
    }


    /**
     * Gets the method of the proxied class that was replaced by the given proxy method.
     * An exception is thrown when the method is not found.
     *
     * @param proxyMethod  The proxy method for which to find the actual method, not null
     * @param proxiedClass The class that was proxied, not null
     * @return The method of the proxied class, not null
     */
    public static Method getOriginalMethod(Method proxyMethod, Class<?> proxiedClass) {
        try {
            return proxiedClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Unable to find the original method in class " + proxiedClass + " for following proxy method: " + proxyMethod, e);
        }
    }


    /**
     * First finds a trace element in which a cglib proxy method was invoked. Then it returns the following stack trace
     * element. This element is the method call that was proxied by the proxy method.
     *
     * @param stackTraceElements The stack trace, not null
     * @return The proxied method trace element, not null
     */
    public static StackTraceElement getProxiedMethodStackTraceElement(StackTraceElement[] stackTraceElements) {
        boolean foundProxyMethod = false;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (foundProxyMethod) {
                // found the proxy method element, the next element is the proxied method element
                return stackTraceElement;
            }
            if (stackTraceElement.getClassName().contains("$$EnhancerByCGLIB$$")) {
                foundProxyMethod = true;
            }
        }
        throw new UnitilsException("No invocation of a cglib proxy method found in stacktrace: " + Arrays.toString(stackTraceElements));
    }
}
