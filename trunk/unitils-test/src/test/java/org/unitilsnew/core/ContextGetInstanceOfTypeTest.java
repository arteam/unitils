/*
 * Copyright 2010,  Unitils.org
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
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.core.config.Configuration;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ContextGetInstanceOfTypeTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        context = new Context(configurationMock.getMock());
    }


    @Test
    public void noConstructors() {
        configurationMock.returns(NoConstructorClass.class.getName()).getOptionalString(TestInterface.class.getName(), null);

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof NoConstructorClass);
    }

    @Test
    public void noArgumentConstructor() {
        configurationMock.returns(NoArgumentConstructorClass.class.getName()).getOptionalString(TestInterface.class.getName(), null);

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof NoArgumentConstructorClass);
    }

    @Test
    public void typeIsUsedWhenNoConfigFound() {
        NoArgumentConstructorClass result = context.getInstanceOfType(NoArgumentConstructorClass.class);
        assertNotNull(result);
    }

    @Test(expected = UnitilsException.class)
    public void noConfigFoundButTypeIsInterface() {
        context.getInstanceOfType(TestInterface.class);
    }

    @Test
    public void sameInstanceIsReturnedForSecondCall() {
        NoArgumentConstructorClass result1 = context.getInstanceOfType(NoArgumentConstructorClass.class);
        NoArgumentConstructorClass result2 = context.getInstanceOfType(NoArgumentConstructorClass.class);
        assertSame(result1, result2);
    }

    @Test
    public void constructorWithArguments() {
        configurationMock.returns(ConstructorsWithArgumentsClass.class.getName()).getOptionalString(TestInterface.class.getName(), null);

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof ConstructorsWithArgumentsClass);
        assertNotNull(((ConstructorsWithArgumentsClass) result).argument1);
        assertNotNull(((ConstructorsWithArgumentsClass) result).argument2);
    }

    @Test(expected = UnitilsException.class)
    public void tooManyConstructors() {
        configurationMock.returns(TwoConstructorsClass.class.getName()).getOptionalString(TestInterface.class.getName(), null);

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof NoConstructorClass);
    }

    @Test(expected = UnitilsException.class)
    public void invalidClassName() {
        configurationMock.returns("xxx").getOptionalString(TestInterface.class.getName(), null);

        context.getInstanceOfType(TestInterface.class);
    }

    @Test(expected = UnitilsException.class)
    public void implementationTypeShouldNotBeAnInterface() {
        configurationMock.returns(TestInterface.class.getName()).getOptionalString(TestInterface.class.getName(), null);

        context.getInstanceOfType(TestInterface.class);
    }

    @Test(expected = UnitilsException.class)
    public void implementationTypeShouldBeOfCorrectType() {
        configurationMock.returns(StringBuffer.class.getName()).getOptionalString(TestInterface.class.getName(), null);

        context.getInstanceOfType(TestInterface.class);
    }


    protected static interface TestInterface {
    }

    protected static class NoConstructorClass implements TestInterface {
    }

    protected static class NoArgumentConstructorClass implements TestInterface {

        public NoArgumentConstructorClass() {
        }
    }

    protected static class ConstructorsWithArgumentsClass implements TestInterface {

        protected NoConstructorClass argument1;
        protected NoArgumentConstructorClass argument2;

        public ConstructorsWithArgumentsClass(NoConstructorClass argument1, NoArgumentConstructorClass argument2) {
            this.argument1 = argument1;
            this.argument2 = argument2;
        }
    }

    protected static class TwoConstructorsClass implements TestInterface {

        public TwoConstructorsClass() {
        }

        public TwoConstructorsClass(int a) {
        }
    }
}
