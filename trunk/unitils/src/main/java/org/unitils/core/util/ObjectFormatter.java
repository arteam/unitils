/*
 * Copyright Unitils.org
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
package org.unitils.core.util;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static org.apache.commons.lang.ClassUtils.getShortClassName;
import static org.unitils.reflectionassert.util.HibernateUtil.getUnproxiedValue;


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

    public static final String MOCK_NAME_CHAIN_SEPARATOR = "##chained##";

    /* The maximum recursion depth */
    protected int maxDepth;
    /* The maximum nr of elements for arrays and collections to display */
    protected int maxNrArrayOrCollectionElements;

    protected ArrayAndCollectionFormatter arrayAndCollectionFormatter;

    /**
     * Creates a formatter with a maximum recursion depth of 3.
     */
    public ObjectFormatter() {
        this(3, 15);
    }


    /**
     * Creates a formatter with the given maximum recursion depth.
     * <p/>
     * NOTE: there is no cycle detection. A large max depth value can cause lots of output in case of a cycle.
     *
     * @param maxDepth                       The max depth > 0
     * @param maxNrArrayOrCollectionElements The maximum nr of elements for arrays and collections to display  > 0
     */
    public ObjectFormatter(int maxDepth, int maxNrArrayOrCollectionElements) {
        this.maxDepth = maxDepth;
        this.maxNrArrayOrCollectionElements = maxNrArrayOrCollectionElements;
        this.arrayAndCollectionFormatter = new ArrayAndCollectionFormatter(maxNrArrayOrCollectionElements, this);
    }


    /**
     * Gets the string representation of the given object.
     *
     * @param object The instance
     * @return The string representation, not null
     */
    public String format(Object object) {
        StringBuilder result = new StringBuilder();
        formatImpl(object, 0, result);
        return result.toString();
    }


    /**
     * Actual implementation of the formatting.
     *
     * @param object       The instance
     * @param currentDepth The current recursion depth
     * @param result       The builder to append the result to, not null
     */
    protected void formatImpl(Object object, int currentDepth, StringBuilder result) {
        // get the actual value if the value is wrapped by a Hibernate proxy
        object = getUnproxiedValue(object);

        if (object == null) {
            result.append(String.valueOf(object));
            return;
        }
        if (formatString(object, result)) {
            return;
        }
        if (formatNumberOrDate(object, result)) {
            return;
        }
        Class<?> type = object.getClass();
        if (formatCharacter(object, type, result)) {
            return;
        }
        if (formatPrimitiveOrEnum(object, type, result)) {
            return;
        }
        if (formatMock(object, result)) {
            return;
        }
        if (formatProxy(object, type, result)) {
            return;
        }
        if (formatJavaLang(object, result, type)) {
            return;
        }
        if (type.isArray()) {
            arrayAndCollectionFormatter.formatArray(object, currentDepth, result);
            return;
        }
        if (object instanceof Collection) {
            arrayAndCollectionFormatter.formatCollection((Collection<?>) object, currentDepth, result);
            return;
        }
        if (object instanceof Map) {
            arrayAndCollectionFormatter.formatMap((Map<?, ?>) object, currentDepth, result);
            return;
        }
        if (currentDepth >= maxDepth) {
            result.append(getShortClassName(type));
            result.append("<...>");
            return;
        }
        if (formatFile(object, result)) {
            return;
        }
        formatObject(object, currentDepth, result);
    }


    protected boolean formatJavaLang(Object object, StringBuilder result, Class<?> type) {
        if (type.getName().startsWith("java.lang")) {
            result.append(String.valueOf(object));
            return true;
        }
        return false;
    }

    protected boolean formatPrimitiveOrEnum(Object object, Class<?> type, StringBuilder result) {
        if (type.isPrimitive() || type.isEnum()) {
            result.append(String.valueOf(object));
            return true;
        }
        return false;
    }

    protected boolean formatCharacter(Object object, Class<?> type, StringBuilder result) {
        if (object instanceof Character || Character.TYPE.equals(type)) {
            result.append('\'');
            result.append(String.valueOf(object));
            result.append('\'');
            return true;
        }
        return false;
    }

    protected boolean formatNumberOrDate(Object object, StringBuilder result) {
        if (object instanceof Number || object instanceof Date) {
            result.append(String.valueOf(object));
            return true;
        }
        return false;
    }

    protected boolean formatString(Object object, StringBuilder result) {
        if (object instanceof String) {
            result.append('"');
            result.append(object);
            result.append('"');
            return true;
        }
        return false;
    }


    /**
     * Formats the given object by formatting the inner fields.
     *
     * @param object       The object, not null
     * @param currentDepth The current recursion depth
     * @param result       The builder to append the result to, not null
     */
    protected void formatObject(Object object, int currentDepth, StringBuilder result) {
        Class<?> type = object.getClass();
        result.append(getShortClassName(type));
        result.append("<");
        formatFields(object, type, currentDepth, result);
        result.append(">");
    }


    /**
     * Formats the field values of the given object.
     *
     * @param object       The object, not null
     * @param clazz        The class for which to format the fields, not null
     * @param currentDepth The current recursion depth
     * @param result       The builder to append the result to, not null
     */
    protected void formatFields(Object object, Class<?> clazz, int currentDepth, StringBuilder result) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (int i = 0; i < fields.length; i++) {
            // skip transient and static fields
            Field field = fields[i];
            if (isTransient(field.getModifiers()) || isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            try {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(field.getName());
                result.append("=");
                formatImpl(field.get(object), currentDepth + 1, result);

            } catch (IllegalAccessException e) {
                // this can't happen. Would get a Security exception instead
                // throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }
        }

        // format fields declared in superclass
        Class<?> superclazz = clazz.getSuperclass();
        while (superclazz != null && !superclazz.getName().startsWith("java.lang")) {
            formatFields(object, superclazz, currentDepth, result);
            superclazz = superclazz.getSuperclass();
        }
    }


    protected boolean formatMock(Object object, StringBuilder result) {
        try {
            Class<?> proxyUtilsClass = getProxyUtilsClass();
            if (proxyUtilsClass == null) {
                return false;
            }
            String mockName = (String) proxyUtilsClass.getMethod("getMockName", Object.class).invoke(null, object);
            if (mockName == null) {
                return false;
            }
            mockName = mockName.replaceAll(MOCK_NAME_CHAIN_SEPARATOR, ".");
            if (isDummy(object)) {
                result.append("Dummy<");
            } else {
                result.append("Mock<");
            }
            result.append(mockName);
            result.append(">");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isDummy(Object object) {
        Class<?> clazz = object.getClass();
        Class<?> dummyObjectClass = getDummyObjectClass();
        return dummyObjectClass != null && dummyObjectClass.isAssignableFrom(clazz);
    }


    protected boolean formatProxy(Object object, Class<?> type, StringBuilder result) {
        if (Proxy.isProxyClass(type)) {
            result.append("Proxy<?>");
            return true;
        }
        String className = getShortClassName(object.getClass());
        int index = className.indexOf("..EnhancerByCGLIB..");
        if (index > 0) {
            result.append("Proxy<");
            result.append(className.substring(0, index));
            result.append(">");
            return true;
        }
        return false;
    }

    protected boolean formatFile(Object object, StringBuilder result) {
        if (object instanceof File) {
            result.append("File<");
            result.append(((File) object).getPath());
            result.append(">");
            return true;
        }
        return false;
    }

    /**
     * @return The interface that represents a dummy object. If the DummyObject interface is not in the
     *         classpath, null is returned.
     */
    protected Class<?> getDummyObjectClass() {
        try {
            return Class.forName("org.unitils.mock.dummy.DummyObject");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    /**
     * @return The proxy utils. null if not in classpath
     */
    protected Class<?> getProxyUtilsClass() {
        try {
            return Class.forName("org.unitils.mock.core.proxy.ProxyUtils");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
