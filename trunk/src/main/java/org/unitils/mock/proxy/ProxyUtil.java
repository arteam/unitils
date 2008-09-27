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
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param proxiedClass       The type to proxy, not null
     * @param invocationHandler The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> proxiedClass, ProxyInvocationHandler invocationHandler) {
        return createProxy(proxiedClass, new ProxyMethodInterceptor(invocationHandler));
    }


    // todo remove let dummy use the invocation handler
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> proxiedClass, MethodInterceptor methodInterceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxiedClass);
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        Class<T> enhancedTargetClass = enhancer.createClass();

        Objenesis objenesis = new ObjenesisStd();
        Factory proxy = (Factory) objenesis.newInstance(enhancedTargetClass);
        proxy.setCallbacks(new Callback[]{methodInterceptor});
        return (T) proxy;
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


    /**
     * A cglib method intercepter that will delegate the invocations to the given invocation hanlder.
     */
    public static class ProxyMethodInterceptor implements MethodInterceptor {

        /* The invocation handler */
        private ProxyInvocationHandler invocationHandler;


        /**
         * Creates an interceptor.
         *
         * @param invocationHandler The handler to delegate the invocations to, not null
         */
        public ProxyMethodInterceptor(ProxyInvocationHandler invocationHandler) {
            this.invocationHandler = invocationHandler;
        }


        /**
         * Intercepts the method call by wrapping the invocation in a {@link CglibProxyInvocation} and delegating the
         * handling to the invocation handler.
         *
         * @param proxy       The proxy, not null
         * @param method      The method that was called, not null
         * @param arguments   The arguments that were used, not null
         * @param methodProxy The cglib method proxy, not null
         * @return The value to return for the method call, ignored for void methods
         */
        public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            StackTraceElement invokedAt = getProxiedMethodStackTraceElement(Thread.currentThread().getStackTrace());
            ProxyInvocation invocation = new CglibProxyInvocation(method, asList(arguments), invokedAt, proxy, methodProxy);
            return invocationHandler.handleInvocation(invocation);
        }
    }


    /**
     * An invocation implementation that uses the cglib method proxy to be able to invoke the original behavior.
     */
    public static class CglibProxyInvocation extends ProxyInvocation {

        /* The proxy */
        private Object proxy;

        /* The cglib method proxy */
        private MethodProxy methodProxy;


        /**
         * Creates an invocation.
         *
         * @param method      The method that was called, not null
         * @param arguments   The arguments that were used, not null
         * @param invokedAt   The location of the invocation, not null
         * @param proxy       The proxy, not null
         * @param methodProxy The cglib method proxy, not null
         */
        public CglibProxyInvocation(Method method, List<?> arguments, StackTraceElement invokedAt, Object proxy, MethodProxy methodProxy) {
            super(method, arguments, invokedAt);
            this.proxy = proxy;
            this.methodProxy = methodProxy;
        }


        /**
         * Invokes the original behavior by calling the method proxy.
         *
         * @return The result value
         */
        @Override
        public Object invokeOriginalBehavior() throws Throwable {
            return methodProxy.invokeSuper(proxy, getArguments().toArray());
        }
    }
}
