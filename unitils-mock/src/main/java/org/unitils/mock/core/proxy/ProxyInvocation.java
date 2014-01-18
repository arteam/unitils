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
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * An invocation of a proxy method.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ProxyInvocation {

    /* The identifier of the proxy */
    protected String proxyId;
    /* The display name of the proxy */
    protected String proxyName;
    /* The proxy on which the method was called */
    protected Object proxy;
    /* The method that was called */
    protected Method method;
    /* The arguments that were used */
    protected List<Argument<?>> arguments;
    /* The trace of the invocation */
    protected StackTraceElement[] invokedAtTrace;


    /**
     * Creates an invocation.
     *
     * @param proxyId        The identifier of the proxy, not null
     * @param proxyName      The display name of the proxy, not null
     * @param proxy          The proxy on which the method was called, not null
     * @param method         The method that was called, not null
     * @param arguments      The arguments that were used (pass by reference), not null
     * @param invokedAtTrace The trace of the invocation, not null
     */
    public ProxyInvocation(String proxyId, String proxyName, Object proxy, Method method, List<Argument<?>> arguments, StackTraceElement[] invokedAtTrace) {
        this.proxyId = proxyId;
        this.proxyName = proxyName;
        this.proxy = proxy;
        this.method = method;
        this.arguments = arguments;
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
        this.proxyId = proxyInvocation.proxyId;
        this.proxyName = proxyInvocation.proxyName;
        this.proxy = proxyInvocation.proxy;
        this.method = proxyInvocation.method;
        this.arguments = proxyInvocation.arguments;
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
     * @return The nr of arguments that are not null
     */
    public int getNrOfNotNullArguments() {
        if (arguments == null) {
            return 0;
        }
        int count = 0;
        for (Argument<?> argument : arguments) {
            if (argument.getValue() != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return True if the invoked method is an abstract method or a method of an interface
     */
    public boolean isAbstractMethod() {
        return isAbstract(method.getModifiers());
    }

    /**
     * @return True if the method does not have a return value
     */
    public boolean isVoidMethod() {
        return method.getReturnType() == Void.TYPE;
    }

    /**
     * @return True if the method is the toString() method
     */
    public boolean isToStringMethod() {
        return "toString".equals(method.getName()) && 0 == method.getParameterTypes().length;
    }

    /**
     * @return True if the method is the equals() method
     */
    public boolean isEqualsMethod() {
        return "equals".equals(method.getName()) && 1 == method.getParameterTypes().length && Object.class.equals(method.getParameterTypes()[0]);
    }

    /**
     * @return True if the method is the hashCode() method
     */
    public boolean isHashCodeMethod() {
        return "hashCode".equals(method.getName()) && 0 == method.getParameterTypes().length;
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

    /**
     * @return The id of the proxy on which the method was called, not null
     */
    public String getProxyId() {
        return proxyId;
    }

    /**
     * @return The name of the proxy on which the method was called, not null
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
    public List<Argument<?>> getArguments() {
        return arguments;
    }

    public List<?> getArgumentValues() {
        List<Object> argumentValues = new ArrayList<Object>(arguments.size());
        for (Argument<?> argument : arguments) {
            Object argumentValue = argument.getValue();
            argumentValues.add(argumentValue);
        }
        return argumentValues;
    }

    /**
     * @return The trace of the invocation, not null
     */
    public StackTraceElement[] getInvokedAtTrace() {
        return invokedAtTrace;
    }
}
