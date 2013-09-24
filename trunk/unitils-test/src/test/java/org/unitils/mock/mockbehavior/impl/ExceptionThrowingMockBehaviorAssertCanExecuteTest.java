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
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class ExceptionThrowingMockBehaviorAssertCanExecuteTest {

    private ExceptionThrowingMockBehavior exceptionThrowingMockBehavior;


    @Test
    public void canExecuteWhenError() throws Exception {
        exceptionThrowingMockBehavior = new ExceptionThrowingMockBehavior(new AbstractMethodError(), null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        exceptionThrowingMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void canExecuteWhenRuntimeException() throws Exception {
        exceptionThrowingMockBehavior = new ExceptionThrowingMockBehavior(new RuntimeException(), null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        exceptionThrowingMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void canExecuteWhenExceptionIsDeclaredInThrowsClause() throws Exception {
        exceptionThrowingMockBehavior = new ExceptionThrowingMockBehavior(new IOException(), null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("exceptionMethod"));
        exceptionThrowingMockBehavior.assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenCheckedExceptionAndNoThrowsClause() throws Exception {
        exceptionThrowingMockBehavior = new ExceptionThrowingMockBehavior(new SAXException(), null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        try {
            exceptionThrowingMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to make a method throw an exception that it doesn't declare. Exception type: class org.xml.sax.SAXException, no declared exceptions", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenCheckedExceptionNotNotDeclaredInThrowsClause() throws Exception {
        exceptionThrowingMockBehavior = new ExceptionThrowingMockBehavior(new SAXException(), null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("exceptionMethod"));
        try {
            exceptionThrowingMockBehavior.assertCanExecute(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to make a method throw an exception that it doesn't declare. Exception type: class org.xml.sax.SAXException, declared exceptions: [class java.sql.SQLException, class java.io.IOException]", e.getMessage());
        }
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        return new ProxyInvocation(null, null, method, emptyList(), emptyList(), null);
    }


    private static interface MyInterface {

        void method();

        void exceptionMethod() throws SQLException, IOException;
    }
}

