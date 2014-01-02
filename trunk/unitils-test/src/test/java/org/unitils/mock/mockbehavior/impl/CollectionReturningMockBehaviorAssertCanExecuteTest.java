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
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class CollectionReturningMockBehaviorAssertCanExecuteTest {

    private CollectionReturningMockBehavior collectionReturningMockBehavior;


    @Test
    public void listReturnType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void setReturnType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = MyInterface.class.getMethod("setMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void arrayReturnType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = MyInterface.class.getMethod("arrayMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void nullReturnValue() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(null);
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void nullElementValues() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(null, null);
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void emptyReturnValue() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior();
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void assignableListValues() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new SubClass(), new MyClass());
        Method method = MyInterface.class.getMethod("myClassListMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void assignableSetValues() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new SubClass(), new MyClass());
        Method method = MyInterface.class.getMethod("myClassSetMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void assignableArrayValues() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new SubClass(), new MyClass());
        Method method = MyInterface.class.getMethod("myClassArrayMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void wildcard() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new SubClass(), new MyClass());
        Method method = MyInterface.class.getMethod("wildcardMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void assignableWildcardExtend() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new SubClass(), new MyClass());
        Method method = MyInterface.class.getMethod("extendsWildcardMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void rawListType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = MyInterface.class.getMethod("rawListMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void boundGenericType() throws Exception {
        GenericClass<String> genericClass = new GenericClass<String>();
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = genericClass.getClass().getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void noFailureWhenBoundGenericTypeIsNotAssignable() throws Exception {
        GenericClass<Properties> propertiesGenericClass = new GenericClass<Properties>();
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = propertiesGenericClass.getClass().getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void notBoundType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = MyInterface.class.getMethod("notBoundType");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void rawSetType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");
        Method method = MyInterface.class.getMethod("rawSetMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenNotAssignableWildcardExtend() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new Properties());
        Method method = MyInterface.class.getMethod("extendsWildcardMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: ? extends org.unitils.mock.mockbehavior.impl.CollectionReturningMockBehaviorAssertCanExecuteTest$MyClass, actual type: class java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNotAssignableListValue() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new Properties());
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class java.lang.String, actual type: class java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNotAssignableSetValue() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new Properties());
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class java.lang.String, actual type: class java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNotAssignableArrayValue() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new Properties());
        Method method = MyInterface.class.getMethod("arrayMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class java.lang.String, actual type: class java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void wrappedReturnValue() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new MyWrapper("test"));
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenVoidReturnType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("test");
        Method method = MyInterface.class.getMethod("voidMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to define mock behavior that returns a value for a void method.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenWrappedTypeIsNotAssignableToReturnType() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new MyWrapper("value"));
        Method method = MyInterface.class.getMethod("myClassListMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class org.unitils.mock.mockbehavior.impl.CollectionReturningMockBehaviorAssertCanExecuteTest$MyClass, actual type: class java.lang.String", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNotListSetOrArray() throws Exception {
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new Properties());
        Method method = MyInterface.class.getMethod("method");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            collectionReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The method does not have a list, set or array return type.", e.getMessage());
        }
    }

    private static interface MyInterface {

        List<String> listMethod();

        Set<String> setMethod();

        String[] arrayMethod();

        void voidMethod();

        List<MyClass> myClassListMethod();

        Set<MyClass> myClassSetMethod();

        MyClass[] myClassArrayMethod();

        List<?> wildcardMethod();

        List<? extends MyClass> extendsWildcardMethod();

        <T> List<T> notBoundType();

        String method();

        List rawListMethod();

        List rawSetMethod();
    }

    private static class GenericClass<T> {

        public List<T> listMethod() {
            return null;
        }
    }

    private static class MyWrapper implements ObjectToInjectHolder<String> {

        private String value;

        private MyWrapper(String value) {
            this.value = value;
        }

        public String getObjectToInject() {
            return value;
        }

        public Type getObjectToInjectType(Type declaredType) {
            return String.class;
        }
    }

    private static class MyClass {
    }

    private static class SubClass extends MyClass {
    }
}

