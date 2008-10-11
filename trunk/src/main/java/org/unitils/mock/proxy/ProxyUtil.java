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
     * @param proxiedClass      The type to proxy, not null
     * @param invocationHandler The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    public static <T> T createProxy(Class<T> proxiedClass, ProxyInvocationHandler invocationHandler) {
        return createProxy(proxiedClass, new Class<?>[0], invocationHandler);
    }
    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param proxiedClass       The type to proxy, not null
     * @param implementedInterfaces Additional interfaces that the proxy must implement, not null
     * @param invocationHandler The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> proxiedClass, Class<?>[] implementedInterfaces, ProxyInvocationHandler invocationHandler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxiedClass);
        if (implementedInterfaces.length != 0) {
            enhancer.setInterfaces(implementedInterfaces);
        }
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        Class<T> enhancedTargetClass = enhancer.createClass();
        
        Factory proxy = (Factory) createInstanceOfType(enhancedTargetClass);
        proxy.setCallbacks(new Callback[]{new ProxyMethodInterceptor(invocationHandler)});
        return (T) proxy;
    }


    /**
     * Creates an instance of the given type using objenesis. The class doesn't have to offer
     * an empty constructor in order for this method to succeed.
     *
     * @param <T>   The type of the instance
     * @param clazz The class for which an instance is requested
     * @return An instance of the given class
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstanceOfType(Class<T> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        return (T) objenesis.newInstance(clazz);
    }


    /**
     * First finds a trace element in which a cglib proxy method was invoked. Then it returns the following stack trace
     * element. This element is the method call that was proxied by the proxy method.
     *
     * @param stackTraceElements The stack trace, not null
     * @param failWhenNoProxyFound 
     * @return The proxied method trace element, not null
     */
    public static StackTraceElement getProxiedMethodStackTraceElement(StackTraceElement[] stackTraceElements, boolean failWhenNoProxyFound) {
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
            StackTraceElement invokedAt = getProxiedMethodStackTraceElement(Thread.currentThread().getStackTrace(), true);
            ProxyInvocation invocation = new CglibProxyInvocation(method, asList(arguments), invokedAt, proxy, methodProxy);
            return invocationHandler.handleInvocation(invocation);
        }
    }


    /**
     * An invocation implementation that uses the cglib method proxy to be able to invoke the original behavior.
     */
    public static class CglibProxyInvocation extends ProxyInvocation {

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
        public CglibProxyInvocation(Method method, List<Object> arguments, StackTraceElement invokedAt, Object proxy, MethodProxy methodProxy) {
            super(proxy, method, arguments, invokedAt);
            this.methodProxy = methodProxy;
        }


        /**
         * Invokes the original behavior by calling the method proxy.
         *
         * @return The result value
         */
        @Override
        public Object invokeOriginalBehavior() throws Throwable {
            return methodProxy.invokeSuper(getProxy(), getArguments().toArray());
        }
    }
}
