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
import org.unitils.core.util.ObjectToFormat;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.util.CloneService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.any;
import static org.unitils.mock.core.proxy.CglibProxyMethodInterceptor.CglibProxyInvocation;

/**
 * Note: this class cannot be moved to unitils-test.
 * Compilation will fail since ASM classes are shaded during package phase.
 *
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
        cglibProxyMethodInterceptor = new CglibProxyMethodInterceptor<MyClass>("i", "myProxy", MyClass.class, proxyInvocationHandlerMock.getMock(), proxyServiceMock.getMock(), cloneServiceMock.getMock());

        stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 10)};
        proxyServiceMock.returns(stackTrace).getProxiedMethodStackTrace(any(StackTraceElement[].class));
        cloneServiceMock.returns(new Object[0]).createDeepClone(new Object[0]);

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
    public void cloneReturnsProxyItself() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("clone");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertSame(proxy, result);
    }

    @Test
    public void equalsReturnsFalseWhenNotSameIfNoBehaviorDefined() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("equals", Object.class);

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{new MyClass()}, methodProxy);
        assertEquals(false, result);
    }

    @Test
    public void equalsCanBeOverridden() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("equals", Object.class);
        List<Argument<?>> arguments = new ArrayList<Argument<?>>(0);
        CglibProxyInvocation cglibProxyInvocation = new CglibProxyInvocation("i", "myProxy", method, arguments, stackTrace, proxy, methodProxy);
        proxyInvocationHandlerMock.returns(true).handleInvocation(cglibProxyInvocation);

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertEquals(true, result);
    }

    @Test
    public void hashCodeReturnsHashCodeOfInterceptorIfNoBehaviorDefined() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("hashCode");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertEquals(cglibProxyMethodInterceptor.hashCode(), result);
    }

    @Test
    public void hashCodeCanBeOverridden() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("hashCode");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>(0);
        CglibProxyInvocation cglibProxyInvocation = new CglibProxyInvocation("i", "myProxy", method, arguments, stackTrace, proxy, methodProxy);
        proxyInvocationHandlerMock.returns(55).handleInvocation(cglibProxyInvocation);

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertEquals(55, result);
    }

    @Test
    public void toStringReturnsNameAndHashCodeIfNoBehaviorDefined() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("toString");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertEquals(MyClass.class.getName() + "@" + Integer.toHexString(cglibProxyMethodInterceptor.hashCode()), result);
    }

    @Test
    public void toStringCanBeOverridden() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("toString");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>(0);
        CglibProxyInvocation cglibProxyInvocation = new CglibProxyInvocation("i", "myProxy", method, arguments, stackTrace, proxy, methodProxy);
        proxyInvocationHandlerMock.returns("result").handleInvocation(cglibProxyInvocation);

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertEquals("result", result);
    }

    @Test
    public void formatObjectReturnsDisplayName() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("$formatObject");

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[0], methodProxy);
        assertEquals("Proxy<myProxy>", result);
    }

    @Test
    public void methodInvocation() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("method", String.class);
        cloneServiceMock.returns("cloned arg").createDeepClone("arg");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>("arg", "cloned arg", String.class));
        proxyInvocationHandlerMock.returns("result").handleInvocation(new CglibProxyInvocation("i", "myProxy", method, arguments, stackTrace, proxy, methodProxy));

        Object result = cglibProxyMethodInterceptor.intercept(proxy, method, new Object[]{"arg"}, methodProxy);
        assertEquals("result", result);
    }


    public static class MyClass implements ObjectToFormat {

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

        public String method(String arg) {
            return null;
        }

        public String $formatObject() {
            return null;
        }
    }
}
