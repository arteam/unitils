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

package org.unitils.core.context;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.core.annotation.Property;
import org.unitils.core.config.Configuration;
import org.unitils.mock.Mock;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ContextGetInstanceOfTypeWithPropertyArgumentsTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        context = new Context(configurationMock.getMock());
    }


    @Test
    public void propertyTypes() {
        StringBuffer stringBuffer = new StringBuffer();
        configurationMock.returns("value").getValueOfType(String.class, "string");
        configurationMock.returns(true).getValueOfType(Boolean.class, "boolean");
        configurationMock.returns(true).getValueOfType(Boolean.TYPE, "boolean");
        configurationMock.returns(5).getValueOfType(Integer.TYPE, "integer");
        configurationMock.returns(5).getValueOfType(Integer.class, "integer");
        configurationMock.returns(6L).getValueOfType(Long.TYPE, "long");
        configurationMock.returns(6L).getValueOfType(Long.class, "long");
        configurationMock.returns(stringBuffer).getValueOfType(StringBuffer.class, "object");
        configurationMock.returns(FIELD).getValueOfType(ElementType.class, "enum");
        configurationMock.returns(Map.class).getValueOfType(Class.class, "class");

        SimpleTypesClass result = context.getInstanceOfType(SimpleTypesClass.class);
        assertEquals("value", result.stringValue);
        assertEquals(true, result.booleanSimpleValue);
        assertEquals(Boolean.TRUE, result.booleanWrapperValue);
        assertEquals(5, result.integerSimpleValue);
        assertEquals(new Integer(5), result.integerWrapperValue);
        assertEquals(6, result.longSimpleValue);
        assertEquals(new Long(6), result.longWrapperValue);
        assertSame(stringBuffer, result.objectValue);
        assertEquals(FIELD, result.enumValue);
        assertEquals(Map.class, result.classValue);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listPropertyTypes() {
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        configurationMock.returns(asList("value1", "value2")).getValueListOfType(String.class, "strings");
        configurationMock.returns(asList(true, false)).getValueListOfType(Boolean.class, "booleans");
        configurationMock.returns(asList(5, 6)).getValueListOfType(Integer.class, "integers");
        configurationMock.returns(asList(7L, 8L)).getValueListOfType(Long.class, "longs");
        configurationMock.returns(asList(stringBuffer1, stringBuffer2)).getValueListOfType(StringBuffer.class, "objects");
        configurationMock.returns(asList(FIELD, METHOD)).getValueListOfType(ElementType.class, "enums");
        configurationMock.returns(asList(Map.class, Set.class)).getValueListOfType(Class.class, "classes");

        ListTypesClass result = context.getInstanceOfType(ListTypesClass.class);
        assertReflectionEquals(asList("value1", "value2"), result.stringValues);
        assertReflectionEquals(asList(true, false), result.booleanValues);
        assertReflectionEquals(asList(5, 6), result.integerValues);
        assertReflectionEquals(asList(7L, 8L), result.longValues);
        assertReflectionEquals(asList(stringBuffer1, stringBuffer2), result.objectValues);
        assertReflectionEquals(asList(FIELD, METHOD), result.enumValues);
        assertReflectionEquals(asList(Map.class, Set.class), result.classValues);
    }

    @Test(expected = UnitilsException.class)
    public void invalidType() {
        configurationMock.raises(UnitilsException.class).getValueOfType(StringBuffer.class, "object");

        context.getInstanceOfType(ObjectTypeClass.class);
    }

    @Test
    public void nullWhenOptionalAndPropertyNotFound() {
        configurationMock.returns(null).getOptionalValueOfType(String.class, "optional");

        OptionalClass result = context.getInstanceOfType(OptionalClass.class);
        assertNull(result.optionalValue);
    }

    @Test
    public void emptyWhenOptionalAndListPropertyNotFound() {
        configurationMock.returns(new ArrayList<String>()).getOptionalValueListOfType(String.class, "optionalList");

        OptionalListClass result = context.getInstanceOfType(OptionalListClass.class);
        assertTrue(result.optionalValues.isEmpty());
    }

    @Test(expected = UnitilsException.class)
    public void exceptionWhenRequiredAndPropertyNotFound() {
        configurationMock.raises(UnitilsException.class).getValueOfType(String.class, "required");

        context.getInstanceOfType(RequiredClass.class);
    }

    @Test(expected = UnitilsException.class)
    public void exceptionWhenRequiredAndListPropertyNotFound() {
        configurationMock.raises(UnitilsException.class).getValueListOfType(String.class, "requiredList");

        context.getInstanceOfType(RequiredListClass.class);
    }

    @Test(expected = UnitilsException.class)
    public void exceptionWhenOptionalNotSpecifiedAndPropertyNotFound() {
        configurationMock.raises(UnitilsException.class).getValueOfType(String.class, "default");

        context.getInstanceOfType(RequiredIsDefaultClass.class);
    }

    @Test
    public void rawListTypeReturnsStringElements() {
        configurationMock.returns(asList("value1", "value2")).getValueListOfType(String.class, "rawValues");

        RawListClass result = context.getInstanceOfType(RawListClass.class);
        assertReflectionEquals(asList("value1", "value2"), result.rawValues);
    }


    protected static class SimpleTypesClass {

        protected String stringValue;
        protected boolean booleanSimpleValue;
        protected Boolean booleanWrapperValue;
        protected int integerSimpleValue;
        protected Integer integerWrapperValue;
        protected long longSimpleValue;
        protected Long longWrapperValue;
        protected StringBuffer objectValue;
        protected ElementType enumValue;
        protected Class<?> classValue;

        public SimpleTypesClass(@Property("string") String stringValue,
                                @Property("boolean") boolean booleanSimpleValue, @Property("boolean") Boolean booleanWrapperValue,
                                @Property("integer") int integerSimpleValue, @Property("integer") Integer integerWrapperValue,
                                @Property("long") long longSimpleValue, @Property("long") Long longWrapperValue,
                                @Property("object") StringBuffer objectValue, @Property("enum") ElementType enumValue,
                                @Property("class") Class<?> classValue) {
            this.stringValue = stringValue;
            this.booleanSimpleValue = booleanSimpleValue;
            this.booleanWrapperValue = booleanWrapperValue;
            this.integerSimpleValue = integerSimpleValue;
            this.integerWrapperValue = integerWrapperValue;
            this.longSimpleValue = longSimpleValue;
            this.longWrapperValue = longWrapperValue;
            this.objectValue = objectValue;
            this.enumValue = enumValue;
            this.classValue = classValue;
        }
    }

    protected static class ListTypesClass {

        protected List<String> stringValues;
        protected List<Boolean> booleanValues;
        protected List<Integer> integerValues;
        protected List<Long> longValues;
        protected List<StringBuffer> objectValues;
        protected List<ElementType> enumValues;
        protected List<Class<?>> classValues;

        public ListTypesClass(@Property("strings") List<String> stringValues,
                              @Property("booleans") List<Boolean> booleanValues,
                              @Property("integers") List<Integer> integerValues,
                              @Property("longs") List<Long> longValues,
                              @Property("objects") List<StringBuffer> objectValues,
                              @Property("enums") List<ElementType> enumValues,
                              @Property("classes") List<Class<?>> classValues) {
            this.stringValues = stringValues;
            this.booleanValues = booleanValues;
            this.integerValues = integerValues;
            this.longValues = longValues;
            this.objectValues = objectValues;
            this.enumValues = enumValues;
            this.classValues = classValues;
        }
    }

    protected static class ObjectTypeClass {

        protected StringBuffer objectValue;

        public ObjectTypeClass(@Property("object") StringBuffer objectValue) {
            this.objectValue = objectValue;
        }
    }

    protected static class OptionalClass {

        protected String optionalValue;

        public OptionalClass(@Property(value = "optional", optional = true) String optionalValue) {
            this.optionalValue = optionalValue;
        }
    }

    protected static class OptionalListClass {

        protected List<String> optionalValues;

        public OptionalListClass(@Property(value = "optionalList", optional = true) List<String> optionalValues) {
            this.optionalValues = optionalValues;
        }
    }

    protected static class RequiredClass {

        protected String requiredValue;

        public RequiredClass(@Property(value = "required", optional = false) String requiredValue) {
            this.requiredValue = requiredValue;
        }
    }

    protected static class RequiredListClass {

        protected List<String> requiredValues;

        public RequiredListClass(@Property(value = "requiredList", optional = false) List<String> requiredValues) {
            this.requiredValues = requiredValues;
        }
    }

    protected static class RequiredIsDefaultClass {

        protected String defaultIsRequiredValue;

        public RequiredIsDefaultClass(@Property("default") String defaultIsRequiredValue) {
            this.defaultIsRequiredValue = defaultIsRequiredValue;
        }
    }

    protected static class RawListClass {

        protected List rawValues;

        public RawListClass(@Property("rawValues") List rawValues) {
            this.rawValues = rawValues;
        }
    }

}
