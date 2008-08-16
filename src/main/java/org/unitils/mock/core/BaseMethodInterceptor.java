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
package org.unitils.mock.core;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import static org.unitils.mock.util.ProxyUtil.getProxiedMethodStackTraceElement;

import java.lang.reflect.Method;
import static java.util.Arrays.asList;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public abstract class BaseMethodInterceptor<T> implements MethodInterceptor {

    private MockObject<T> mockObject;

    private Scenario scenario;


    public BaseMethodInterceptor(MockObject<T> mockObject, Scenario scenario) {
        this.mockObject = mockObject;
        this.scenario = scenario;
    }


    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getDeclaringClass().equals(MockObjectProxy.class)) {
            // handle the single $_$_getMockObject method of the MockObjectProxy interface
            return mockObject;
        }

        StackTraceElement invokedAt = getProxiedMethodStackTraceElement(Thread.currentThread().getStackTrace());
        Invocation invocation = new Invocation(mockObject, object, method, asList(args), invokedAt, methodProxy);
        return handleInvocation(invocation);
    }

    public abstract Object handleInvocation(Invocation invocation) throws Throwable;


    public MockObject<T> getMockObject() {
        return mockObject;
    }

    public Scenario getScenario() {
        return scenario;
    }

}