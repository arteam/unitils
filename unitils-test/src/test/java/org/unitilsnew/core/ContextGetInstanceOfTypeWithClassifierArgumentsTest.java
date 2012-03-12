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
import org.unitils.mock.Mock;
import org.unitilsnew.core.annotation.Classifier;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.config.Configuration;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ContextGetInstanceOfTypeWithClassifierArgumentsTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        context = new Context(configurationMock.getMock());
    }


    @Test
    public void singleClassifier() {
        configurationMock.returns(TestClassA.class.getName()).getOptionalString(TestInterface.class.getName(), "a");
        configurationMock.returns(TestClassB.class.getName()).getOptionalString(TestInterface.class.getName(), "b");

        SingleClassifierClass result = context.getInstanceOfType(SingleClassifierClass.class);
        assertTrue(result.value1 instanceof TestClassA);
        assertTrue(result.value2 instanceof TestClassB);
    }

    @Test
    public void multipleClassifiers() {
        configurationMock.returns(TestClassA.class.getName()).getOptionalString(TestInterface.class.getName(), "a", "b");

        MultipleClassifierClass result = context.getInstanceOfType(MultipleClassifierClass.class);
        assertTrue(result.value instanceof TestClassA);
    }

    @Test
    public void propertyClassifiers() {
        configurationMock.returns("value").getValueOfType(String.class, "property", "a", "b");

        PropertyClassifierClass result = context.getInstanceOfType(PropertyClassifierClass.class);
        assertEquals("value", result.value);
    }

    @Test
    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public void nullClassifierSameAsNoClassifiers() {
        configurationMock.returns(TestClassA.class.getName()).getOptionalString(TestInterface.class.getName());

        TestInterface result1 = context.getInstanceOfType(TestInterface.class);
        TestInterface result2 = context.getInstanceOfType(TestInterface.class, null);
        assertSame(result1, result2);
    }

    @Test
    @SuppressWarnings("RedundantArrayCreation")
    public void emptyClassifierSameAsNoClassifiers() {
        configurationMock.returns(TestClassA.class.getName()).getOptionalString(TestInterface.class.getName());

        TestInterface result1 = context.getInstanceOfType(TestInterface.class);
        TestInterface result2 = context.getInstanceOfType(TestInterface.class, new String[0]);
        assertSame(result1, result2);
    }


    protected static class SingleClassifierClass {

        protected TestInterface value1;
        protected TestInterface value2;

        public SingleClassifierClass(@Classifier("a") TestInterface value1,
                                     @Classifier("b") TestInterface value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
    }

    protected static class MultipleClassifierClass {

        protected TestInterface value;

        public MultipleClassifierClass(@Classifier({"a", "b"}) TestInterface value) {
            this.value = value;
        }
    }

    protected static class PropertyClassifierClass {

        protected String value;

        public PropertyClassifierClass(@Property("property") @Classifier({"a", "b"}) String value) {
            this.value = value;
        }
    }


    protected static interface TestInterface {
    }

    protected static class TestClassA implements TestInterface {
    }

    protected static class TestClassB implements TestInterface {
    }

}
