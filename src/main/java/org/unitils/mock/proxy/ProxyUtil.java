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

import net.sf.cglib.proxy.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProxyUtil {


    /**
     * Creates a proxy object for the given mock object.
     * A invocation handler must be passed to hook into the proxy.
     *
     * @param mockedClass       The type to proxy, not null
     * @param invocationHandler The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> mockedClass, ProxyInvocationHandler invocationHandler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(mockedClass);
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        Class<T> enhancedTargetClass = enhancer.createClass();

        Objenesis objenesis = new ObjenesisStd();
        Factory proxy = (Factory) objenesis.newInstance(enhancedTargetClass);
        proxy.setCallbacks(new Callback[]{new ProxyMethodInterceptor(invocationHandler)});
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


    public static class ProxyMethodInterceptor implements MethodInterceptor {

        private ProxyInvocationHandler invocationHandler;


        public ProxyMethodInterceptor(ProxyInvocationHandler invocationHandler) {
            this.invocationHandler = invocationHandler;
        }


        public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            StackTraceElement invokedAt = getProxiedMethodStackTraceElement(Thread.currentThread().getStackTrace());
            ProxyInvocation invocation = new ProxyMethodInvocation(method, asList(arguments), invokedAt, proxy, methodProxy);
            return invocationHandler.handleInvocation(invocation);
        }
    }


    public static class ProxyMethodInvocation extends ProxyInvocation {

        private Object proxy;

        private MethodProxy methodProxy;


        public ProxyMethodInvocation(Method method, List<?> arguments, StackTraceElement invokedAt, Object proxy, MethodProxy methodProxy) {
            super(method, arguments, invokedAt);
            this.proxy = proxy;
            this.methodProxy = methodProxy;
        }


        @Override
        public Object invokeOriginalBehavior() throws Throwable {
            return methodProxy.invokeSuper(proxy, getArguments().toArray());
        }
    }
}
