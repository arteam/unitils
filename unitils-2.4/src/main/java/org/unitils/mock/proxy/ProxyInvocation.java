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
 * An invocation of a proxy method.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public abstract class ProxyInvocation {

    /* The proxy on which the method was called */
    private Object proxy;

    /* The method that was called */
    private Method method;

    /* The arguments that were used */
    private List<Object> arguments;

    /* The arguments at the time that they were used */
    private List<Object> argumentsAtInvocationTime;

    /* The location of the invocation */
    private StackTraceElement invokedAt;


    /**
     * Creates an invocation.
     *
     * @param proxy     The proxy on which the method was called, not null
     * @param method    The method that was called, not null
     * @param arguments The arguments that were used, not null
     * @param invokedAt The location of the invocation, not null
     */
    public ProxyInvocation(Object proxy, Method method, List<Object> arguments, StackTraceElement invokedAt) {
        this(proxy, method, arguments, arguments, invokedAt);
    }


    /**
     * Creates an invocation.
     *
     * The argumentsAtInvocationTime can be set as copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference). If not explicitly set, this will return the
     * same values as the arguments.
     *
     * @param proxy                     The proxy on which the method was called, not null
     * @param method                    The method that was called, not null
     * @param arguments                 The arguments that were used by reference, not null
     * @param argumentsAtInvocationTime The arguments at the time that they were used, not null
     * @param invokedAt                 The location of the invocation, not null
     */
    public ProxyInvocation(Object proxy, Method method, List<Object> arguments, List<Object> argumentsAtInvocationTime, StackTraceElement invokedAt) {
        this.proxy = proxy;
        this.method = method;
        this.arguments = arguments;
        this.argumentsAtInvocationTime = argumentsAtInvocationTime;
        this.invokedAt = invokedAt;
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
     * @return The proxy on which the method was called
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
    public List<Object> getArguments() {
        return arguments;
    }


    /**
     * The arguments at the time that they were used.
     *
     * The argumentsAtInvocationTime can be set as copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference). If not explicitly set, this will return the
     * same values as the arguments.
     *
     * @return The arguments, not null
     */
    public List<Object> getArgumentsAtInvocationTime() {
        return argumentsAtInvocationTime;
    }


    /**
     * @return The location of the invocation, not null
     */
    public StackTraceElement getInvokedAt() {
        return invokedAt;
    }

}
