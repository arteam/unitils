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
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.proxy.ProxyInvocation;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SyntaxMonitor {

    protected String mockName;

    protected String definingMethodName;

    protected StackTraceElement[] invokedAt;


    public void startBehaviorDefinition(String mockObjectName, String methodName, StackTraceElement[] invokedAt) {
        assertNotExpectingInvocation();
        this.mockName = mockObjectName;
        this.definingMethodName = methodName;
        this.invokedAt = invokedAt;
        ArgumentMatcherRepository.getInstance().registerStartOfMatchingInvocation(invokedAt[0].getLineNumber());
    }


    public void endBehaviorDefinition(ProxyInvocation proxyInvocation) {
        ArgumentMatcherRepository.getInstance().registerEndOfMatchingInvocation(proxyInvocation.getLineNumber(), "todo");
        reset();
    }

    public void reset() {
        this.mockName = null;
        this.definingMethodName = null;
        this.invokedAt = null;
    }


    public void assertNotExpectingInvocation() {
        if (mockName != null) {
            UnitilsException exception = new UnitilsException("Invalid syntax. " + mockName + "." + definingMethodName +
                    "() must be followed by a method invocation on the returned proxy. E.g. " + mockName + "." + definingMethodName + "().myMethod();");
            exception.setStackTrace(invokedAt);
            reset();
            throw exception;
        }
    }


}
