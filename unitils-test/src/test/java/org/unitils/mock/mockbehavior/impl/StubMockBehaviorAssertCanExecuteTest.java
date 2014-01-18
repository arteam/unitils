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
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * @author Tim Ducheyne
 */
public class StubMockBehaviorAssertCanExecuteTest {

    private StubMockBehavior stubMockBehavior;


    @Before
    public void initialize() {
        stubMockBehavior = new StubMockBehavior();
    }


    @Test
    public void alwaysAllowed() throws Exception {
        Method method = MyInterface.class.getMethod("voidMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        stubMockBehavior.assertCanExecute(proxyInvocation);
    }


    private static interface MyInterface {

        void voidMethod();
    }
}

