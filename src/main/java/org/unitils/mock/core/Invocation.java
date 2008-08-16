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

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class Invocation {

    private MockObject<?> mockObject;

    private Object object;

    private Method method;

    private List<?> arguments;

    private StackTraceElement invokedAt;

    private MethodProxy methodProxy;


    public Invocation(MockObject<?> mockObject, Object object, Method method, List<?> arguments, StackTraceElement invokedAt, MethodProxy methodProxy) {
        this.mockObject = mockObject;
        this.object = object;
        this.method = method;
        this.arguments = arguments;
        this.invokedAt = invokedAt;
        this.methodProxy = methodProxy;
    }


    public Object invokeOriginalBehavior() throws Throwable {
        return methodProxy.invokeSuper(object, arguments.toArray());
    }


    public MockObject<?> getMockObject() {
        return mockObject;
    }


    public Method getMethod() {
        return method;
    }


    public List<?> getArguments() {
        return arguments;
    }


    public StackTraceElement getInvokedAt() {
        return invokedAt;
    }

}