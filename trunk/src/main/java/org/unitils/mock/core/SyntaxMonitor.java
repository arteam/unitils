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

    protected ProxyInvocationHandler expectingInvocationOn;
    
    protected String mockObjectName;
    
    protected String precedingMethodName;
    
    protected StackTraceElement[] invokedAt;

    public void registerProxyReturningMethodCall(ProxyInvocationHandler proxyInvocationHandler, String mockObjectName, 
                String behaviorDefiningMethodName, StackTraceElement[] invokedAt) {
        assertNotExpectingInvocation();
        this.expectingInvocationOn = proxyInvocationHandler;
        this.mockObjectName = mockObjectName;
        this.precedingMethodName = behaviorDefiningMethodName;
        this.invokedAt = invokedAt;
    }
    
    
    public void registerProxyMethodCall(ProxyInvocationHandler proxyInvocationHandler) {
        if (expectingInvocationOn != proxyInvocationHandler) {
            raiseSyntaxException();
        }
        expectingInvocationOn = null;
        mockObjectName = null;
        precedingMethodName = null;
    }
    
    
    public void assertNotExpectingInvocation() {
        if (expectingInvocationOn != null) {
            raiseSyntaxException();
        }
    }


    protected void raiseSyntaxException() {
        UnitilsException exception = new UnitilsException(mockObjectName + "." + precedingMethodName 
                + " must be followed by a method invocation on the returned proxy.");
        exception.setStackTrace(invokedAt);
        throw exception;
    }
    
}
