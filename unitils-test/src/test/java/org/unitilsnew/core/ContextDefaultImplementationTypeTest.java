/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitilsnew.core.config.Configuration;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ContextDefaultImplementationTypeTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        context = new Context(configurationMock.getMock());
    }


    @Test
    public void getAndSetDefaultImplementationType() {
        context.setDefaultImplementationType(TestInterface.class, TestClass.class);
        Class<?> result = context.getDefaultImplementationType(TestInterface.class);

        assertEquals(TestClass.class, result);
    }

    @Test
    public void classifiers() {
        context.setDefaultImplementationType(TestInterface.class, TestClass.class, "a", "b");
        Class<?> result = context.getDefaultImplementationType(TestInterface.class, "a", "b");

        assertEquals(TestClass.class, result);
    }

    @Test
    public void notFound() {
        Class<?> result = context.getDefaultImplementationType(TestInterface.class);

        assertNull(result);
    }

    @Test
    public void defaultTypeIsUsedWhenNoConfigFound() {
        context.setDefaultImplementationType(TestInterface.class, TestClass.class);

        TestInterface result = context.getInstanceOfType(TestInterface.class);

        assertTrue(result instanceof TestClass);
    }

    @Test
    public void defaultTypeNotUsedWhenConfigFound() {
        configurationMock.returns(OtherTestClass.class.getName()).getOptionalString(TestInterface.class.getName());
        context.setDefaultImplementationType(TestInterface.class, TestClass.class);

        TestInterface result = context.getInstanceOfType(TestInterface.class);

        assertTrue(result instanceof OtherTestClass);
    }


    protected static interface TestInterface {
    }

    protected static class TestClass implements TestInterface {
    }

    protected static class OtherTestClass implements TestInterface {
    }
}
