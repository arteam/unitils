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
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.config.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void simplePropertyTypes() {
        configurationMock.returns("value").getValueOfType(String.class, "string", null);
        configurationMock.returns(true).getValueOfType(Boolean.class, "boolean", null);
        configurationMock.returns(true).getValueOfType(Boolean.TYPE, "boolean", null);
        configurationMock.returns(5).getValueOfType(Integer.TYPE, "integer", null);
        configurationMock.returns(5).getValueOfType(Integer.class, "integer", null);
        configurationMock.returns(6L).getValueOfType(Long.TYPE, "long", null);
        configurationMock.returns(6L).getValueOfType(Long.class, "long", null);
        configurationMock.returns(new StringBuffer()).getValueOfType(StringBuffer.class, "object", null);

        SimpleTypesClass result = context.getInstanceOfType(SimpleTypesClass.class);
        assertEquals("value", result.stringValue);
        assertEquals(true, result.booleanSimpleValue);
        assertEquals(Boolean.TRUE, result.booleanWrapperValue);
        assertEquals(5, result.integerSimpleValue);
        assertEquals(new Integer(5), result.integerWrapperValue);
        assertEquals(6, result.longSimpleValue);
        assertEquals(new Long(6), result.longWrapperValue);
    }

    @Test
    public void listPropertyTypes() {
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        configurationMock.returns(asList("value1", "value2")).getValueOfType(List.class, "strings", null);
        configurationMock.returns(asList(true, false)).getValueOfType(List.class, "booleans", null);
        configurationMock.returns(asList(5, 6)).getValueOfType(List.class, "integers", null);
        configurationMock.returns(asList(7L, 8L)).getValueOfType(List.class, "longs", null);
        configurationMock.returns(asList(stringBuffer1, stringBuffer2)).getValueOfType(List.class, "objects", null);

        ListTypesClass result = context.getInstanceOfType(ListTypesClass.class);
        assertReflectionEquals(asList("value1", "value2"), result.stringValues);
        assertReflectionEquals(asList(true, false), result.booleanValues);
        assertReflectionEquals(asList(5, 6), result.integerValues);
        assertReflectionEquals(asList(7L, 8L), result.longValues);
        assertReflectionEquals(asList(stringBuffer1, stringBuffer2), result.objectValues);
    }

    @Test
    public void objectType() {
        configurationMock.returns(new StringBuffer()).getValueOfType(StringBuffer.class, "object", null);

        ObjectTypeClass result = context.getInstanceOfType(ObjectTypeClass.class);
        assertTrue(result.objectValue instanceof StringBuffer);
    }

    @Test(expected = UnitilsException.class)
    public void invalidType() {
        configurationMock.raises(UnitilsException.class).getValueOfType(StringBuffer.class, "object", null);

        context.getInstanceOfType(ObjectTypeClass.class);
    }


    protected static class SimpleTypesClass {

        protected String stringValue;
        protected boolean booleanSimpleValue;
        protected Boolean booleanWrapperValue;
        protected int integerSimpleValue;
        protected Integer integerWrapperValue;
        protected long longSimpleValue;
        protected Long longWrapperValue;

        public SimpleTypesClass(@Property("string") String stringValue,
                                @Property("boolean") boolean booleanSimpleValue, @Property("boolean") Boolean booleanWrapperValue,
                                @Property("integer") int integerSimpleValue, @Property("integer") Integer integerWrapperValue,
                                @Property("long") long longSimpleValue, @Property("long") Long longWrapperValue) {
            this.stringValue = stringValue;
            this.booleanSimpleValue = booleanSimpleValue;
            this.booleanWrapperValue = booleanWrapperValue;
            this.integerSimpleValue = integerSimpleValue;
            this.integerWrapperValue = integerWrapperValue;
            this.longSimpleValue = longSimpleValue;
            this.longWrapperValue = longWrapperValue;
        }
    }

    protected static class ObjectTypeClass {

        protected StringBuffer objectValue;

        public ObjectTypeClass(@Property("object") StringBuffer objectValue) {
            this.objectValue = objectValue;
        }
    }

    protected static class ListTypesClass {

        protected List<String> stringValues;
        protected List<Boolean> booleanValues;
        protected List<Integer> integerValues;
        protected List<Long> longValues;
        protected List<StringBuffer> objectValues;

        public ListTypesClass(@Property("strings") List<String> stringValues,
                              @Property("booleans") List<Boolean> booleanValues,
                              @Property("integers") List<Integer> integerValues,
                              @Property("longs") List<Long> longValues,
                              @Property("objects") List<StringBuffer> objectValues) {
            this.stringValues = stringValues;
            this.booleanValues = booleanValues;
            this.integerValues = integerValues;
            this.longValues = longValues;
            this.objectValues = objectValues;
        }
    }
}
