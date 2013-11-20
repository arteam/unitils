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
import org.unitils.core.UnitilsException;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ProxyServiceCreateUninitializedInstanceOfTypeTest {

    private ProxyService proxyService;


    @Before
    public void initialize() {
        proxyService = new ProxyService(null);
    }


    @Test
    public void createUninitializedInstanceOfType() {
        TestClass result = proxyService.createUninitializedInstanceOfType(TestClass.class);
        assertNull(result.value);
    }

    @Test
    public void constructorNotCalled() {
        DefaultConstructorTestClass result = proxyService.createUninitializedInstanceOfType(DefaultConstructorTestClass.class);
        assertNull(result.value);
    }

    @Test
    public void canCreateWhenNoDefaultConstructor() {
        NoDefaultConstructorTestClass result = proxyService.createUninitializedInstanceOfType(NoDefaultConstructorTestClass.class);
        assertNull(result.value);
    }

    @Test
    public void exceptionWhenInterface() {
        try {
            proxyService.createUninitializedInstanceOfType(TestInterface.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Cannot create instance of an interface or abstract type: interface org.unitils.mock.core.proxy.ProxyServiceCreateUninitializedInstanceOfTypeTest$TestInterface", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenAbstractClass() {
        try {
            proxyService.createUninitializedInstanceOfType(AbstractTestClass.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Cannot create instance of an interface or abstract type: class org.unitils.mock.core.proxy.ProxyServiceCreateUninitializedInstanceOfTypeTest$AbstractTestClass", e.getMessage());
        }
    }


    private static interface TestInterface {

        String method();
    }

    private static class TestClass {

        private String value = "test";
    }

    private static class DefaultConstructorTestClass {

        private String value;

        private DefaultConstructorTestClass() {
            this.value = "test";
        }
    }

    private static class NoDefaultConstructorTestClass {

        private String value = "test";

        public NoDefaultConstructorTestClass(String arg) {
        }
    }

    private static abstract class AbstractTestClass {
    }
}
