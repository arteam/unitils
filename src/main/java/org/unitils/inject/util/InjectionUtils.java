/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.inject.util;

import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;
import static org.unitils.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Class containing static methods that implement explicit injection using OGNL expressions, and auto-injection by type.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectionUtils {

    /* The logger instance for this class */
    private static final Logger logger = Logger.getLogger(InjectionUtils.class);

    /**
     * Explicit injection of the objectToInject into the specified property of the target. The property should be a
     * correct OGNL expression.
     *
     * @param objectToInject The object that is injected
     * @param target         The target object, not null
     * @param property       The OGNL expression that defines where the object will be injected, not null
     * @return The object that was replaced by the injection
     */
    public static Object inject(Object objectToInject, Object target, String property) {
        try {
            OgnlContext ognlContext = new OgnlContext();
            ognlContext.setMemberAccess(new DefaultMemberAccess(true));
            Object ognlExpression = Ognl.parseExpression(property);

            Object oldValue = null;
            try {
                Ognl.getValue(ognlExpression, ognlContext, target);

            } catch (Exception e) {
                logger.warn("Unable to retrieve current value of field to inject into. Will not be able to restore value after injection.", e);
            }
            Ognl.setValue(ognlExpression, ognlContext, target, objectToInject);
            return oldValue;

        } catch (OgnlException e) {
            throw new UnitilsException("Failed to set value using OGNL expression " + property, e);
        }
    }

    /**
     * Explicit injection of the objectToInject into the specified static property of the target class. The property
     * should be a correct OGNL expression.
     *
     * @param objectToInject The object that is injected
     * @param targetClass    The target class, not null
     * @param property       The OGNL expression that defines where the object will be injected, not null
     * @return The object that was replaced by the injection
     */
    public static Object injectStatic(Object objectToInject, Class targetClass, String property) {
        String staticProperty = StringUtils.substringBefore(property, ".");
        if (property.equals(staticProperty)) {
            // Simple property: directly set value on this property
            Object oldValue = null;
            try {
                oldValue = getValueStatic(targetClass, staticProperty);

            } catch (Exception e) {
                logger.warn("Unable to retrieve current value of field to inject into. Will not be able to restore value after injection.", e);
            }
            setValueStatic(targetClass, staticProperty, objectToInject);
            return oldValue;

        } else {
            // Multipart property: use ognl for remaining property part
            Object objectToInjectInto = getValueStatic(targetClass, staticProperty);
            String remainingPropertyPart = StringUtils.substringAfter(property, ".");
            try {
                return inject(objectToInject, objectToInjectInto, remainingPropertyPart);

            } catch (UnitilsException e) {
                throw new UnitilsException("Property named " + remainingPropertyPart + " not found on " + objectToInjectInto.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * Performs auto-injection by type of the objectToInject on the target object.
     *
     * @param objectToInject     The object that is injected
     * @param objectToInjectType The type of the object. This should be the type of the object or one of his super-types
     *                           or implemented interfaces. This type is used for property type matching on the target object
     * @param target             The object into which the objectToInject is injected
     * @param propertyAccess Defines if field or setter injection is used
     * @return The object that was replaced by the injection
     */
    public static Object autoInject(Object objectToInject, Class objectToInjectType, Object target, PropertyAccess propertyAccess) {
        if (propertyAccess == PropertyAccess.FIELD) {
            return autoInjectToField(objectToInject, objectToInjectType, target, target.getClass(), false);
        }
        return autoInjectToSetter(objectToInject, objectToInjectType, target, target.getClass(), false);
    }

    /**
     * Performs auto-injection by type of the objectToInject into the target class.
     *
     * @param objectToInject     The object that is injected
     * @param objectToInjectType The type of the object. This should be the type of the object or one of his super-types
     *                           or implemented interfaces. This type is used for property type matching on the target class
     * @param targetClass        The class into which the objectToInject is injected
     * @param propertyAccess Defines if field or setter injection is used
     * @return The object that was replaced by the injection
     */
    public static Object autoInjectStatic(Object objectToInject, Class objectToInjectType, Class targetClass, PropertyAccess propertyAccess) {
        if (propertyAccess == PropertyAccess.FIELD) {
            return autoInjectToField(objectToInject, objectToInjectType, null, targetClass, true);
        }
        return autoInjectToSetter(objectToInject, objectToInjectType, null, targetClass, true);
    }

    /**
     * Performs auto-injection on a field by type of the objectToInject into the given target object or targetClass,
     * depending on the value of isStatic. The object is injected on one single field, if there is more than one
     * candidate field, a {@link UnitilsException} is thrown. We try to inject the object on the most specific field,
     * this means that when there are muliple fields of one of the super-types or implemented interfaces of the field,
     * the one that is lowest in the hierarchy is chosen (if possible, otherwise, a {@link UnitilsException} is thrown.
     *
     * @param objectToInject     The object that is injected
     * @param objectToInjectType The type of the object that is injected
     * @param target             The target object (only used when isStatic is false)
     * @param targetClass        The target class (only used when isStatis is true)
     * @param isStatic           Indicates wether injection should be performed on the target object or on the target class
     * @return The object that was replaced by the injection
     */
    private static Object autoInjectToField(Object objectToInject, Class objectToInjectType, Object target, Class targetClass, boolean isStatic) {

        // Try to find a field with an exact matching type
        Field fieldToInjectTo = null;
        List<Field> fieldsWithExactType = ReflectionUtils.getFieldsOfType(targetClass, objectToInjectType, isStatic);
        if (fieldsWithExactType.size() > 1) {
            throw new UnitilsException("More than one " + (isStatic ? "static " : "") + "field with exact type " + objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());

        } else if (fieldsWithExactType.size() == 1) {
            fieldToInjectTo = fieldsWithExactType.get(0);

        } else {
            // Try to find a supertype field:
            // If one field exist that has a type which is more specific than all other fields of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Field> fieldsOfType = getFieldsAssignableFrom(targetClass, objectToInjectType, isStatic);
            if (fieldsOfType.size() == 0) {
                throw new UnitilsException("No " + (isStatic ? "static " : "") + "field with (super)type " + objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());
            }
            for (Field field : fieldsOfType) {
                boolean moreSpecific = true;
                for (Field compareToField : fieldsOfType) {
                    if (field != compareToField) {
                        if (!compareToField.getClass().isAssignableFrom(field.getClass())) {
                            moreSpecific = false;
                            break;
                        }
                    }
                }
                if (moreSpecific) {
                    fieldToInjectTo = field;
                    break;
                }
            }
            if (fieldToInjectTo == null) {
                throw new UnitilsException("Multiple candidate target " + (isStatic ? "static " : "") + "fields found in " + target.getClass().getSimpleName() +
                        ", with none of them more specific than all others: " + StringUtils.join(fieldsOfType.iterator(), ", "));
            }
        }

        // Field to inject into found, inject the object and return old value
        Object oldValue = null;
        try {
            oldValue = getFieldValue(target, fieldToInjectTo);

        } catch (Exception e) {
            logger.warn("Unable to retrieve current value of field to inject into. Will not be able to restore value after injection.", e);
        }
        setFieldValue(target, fieldToInjectTo, objectToInject);
        return oldValue;
    }

    /**
     * Performs auto-injection on a setter by type of the objectToInject into the given target object or targetClass,
     * depending on the value of isStatic. The object is injected to one single setter, if there is more than one
     * candidate setter, a {@link UnitilsException} is thrown. We try to inject the object on the most specific type,
     * this means that when there are muliple setters for one of the super-types or implemented interfaces of the setter
     * type, the one that is lowest in the hierarchy is chosen (if possible, otherwise, a {@link UnitilsException} is
     * thrown.
     *
     * @param objectToInject     The object that is injected
     * @param objectToInjectType The type of the object that is injected
     * @param target             The target object (only used when isStatic is false)
     * @param targetClass        The target class (only used when isStatis is true)
     * @param isStatic           Indicates wether injection should be performed on the target object or on the target class
     * @return The object that was replaced by the injection
     */
    private static Object autoInjectToSetter(Object objectToInject, Class objectToInjectType, Object target, Class targetClass, boolean isStatic) {

        // Try to find a method with an exact matching type
        Method setterToInjectTo = null;
        List<Method> settersWithExactType = getSettersOfType(targetClass, objectToInjectType, isStatic);
        if (settersWithExactType.size() > 1) {
            throw new UnitilsException("More than one " + (isStatic ? "static " : "") + "setter with exact type " + objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());

        } else if (settersWithExactType.size() == 1) {
            setterToInjectTo = settersWithExactType.get(0);

        } else {
            // Try to find a supertype setter:
            // If one setter exist that has a type which is more specific than all other setters of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Method> settersOfType = getSettersAssignableFrom(targetClass, objectToInjectType, isStatic);
            if (settersOfType.size() == 0) {
                throw new UnitilsException("No " + (isStatic ? "static " : "") + "setter with (super)type " + objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());
            }
            for (Method setter : settersOfType) {
                boolean moreSpecific = true;
                for (Method compareToSetter : settersOfType) {
                    if (setter != compareToSetter) {
                        if (!compareToSetter.getClass().isAssignableFrom(setter.getClass())) {
                            moreSpecific = false;
                            break;
                        }
                    }
                }
                if (moreSpecific) {
                    setterToInjectTo = setter;
                    break;
                }
            }
            if (setterToInjectTo == null) {
                throw new UnitilsException("Multiple candidate target " + (isStatic ? "static " : "") + " setters found in " + targetClass.getSimpleName() +
                        ", with none of them more specific than all others: " + StringUtils.join(settersOfType.iterator(), ", "));
            }
        }

        // Setter to inject into found, inject the object and return old value
        Object oldValue = null;
        try {
            Method getter = getGetter(setterToInjectTo);
            if (getter == null) {
                logger.warn("Unable to retrieve current value of field to inject into, no getter found for setter: " + setterToInjectTo + ". Will not be able to restore value after injection.");
            } else {
                oldValue = invokeMethod(target, getter);
            }
        } catch (Exception e) {
            logger.warn("Unable to retrieve current value of field to inject into. Will not be able to restore value after injection.", e);
        }

        try {
            invokeMethod(target, setterToInjectTo, objectToInject);

        } catch (InvocationTargetException e) {
            throw new UnitilsException("Unable to inject to setter, exception thrown by target.", e);
        }
        return oldValue;
    }

    /**
     * Retrieves the value of the static property from the given class
     *
     * @param targetClass    The class from which the static property value is retrieved
     * @param staticProperty The name of the property (simple name, not a composite expression)
     * @return The value of the static property from the given class
     */
    private static Object getValueStatic(Class targetClass, String staticProperty) {

        Method staticGetter = getGetter(targetClass, staticProperty, true);
        if (staticGetter != null) {
            try {
                return invokeMethod(targetClass, staticGetter);

            } catch (InvocationTargetException e) {
                throw new UnitilsException("Exception thrown by target", e);
            }
        } else {
            Field staticField = getFieldWithName(targetClass, staticProperty, true);
            if (staticField != null) {
                return getFieldValue(targetClass, staticField);
            } else {
                throw new UnitilsException("Static property named " + staticProperty + " not found on class " + targetClass.getSimpleName());
            }
        }
    }

    /**
     * Sets the given value on the static property of the given targetClass
     *
     * @param targetClass    The class on which the static property value should be set
     * @param staticProperty The name of the property (simple name, not a composite expression)
     * @param value          The value to set
     */
    private static void setValueStatic(Class targetClass, String staticProperty, Object value) {
        Method staticSetter = ReflectionUtils.getSetter(targetClass, staticProperty, true);
        if (staticSetter != null) {
            try {
                invokeMethod(targetClass, staticSetter, value);

            } catch (InvocationTargetException e) {
                throw new UnitilsException("Exception thrown by target", e);
            }
        } else {
            Field staticField = getFieldWithName(targetClass, staticProperty, true);
            if (staticField == null) {
                throw new UnitilsException("Static property named " + staticProperty + " not found on class " + targetClass.getSimpleName());
            }
            setFieldValue(targetClass, staticField, value);
        }
    }
}
