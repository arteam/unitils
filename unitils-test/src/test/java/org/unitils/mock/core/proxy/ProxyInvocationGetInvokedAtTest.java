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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationGetInvokedAtTest {

    private ProxyInvocation proxyInvocation;

    private StackTraceElement stackTraceElement1;
    private StackTraceElement stackTraceElement2;


    @Before
    public void initialize() throws Exception {
        stackTraceElement1 = new StackTraceElement("class1", "method1", "file1", 1);
        stackTraceElement2 = new StackTraceElement("class2", "method2", "file2", 2);
        StackTraceElement[] stackTrace = new StackTraceElement[]{stackTraceElement1, stackTraceElement2};
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        proxyInvocation = new ProxyInvocation(null, null, null, null, arguments, stackTrace);
    }


    @Test
    public void getInvokedAt() throws Throwable {
        StackTraceElement result = proxyInvocation.getInvokedAt();
        assertSame(stackTraceElement1, result);
    }
}
