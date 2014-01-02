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

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.util.CloneService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isAbstract;
import static org.unitils.util.MethodUtils.*;

/**
 * A cglib method interceptor that will delegate the invocations to the given invocation handler.
 *
 * @author Tim Ducheyne
 */
public class CglibProxyMethodInterceptor<T> implements MethodInterceptor {

    protected String proxyName;
    protected Class<T> proxiedType;
    protected ProxyInvocationHandler proxyInvocationHandler;

    protected ProxyService proxyService;
    protected CloneService cloneService;


    /**
     * Creates an interceptor.
     *
     * @param proxyName              The display name of the proxy, not null
     * @param proxiedType            The proxied type, not null
     * @param proxyInvocationHandler The handler to delegate the invocations to, not null
     */
    public CglibProxyMethodInterceptor(String proxyName, Class<T> proxiedType, ProxyInvocationHandler proxyInvocationHandler, ProxyService proxyService, CloneService cloneService) {
        this.proxyName = proxyName;
        this.proxiedType = proxiedType;
        this.proxyInvocationHandler = proxyInvocationHandler;
        this.proxyService = proxyService;
        this.cloneService = cloneService;
    }


    /**
     * @return The proxied type, not null
     */
    public Class<?> getProxiedType() {
        return proxiedType;
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
        } else if (isFormatObjectMethod(method)) {
            return "Proxy<" + proxyName + ">";
        }

        Class<?>[] argumentTypes = method.getParameterTypes();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement[] proxiedMethodStackTrace = proxyService.getProxiedMethodStackTrace(stackTrace);
        List<Argument<?>> argumentsList = getArguments(arguments, argumentTypes);

        ProxyInvocation invocation = new CglibProxyInvocation(proxyName, method, argumentsList, proxiedMethodStackTrace, proxy, methodProxy);
        Object result = proxyInvocationHandler.handleInvocation(invocation);
        if (result == null && isToStringMethod(method)) {
            return getProxiedType().getName() + "@" + Integer.toHexString(super.hashCode());
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    protected List<Argument<?>> getArguments(Object[] values, Class<?>[] types) {
        int nrOfValues = values.length;
        List<Argument<?>> arguments = new ArrayList<Argument<?>>(nrOfValues);
        for (int i = 0; i < nrOfValues; i++) {
            Object value = values[i];
            Object clonedValue = cloneService.createDeepClone(value);
            Class<?> type = types[i];

            Argument<?> argument = new Argument(value, clonedValue, type);
            arguments.add(argument);
        }
        return arguments;
    }

    /**
     * An invocation implementation that uses the cglib method proxy to be able to invoke the original behavior.
     */
    protected static class CglibProxyInvocation extends ProxyInvocation {

        /* The cglib method proxy */
        protected MethodProxy methodProxy;

        /**
         * Creates an invocation.
         *
         * @param proxyName   The display name of the proxy, not null
         * @param method      The method that was called, not null
         * @param arguments   The arguments that were used, not null
         * @param invokedAt   The location of the invocation, not null
         * @param proxy       The proxy, not null
         * @param methodProxy The cglib method proxy, not null
         */
        public CglibProxyInvocation(String proxyName, Method method, List<Argument<?>> arguments, StackTraceElement[] invokedAt, Object proxy, MethodProxy methodProxy) {
            super(proxyName, proxy, method, arguments, invokedAt);
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
            Method method = getMethod();
            if (isAbstract(method.getModifiers())) {
                throw new UnitilsException("Cannot invoke original behavior of an abstract method. Method: " + getMethod());
            }
            List<?> argumentValues = getArgumentValues();
            return methodProxy.invokeSuper(getProxy(), argumentValues.toArray());
        }
    }
}
