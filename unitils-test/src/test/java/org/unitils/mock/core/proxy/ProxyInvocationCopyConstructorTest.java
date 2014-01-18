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

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationCopyConstructorTest {

    private ProxyInvocation proxyInvocation;


    @Before
    public void initialize() throws Exception {
        Object proxy = new Properties();
        Method method = MyInterface.class.getMethod("method");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>("1", "2", String.class));
        StackTraceElement[] stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 1)};
        proxyInvocation = new ProxyInvocation("1", "name", proxy, method, arguments, stackTrace);
    }


    @Test
    public void getMethod() throws Throwable {
        ProxyInvocation result = new ProxyInvocation(proxyInvocation);
        assertReflectionEquals(proxyInvocation, result);
    }


    private static interface MyInterface {

        void method();
    }
}
