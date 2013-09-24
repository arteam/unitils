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
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.mock.core.proxy.CglibProxyMethodInterceptor.CglibProxyInvocation;

/**
 * @author Tim Ducheyne
 */
public class CglibProxyMethodInterceptorInterceptTest extends UnitilsJUnit4 {

    private CglibProxyMethodInterceptor<MyClass> cglibProxyMethodInterceptor;

    private Mock<ProxyInvocationHandler> proxyInvocationHandlerMock;
    private Mock<ProxyService> proxyServiceMock;
    private Mock<CloneService> cloneServiceMock;
    @Dummy
    private MyClass proxy;
    private MethodProxy methodProxy;
    private StackTraceElement[] stackTrace;


    @Before
    public void initialize() throws Exception {
        cglibProxyMethodInterceptor = new CglibProxyMethodInterceptor<MyClass>("mockName", MyClass.class, proxyInvocationHandlerMock.getMock(), proxyServiceMock.getMock(), cloneServiceMock.getMock());

        stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 10)};
        proxyServiceMock.returns(stackTrace).getProxiedMethodStackTrace();

        methodProxy = MethodProxy.create(MyClass.class, MyClass.class, "", "", "");
    }


    @Test
    public void ignoreFinalizeMethod() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("finalize");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertNull(result);
    }

    @Test
    public void equalsReturnsTrueWhenSame() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("equals", Object.class);

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{proxy}, methodProxy);
        assertEquals(true, result);
    }

    @Test
    public void equalsReturnsFalseWhenNotSame() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("equals", Object.class);

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{new MyClass()}, methodProxy);
        assertEquals(false, result);
    }

    @Test
    public void hashCodeReturnsHashCodeOfInterceptor() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("hashCode");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{new MyClass()}, methodProxy);
        assertEquals(cglibProxyMethodInterceptor.hashCode(), result);
    }

    @Test
    public void cloneReturnsProxyItself() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("clone");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{new MyClass()}, methodProxy);
        assertSame(proxy, result);
    }

    @Test
    public void toStringReturnsNameAndHashCode() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("toString");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{new MyClass()}, methodProxy);
        assertEquals(MyClass.class.getName() + "@" + Integer.toHexString(cglibProxyMethodInterceptor.hashCode()), result);
    }

    @Test
    public void methodInvocation() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("method");
        cloneServiceMock.returns("cloned arg").createDeepClone("arg");
        proxyInvocationHandlerMock.returns("result").handleInvocation(new CglibProxyInvocation("mockName", method, asList("arg"), asList("cloned arg"), stackTrace, proxy, methodProxy));

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{"arg"}, methodProxy);
        assertEquals("result", result);
    }


    public static class MyClass {

        @Override
        protected void finalize() {
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return -1;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return null;
        }

        public String method() {
            return null;
        }
    }
}
