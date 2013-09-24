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
package org.unitils.mock.mockbehavior.impl;

import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.ObjectToInjectHolder;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class ValueReturningMockBehaviorAssertCanExecuteTest {

    private ValueReturningMockBehavior valueReturningMockBehavior;


    @Test
    public void canExecute() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior("test");

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        valueReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void nullReturnValue() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior(null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        valueReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void assignableReturnValue() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior(new MyClass());

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("typeMethod"));
        valueReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void wrappedReturnValue() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior(new MyWrapper());

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        valueReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenVoidReturnType() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior("test");

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("voidMethod"));
        try {
            valueReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to define mock behavior that returns a value for a void method.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenReturnValueNotAssignableToReturnType() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior("xxx");

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("typeMethod"));
        try {
            valueReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to make a method return a value that is not assignable to the return type. Return type: class org.unitils.mock.mockbehavior.impl.ValueReturningMockBehaviorAssertCanExecuteTest$SuperClass, value type: class java.lang.String, value: xxx", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenWrappedTypeIsNotAssignableToReturnType() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior(new MyWrapper());

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("typeMethod"));
        try {
            valueReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to make a method return a value that is not assignable to the return type. Return type: class org.unitils.mock.mockbehavior.impl.ValueReturningMockBehaviorAssertCanExecuteTest$SuperClass, value type: class java.lang.String, value: object to inject", e.getMessage());
        }
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        return new ProxyInvocation(null, null, method, emptyList(), emptyList(), null);
    }


    private static interface MyInterface {

        String method();

        void voidMethod();

        SuperClass typeMethod();
    }

    private static class SuperClass {
    }

    private static class MyClass extends SuperClass {
    }

    private static class MyWrapper implements ObjectToInjectHolder<String> {

        public String getObjectToInject() {
            return "object to inject";
        }

        public Type getObjectToInjectType(Type declaredType) {
            return String.class;
        }
    }
}

