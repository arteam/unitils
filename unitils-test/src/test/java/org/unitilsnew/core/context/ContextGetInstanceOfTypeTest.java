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

package org.unitilsnew.core.context;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.Factory;
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
        configurationMock.returns(NoConstructorClass.class.getName()).getOptionalString(TestInterface.class.getName());

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof NoConstructorClass);
    }

    @Test
    public void noArgumentConstructor() {
        configurationMock.returns(NoArgumentConstructorClass.class.getName()).getOptionalString(TestInterface.class.getName());

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof NoArgumentConstructorClass);
    }

    @Test
    public void noConfigButDefaultFound() {
        context.setDefaultImplementationType(TestInterface.class, NoArgumentConstructorClass.class);

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof NoArgumentConstructorClass);
    }

    @Test
    public void noConfigOrDefaultFoundButFactoryExists() {
        TestInterface2 result = context.getInstanceOfType(TestInterface2.class);
        assertTrue(result instanceof TestInterface2Impl);
    }

    @Test
    public void typeIsUsedWhenNoOtherDefaultOrFactoryFound() {
        NoArgumentConstructorClass result = context.getInstanceOfType(NoArgumentConstructorClass.class);
        assertNotNull(result);
    }

    @Test
    public void noConfigFoundButTypeIsInterface() {
        try {
            context.getInstanceOfType(TestInterface.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance for type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface.\n" +
                    "Reason: No implementation type configured for given interface type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface", e.getMessage());
        }
    }

    @Test
    public void sameInstanceIsReturnedForSecondCall() {
        NoArgumentConstructorClass result1 = context.getInstanceOfType(NoArgumentConstructorClass.class);
        NoArgumentConstructorClass result2 = context.getInstanceOfType(NoArgumentConstructorClass.class);
        assertSame(result1, result2);
    }

    @Test
    public void constructorWithArguments() {
        configurationMock.returns(ConstructorsWithArgumentsClass.class.getName()).getOptionalString(TestInterface.class.getName());

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof ConstructorsWithArgumentsClass);
        assertNotNull(((ConstructorsWithArgumentsClass) result).argument1);
        assertNotNull(((ConstructorsWithArgumentsClass) result).argument2);
    }

    @Test
    public void tooManyConstructors() {
        configurationMock.returns(TwoConstructorsClass.class.getName()).getOptionalString(TestInterface.class.getName());
        try {
            TestInterface result = context.getInstanceOfType(TestInterface.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance for type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface.\n" +
                    "Reason: Found more than 1 constructor in implementation type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TwoConstructorsClass", e.getMessage());
        }
    }

    @Test
    public void useNoArgsConstructorWhenThereIsMoreThanOneConstructor() {
        configurationMock.returns(TwoConstructorsClassWithDefault.class.getName()).getOptionalString(TestInterface.class.getName());

        TestInterface result = context.getInstanceOfType(TestInterface.class);
        assertTrue(result instanceof TwoConstructorsClassWithDefault);
    }

    @Test
    public void invalidClassName() {
        configurationMock.returns("xxx").getOptionalString(TestInterface.class.getName());
        try {
            context.getInstanceOfType(TestInterface.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance for type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface.\n" +
                    "Reason: Invalid implementation type xxx\n" +
                    "Reason: ClassNotFoundException: xxx", e.getMessage());
        }
    }

    @Test
    public void implementationTypeShouldNotBeAnInterface() {
        configurationMock.returns(TestInterface.class.getName()).getOptionalString(TestInterface.class.getName());
        try {
            context.getInstanceOfType(TestInterface.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance for type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface.\n" +
                    "Reason: Interface found as implementation type of org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface", e.getMessage());
        }
    }

    @Test
    public void implementationTypeShouldBeOfCorrectType() {
        configurationMock.returns(StringBuffer.class.getName()).getOptionalString(TestInterface.class.getName());
        try {
            context.getInstanceOfType(TestInterface.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance for type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface.\n" +
                    "Reason: Implementation type java.lang.StringBuffer is not of type org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface or org.unitilsnew.core.Factory<org.unitilsnew.core.context.ContextGetInstanceOfTypeTest$TestInterface>", e.getMessage());
        }
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

        public TwoConstructorsClass(String a) {
        }

        public TwoConstructorsClass(int a) {
        }
    }

    protected static class TwoConstructorsClassWithDefault implements TestInterface {

        public TwoConstructorsClassWithDefault() {
        }

        public TwoConstructorsClassWithDefault(int a) {
        }
    }

    protected static interface TestInterface2 {
    }

    protected static class TestInterface2Impl implements TestInterface2 {
    }

    protected static class TestInterface2Factory implements Factory<TestInterface2> {

        public TestInterface2 create() {
            return new TestInterface2Impl();
        }
    }

}
