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
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class DefaultValueReturningMockBehaviorAssertCanExecuteTest {

    private DefaultValueReturningMockBehavior defaultValueReturningMockBehavior = new DefaultValueReturningMockBehavior();


    @Test
    public void canExecute() throws Exception {
        Method method = MyInterface.class.getMethod("method");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        defaultValueReturningMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenVoidReturnType() throws Exception {
        Method method = MyInterface.class.getMethod("voidMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        try {
            defaultValueReturningMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to define mock behavior that returns a value for a void method.", e.getMessage());
        }
    }


    private static interface MyInterface {

        String method();

        void voidMethod();
    }
}

