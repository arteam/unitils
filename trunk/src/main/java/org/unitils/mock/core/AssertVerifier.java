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
package org.unitils.mock.core;

import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtils.createProxy;
import static org.unitils.util.StackTraceUtils.getInvocationStackTrace;

public abstract class AssertVerifier<T> {

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;

    /* The name of the mock (e.g. the name of the field) */
    protected String mockName;

    /* The class type that is mocked */
    protected Class<T> mockedType;

    protected SyntaxMonitor syntaxMonitor;


    public AssertVerifier(String mockName, Class<T> mockedType, Scenario scenario, SyntaxMonitor syntaxMonitor) {
        this.mockName = mockName;
        this.mockedType = mockedType;
        this.scenario = scenario;
        this.syntaxMonitor = syntaxMonitor;
    }


    public T getProxyInstance(String definingMethodName) {
        StackTraceElement[] invocationStackTrace = getInvocationStackTrace(MockObject.class);
        syntaxMonitor.startBehaviorDefinition(mockName, definingMethodName, invocationStackTrace);
        return createProxy(mockedType, new InvocationHandler());
    }


    protected abstract void handleAssertVerificationInvocation(ProxyInvocation proxyInvocation);


    protected class InvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            syntaxMonitor.endBehaviorDefinition(proxyInvocation);
            handleAssertVerificationInvocation(proxyInvocation);
            return null;
        }
    }
}