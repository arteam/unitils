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

import java.lang.reflect.Method;
import java.util.List;

/**
 * An invocation of a proxy method.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ProxyInvocation {

    /* The display name of the proxy */
    protected String proxyName;
    /* The proxy on which the method was called */
    protected Object proxy;
    /* The method that was called */
    protected Method method;
    /* The arguments that were used */
    protected List<?> arguments;
    /* The arguments at the time that they were used */
    protected List<?> argumentsAtInvocationTime;
    /* The trace of the invocation */
    protected StackTraceElement[] invokedAtTrace;


    /**
     * Creates an invocation.
     *
     * @param proxyName                 The display name of the proxy, not null
     * @param proxy                     The proxy on which the method was called, not null
     * @param method                    The method that was called, not null
     * @param arguments                 The arguments that were used (pass by reference), not null
     * @param argumentsAtInvocationTime A copy of the values at the time of invocation (pass by value), not null
     * @param invokedAtTrace            The trace of the invocation, not null
     */
    public ProxyInvocation(String proxyName, Object proxy, Method method, List<?> arguments, List<?> argumentsAtInvocationTime, StackTraceElement[] invokedAtTrace) {
        this.proxyName = proxyName;
        this.proxy = proxy;
        this.method = method;
        this.arguments = arguments;
        this.argumentsAtInvocationTime = argumentsAtInvocationTime;
        this.invokedAtTrace = invokedAtTrace;
    }

    /**
     * Creates a copy of the given proxy invocation.
     * <p/>
     * The argumentsAtInvocationTime will be set as copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference). If not explicitly set, this will return the
     * same values as the arguments.
     *
     * @param proxyInvocation The proxy invocation to copy, not null
     */
    public ProxyInvocation(ProxyInvocation proxyInvocation) {
        this.proxyName = proxyInvocation.proxyName;
        this.proxy = proxyInvocation.proxy;
        this.method = proxyInvocation.method;
        this.arguments = proxyInvocation.arguments;
        this.argumentsAtInvocationTime = proxyInvocation.argumentsAtInvocationTime;
        this.invokedAtTrace = proxyInvocation.invokedAtTrace;
    }


    /**
     * Calls the actual method that was proxied using the same arguments.
     *
     * @return The result value
     */
    public Object invokeOriginalBehavior() throws Throwable {
        throw new UnsupportedOperationException("Invoking of original behavior not implemented.");
    }

    /**
     * @return The nr of arguments at invocation time that were not null
     */
    public int getNrOfNotNullArguments() {
        if (argumentsAtInvocationTime == null) {
            return 0;
        }
        int count = 0;
        for (Object argument : argumentsAtInvocationTime) {
            if (argument != null) {
                count++;
            }
        }
        return count;
    }


    /**
     * @return The proxy on which the method was called, not null
     */
    public String getProxyName() {
        return proxyName;
    }

    /**
     * @return The proxy on which the method was called, not null
     */
    public Object getProxy() {
        return proxy;
    }

    /**
     * @return The method that was called, not null
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return The arguments that were used, not null
     */
    public List<?> getArguments() {
        return arguments;
    }

    /**
     * The arguments at the time that they were used.
     * <p/>
     * The argumentsAtInvocationTime can be set as copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference). If not explicitly set, this will return the
     * same values as the arguments.
     *
     * @return The arguments, not null
     */
    public List<?> getArgumentsAtInvocationTime() {
        return argumentsAtInvocationTime;
    }

    /**
     * @return The trace of the invocation, not null
     */
    public StackTraceElement[] getInvokedAtTrace() {
        return invokedAtTrace;
    }

    /**
     * @return The location of the invocation, not null
     */
    public StackTraceElement getInvokedAt() {
        return invokedAtTrace[0];
    }

    /**
     * @return The line nr of the invocation
     */
    public int getLineNumber() {
        return getInvokedAt().getLineNumber();
    }
}
