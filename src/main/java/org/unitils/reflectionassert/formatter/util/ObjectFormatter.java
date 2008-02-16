/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.reflectionassert.formatter.util;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * A class for generating a string representation of any object, array or primitive value.
 * <p/>
 * Non-primitive objects are processed recursively so that a string representation of inner objects is also generated.
 * Too avoid too much output, this recursion is limited with a given maximum depth.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectFormatter {

    private int maxDepth;


    /**
     * Creates a formatter with a maximum recursion depth of 5.
     */
    public ObjectFormatter() {
        this(3);
    }


    /**
     * Creates a formatter with the given maximum recursion depth.
     * <p/>
     * NOTE: there is no cycle detection. A large max depth value can cause lots of output in case of a cycle.
     *
     * @param maxDepth The max depth > 0
     */
    public ObjectFormatter(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    /**
     * Gets the string representation of the given object.
     *
     * @param object The instance
     * @return The string representation, not null
     */
    public String format(Object object) {
        return formatImpl(object, 0);
    }


    /**
     * Actual implementation of the formatting.
     *
     * @param object       The instance
     * @param currentDepth The current recursion depth
     * @return The string representation, not null
     */
    protected String formatImpl(Object object, int currentDepth) {
        if (object == null) {
            return String.valueOf(object);
        }
        if (object instanceof String || object instanceof Number || object instanceof Character) {
            return String.valueOf(object);
        }
        Class type = object.getClass();
        if (type.isPrimitive() || type.isEnum()) {
            return String.valueOf(object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.toString((Object[]) object);
        }
        if (object instanceof Collection || object instanceof Map) {
            return object.toString();
        }
        if (currentDepth >= maxDepth) {
            return object.toString();
        }
        return new RecursiveReflectionToStringBuilder(object, currentDepth + 1).toString();
    }


    /**
     * A reflection to string builder that recursively handles inner objects.
     */
    protected class RecursiveReflectionToStringBuilder extends ReflectionToStringBuilder {

        /* The current recursion depth */
        private int currentDepth;


        /**
         * Creates a to string builder.
         *
         * @param object       The object to format
         * @param currentDepth The current recursion depth.
         */
        public RecursiveReflectionToStringBuilder(Object object, int currentDepth) {
            super(object, ToStringStyle.SHORT_PREFIX_STYLE);
            this.currentDepth = currentDepth;
        }


        /**
         * Overriden to recursively handle inner objects.
         *
         * @param field The field, not null
         * @return The value for the field, a formatted string in case of an object
         */
        protected Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException {
            Object fieldValue = super.getValue(field);
            if (fieldValue == null || fieldValue instanceof String) {
                return fieldValue;
            }

            // instead of the field value, this will return the formatted string of the field value
            return formatImpl(fieldValue, currentDepth);
        }
    }

}
