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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class OriginalBehaviorInvokingMockBehaviorAssertCanExecuteTest {

    private OriginalBehaviorInvokingMockBehavior originalBehaviorInvokingMockBehavior;


    @Before
    public void initialize() {
        originalBehaviorInvokingMockBehavior = new OriginalBehaviorInvokingMockBehavior();
    }


    @Test
    public void canExecute() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(MyClass.class.getMethod("method"));
        originalBehaviorInvokingMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenAbstractMethod() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(MyClass.class.getMethod("abstractMethod"));
        try {
            originalBehaviorInvokingMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to invoke the original method behavior. Invoked method is abstract: public abstract void org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehaviorAssertCanExecuteTest$MyClass.abstractMethod()", e.getMessage());
            assertReflectionEquals(proxyInvocation.getInvokedAtTrace(), e.getStackTrace());
        }
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        StackTraceElement[] stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 0)};
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        return new ProxyInvocation(null, null, method, arguments, stackTrace);
    }


    private static abstract class MyClass {

        public void method() {
        }

        public abstract void abstractMethod();
    }
}

