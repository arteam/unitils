/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core;

import org.unitils.core.UnitilsException;
import org.unitils.mock.proxy.ProxyInvocationHandler;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SyntaxMonitor {


    protected String mockObjectName;

    protected String definingMethodName;

    protected ProxyInvocationHandler activeProxyInvocationHandler;

    protected StackTraceElement[] invokedAt;


    public void startDefinition(String mockObjectName, String methodName, ProxyInvocationHandler proxyInvocationHandler, StackTraceElement[] invokedAt) {
        assertNotExpectingInvocation();
        this.activeProxyInvocationHandler = proxyInvocationHandler;
        this.mockObjectName = mockObjectName;
        this.definingMethodName = methodName;
        this.invokedAt = invokedAt;
    }


    public void endDefinition() {
        this.activeProxyInvocationHandler = null;
        this.mockObjectName = null;
        this.definingMethodName = null;
        this.invokedAt = null;
    }


    public void assertNotExpectingInvocation() {
        if (activeProxyInvocationHandler != null) {
            UnitilsException exception = new UnitilsException("Invalid syntax. " + mockObjectName + "." + definingMethodName + "() must be followed by a method invocation on the returned proxy. E.g. " + mockObjectName + "." + definingMethodName + "().myMethod();");
            exception.setStackTrace(invokedAt);
            throw exception;
        }
    }


}
