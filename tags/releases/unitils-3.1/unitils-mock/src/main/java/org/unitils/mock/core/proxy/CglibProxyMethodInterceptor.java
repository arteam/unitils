/*
 * Copyright 2006-2009,  Unitils.org
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

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.unitils.core.UnitilsException;
import static org.unitils.mock.core.proxy.ProxyUtils.getProxiedMethodStackTrace;
import static org.unitils.util.MethodUtils.*;

import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * A cglib method intercepter that will delegate the invocations to the given invocation hanlder.
 */
public class CglibProxyMethodInterceptor<T> implements MethodInterceptor {

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
    public CglibProxyMethodInterceptor(String mockName, Class<T> proxiedType, ProxyInvocationHandler invocationHandler) {
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


    public String getMockName() {
        return mockName;
    }

    /**
     * @return The proxied type, not null
     */
    public Class<?> getProxiedType() {
        return proxiedType;
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
