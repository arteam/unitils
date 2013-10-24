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

import net.sf.cglib.proxy.MethodProxy;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.unitils.mock.core.proxy.CglibProxyMethodInterceptor.CglibProxyInvocation;

/**
 * Note: this class cannot be moved to unitils-test.
 * Compilation will fail since ASM classes are shaded during package phase.
 *
 * @author Tim Ducheyne
 */
public class CglibProxyInvocationInvokeOriginalBehaviorTest {

    private CglibProxyInvocation cglibProxyInvocation;

    private MethodProxy methodProxy;
    private StackTraceElement[] stackTrace;


    @Before
    public void initialize() throws Exception {
        stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 10)};
    }


    @Test
    public void invokeOriginalBehavior() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("method", String.class);
        methodProxy = MethodProxy.create(MyClass.class, MyClass.class, getMethodDescriptor(method), "method", "method");
        cglibProxyInvocation = new CglibProxyInvocation("mockName", method, asList("value"), asList("cloned value"), stackTrace, new MyClass(), methodProxy);

        Object result = cglibProxyInvocation.invokeOriginalBehavior();
        assertEquals("original result value", result);
    }

    @Test
    public void exceptionWhenAbstractMethod() throws Throwable {
        Method method = AbstractClass.class.getDeclaredMethod("abstractMethod");
        methodProxy = MethodProxy.create(AbstractClass.class, Void.class, getMethodDescriptor(method), "abstractMethod", "abstractMethod");
        cglibProxyInvocation = new CglibProxyInvocation("mockName", method, emptyList(), emptyList(), stackTrace, "object", methodProxy);
        try {
            cglibProxyInvocation.invokeOriginalBehavior();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Cannot invoke original behavior of an abstract method. Method: " + method, e.getMessage());
        }
    }


    private static abstract class AbstractClass {

        public abstract void abstractMethod();
    }

    private static class MyClass {

        public String method(String arg) {
            return "original result " + arg;
        }
    }
}
