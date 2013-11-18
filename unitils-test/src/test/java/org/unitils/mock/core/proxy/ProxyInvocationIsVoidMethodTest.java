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

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationIsVoidMethodTest {

    private ProxyInvocation proxyInvocation;


    @Test
    public void trueWhenVoidMethod() throws Exception {
        Method method = TestInterface.class.getMethod("voidMethod");
        proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        boolean result = proxyInvocation.isVoidMethod();
        assertTrue(result);
    }

    @Test
    public void falseWhenNotVoidMethod() throws Exception {
        Method method = TestInterface.class.getMethod("returnMethod");
        proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        boolean result = proxyInvocation.isVoidMethod();
        assertFalse(result);
    }


    private static interface TestInterface {

        void voidMethod();

        int returnMethod();
    }
}
