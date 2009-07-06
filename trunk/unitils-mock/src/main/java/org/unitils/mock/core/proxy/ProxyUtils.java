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
package org.unitils.mock.core.proxy;

import net.sf.cglib.proxy.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;
import static org.unitils.util.MethodUtils.*;

import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isAbstract;
import java.util.ArrayList;
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
public class ProxyUtils {


    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param mockName          The name of the mock, not null
     * @param proxiedClass      The type to proxy, not null
     * @param invocationHandler The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    public static <T> T createProxy(String mockName, Class<T> proxiedClass, ProxyInvocationHandler invocationHandler) {
        return createProxy(mockName, proxiedClass, new Class<?>[0], invocationHandler);
    }


    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param mockName              The name of the mock, not null
     * @param proxiedClass          The type to proxy, not null
     * @param implementedInterfaces Additional interfaces that the proxy must implement, not null
     * @param invocationHandler     The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(String mockName, Class<T> proxiedClass, Class<?>[] implementedInterfaces, ProxyInvocationHandler invocationHandler) {
        Enhancer enhancer = new Enhancer();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        if (proxiedClass.isInterface()) {
            enhancer.setSuperclass(Object.class);
            interfaces.add(proxiedClass);
        } else {
            enhancer.setSuperclass(proxiedClass);
        }
        if (implementedInterfaces != null && implementedInterfaces.length > 0) {
            interfaces.addAll(asList(implementedInterfaces));
        }
        if (!interfaces.isEmpty()) {
            enhancer.setInterfaces(interfaces.toArray(new Class<?>[interfaces.size()]));
        }
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        Class<T> enhancedTargetClass = enhancer.createClass();

        Factory proxy = (Factory) createInstanceOfType(enhancedTargetClass);
        proxy.setCallbacks(new Callback[]{new ProxyMethodInterceptor(mockName, proxiedClass, invocationHandler)});
        return (T) proxy;
    }


    /**
     * @param object The object to check
     * @return The proxied type, null if the object is not a proxy
     */
    public static Class<?> getProxiedTypeIfProxy(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> type = object.getClass();
        if (object instanceof Factory) {
            Callback callback = ((Factory) object).getCallback(0);
            if (callback instanceof ProxyMethodInterceptor) {
                return ((ProxyMethodInterceptor) callback).getProxiedType();
            }
        }
        return null;
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


    public static boolean isProxyClassName(String className) {
        return className.contains("$$EnhancerByCGLIB$$");
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


    /**
     * A cglib method intercepter that will delegate the invocations to the given invocation hanlder.
     */
    public static class ProxyMethodInterceptor<T> implements MethodInterceptor {

        private String mockName;

        private Class<T> proxiedType;

        /* The invocation handler */
        private ProxyInvocationHandler invocationHandler;


        /**
         * Creates an interceptor.
         *
         * @param mockName          The name of the mock, not null
         * @param proxiedType       The proxied type, not null
         * @param invocationHandler The handler to delegate the invocations to, not null
         */
        public ProxyMethodInterceptor(String mockName, Class<T> proxiedType, ProxyInvocationHandler invocationHandler) {
            this.mockName = mockName;
            this.proxiedType = proxiedType;
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
            if (isFinalizeMethod(method)) {
                return null;
            } else if (isEqualsMethod(method)) {
                return proxy == arguments[0];
            } else if (isHashCodeMethod(method)) {
                return super.hashCode();
            } else if (isCloneMethod(method)) {
                return proxy;
            } else if (isToStringMethod(method)) {
                return getProxiedType().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
            }

            ProxyInvocation invocation = new CglibProxyInvocation(mockName, method, asList(arguments), getProxiedMethodStackTrace(), proxy, methodProxy);
            return invocationHandler.handleInvocation(invocation);
        }

        /**
         * @return The proxied type, not null
         */
        public Class<?> getProxiedType() {
            return proxiedType;
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
         * @param mockName    The name of the mock, not null
         * @param method      The method that was called, not null
         * @param arguments   The arguments that were used, not null
         * @param invokedAt   The location of the invocation, not null
         * @param proxy       The proxy, not null
         * @param methodProxy The cglib method proxy, not null
         */
        public CglibProxyInvocation(String mockName, Method method, List<Object> arguments, StackTraceElement[] invokedAt, Object proxy, MethodProxy methodProxy) {
            super(mockName, proxy, method, arguments, invokedAt);
            this.methodProxy = methodProxy;
        }


        /**
         * Invokes the original behavior by calling the method proxy.
         * If there is no original behavior, e.g. an interface or abstract method, an exception is raised.
         *
         * @return The result value
         */
        @Override
        public Object invokeOriginalBehavior() throws Throwable {
            if (isAbstract(getMethod().getModifiers())) {
                throw new UnitilsException("Unable to invoke original behavior. The method is abstract, it does not have any behavior defined: " + getMethod());
            }
            return methodProxy.invokeSuper(getProxy(), getArguments().toArray());
        }
    }
}
