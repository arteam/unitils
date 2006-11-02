/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.inject.util;

import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * General utility class that implements explicit injection using OGNL expressions, or auto-injection by type.
 */
public class InjectionUtils {

    /**
     * Explicit injection of the objectToInject into the specified property of the target. The property should be a
     * correct OGNL expression.
     *
     * @param objectToInject The object that is injected
     * @param target         The target object
     * @param property       The OGNL expression that defines where the object will be injected
     */
    public static void inject(Object objectToInject, Object target, String property) {
        try {
            OgnlContext ognlContext = new OgnlContext();
            ognlContext.setMemberAccess(new DefaultMemberAccess(true));
            Object ognlExpression = Ognl.parseExpression(property);
            Ognl.setValue(ognlExpression, ognlContext, target, objectToInject);
        } catch (OgnlException e) {
            throw new UnitilsException("Failed to set value using OGNL expression " + property, e);
        }
    }

    /**
     * Explicit injection of the objectToInject into the specified static property of the target class. The property
     * should be a correct OGNL expression.
     *
     * @param objectToInject
     * @param targetClass
     * @param property
     */
    public static void injectStatic(Object objectToInject, Class targetClass, String property) {
        String staticProperty = StringUtils.substringBefore(property, ".");
        if (property.equals(staticProperty)) {
            // Simple property: directly set value on this property
            boolean succeeded = setValueStatic(targetClass, staticProperty, objectToInject);
            if (!succeeded) {
                throw new UnitilsException("Static property named " + property + " not found on " + objectToInject.getClass().getSimpleName());
            }
        } else {
            // Multipart property: use ognl for remaining property part
            Object objectToInjectInto = getValueStatic(targetClass, staticProperty);
            String remainingPropertyPart = StringUtils.substringAfter(property, ".");
            try {
                inject(objectToInject, objectToInjectInto, remainingPropertyPart);
            } catch (UnitilsException e) {
                throw new UnitilsException("Property named " + remainingPropertyPart + " not found on " +
                        objectToInjectInto.getClass().getSimpleName(), e);
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
     * @param propertyAccessType Defines if field or setter injection is used
     */
    public static void autoInject(Object objectToInject, Class objectToInjectType, Object target, PropertyAccessType propertyAccessType) {
        if (propertyAccessType == PropertyAccessType.FIELD) {
            autoInjectToField(objectToInject, objectToInjectType, target, target.getClass(), false);
        } else {
            autoInjectToSetter(objectToInject, objectToInjectType, target, target.getClass(), false);
        }
    }

    /**
     * Performs auto-injection by type of the objectToInject into the target class.
     *
     * @param objectToInject     The object that is injected
     * @param objectToInjectType The type of the object. This should be the type of the object or one of his super-types
     *                           or implemented interfaces. This type is used for property type matching on the target class
     * @param targetClass        The class into which the objectToInject is injected
     * @param propertyAccessType Defines if field or setter injection is used
     */
    public static void autoInjectStatic(Object objectToInject, Class objectToInjectType, Class targetClass, PropertyAccessType propertyAccessType) {
        if (propertyAccessType == PropertyAccessType.FIELD) {
            autoInjectToField(objectToInject, objectToInjectType, null, targetClass, true);
        } else {
            autoInjectToSetter(objectToInject, objectToInjectType, null, targetClass, true);
        }
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
     */
    private static void autoInjectToField(Object objectToInject, Class objectToInjectType, Object target, Class targetClass, boolean isStatic) {

        // Try to find a field with an exact matching type
        Field fieldToInjectTo = null;
        List<Field> fieldsWithExactType = ReflectionUtils.getFieldsOfType(targetClass, objectToInjectType, isStatic);
        if (fieldsWithExactType.size() > 1) {
            throw new UnitilsException("More than one " + (isStatic ? "static " : "") + "field with exact type " +
                    objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());
        } else if (fieldsWithExactType.size() == 1) {
            fieldToInjectTo = fieldsWithExactType.get(0);
        } else {
            // Try to find a supertype field:
            // If one field exist that has a type which is more specific than all other fields of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Field> fieldsOfType = ReflectionUtils.getFieldsAssignableFrom(targetClass, objectToInjectType, isStatic);
            if (fieldsOfType.size() == 0) {
                throw new UnitilsException("No " + (isStatic ? "static " : "") + "field with (super)type " + objectToInjectType.getSimpleName() +
                        " found in " + targetClass.getSimpleName());
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
                throw new UnitilsException("Multiple candidate target " + (isStatic ? "static " : "") + "fields found in " +
                        target.getClass().getSimpleName() + ", with none of them more specific than all others: " +
                        StringUtils.join(fieldsOfType.iterator(), ", "));
            }
        }
        // Field to inject into found, inject the object
        ReflectionUtils.setFieldValue(target, fieldToInjectTo, objectToInject);
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
     */
    private static void autoInjectToSetter(Object objectToInject, Class objectToInjectType, Object target, Class targetClass, boolean isStatic) {

        // Try to find a method with an exact matching type
        Method setterToInjectTo = null;
        List<Method> settersWithExactType = ReflectionUtils.getSettersOfType(targetClass, objectToInjectType, false);
        if (settersWithExactType.size() > 1) {
            throw new UnitilsException("More than one " + (isStatic ? "static " : "") + "setter with exact type " +
                    objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());
        } else if (settersWithExactType.size() == 1) {
            setterToInjectTo = settersWithExactType.get(0);
        } else {
            // Try to find a supertype setter:
            // If one setter exist that has a type which is more specific than all other setters of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Method> settersOfType = ReflectionUtils.getSettersAssignableFrom(targetClass, objectToInjectType, false);
            if (settersOfType.size() == 0) {
                throw new UnitilsException("No " + (isStatic ? "static " : "") + "setter with (super)type " +
                        objectToInjectType.getSimpleName() + " found in " + targetClass.getSimpleName());
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
                throw new UnitilsException("Multiple candidate target " + (isStatic ? "static " : "") + " setters found in " +
                        targetClass.getSimpleName() + ", with none of them more specific than all others: " +
                        StringUtils.join(settersOfType.iterator(), ", "));
            }
        }
        // Setter to inject into found, inject the object
        ReflectionUtils.invokeMethod(target, setterToInjectTo, objectToInject);
    }

    /**
     * Retrieves the value of the static property from the given class
     *
     * @param targetClass    the class from which the static property value is retrieved
     * @param staticProperty the name of the property (simple name, not a composite expression)
     * @return The value of the static property from the given class
     */
    private static Object getValueStatic(Class targetClass, String staticProperty) {

        Method staticGetter = ReflectionUtils.getGetter(targetClass, staticProperty, true);
        if (staticGetter != null) {
            return ReflectionUtils.invokeMethod(targetClass, staticGetter);
        } else {
            Field staticField = ReflectionUtils.getFieldWithName(targetClass, staticProperty, true);
            if (staticField != null) {
                return ReflectionUtils.getFieldValue(targetClass, staticField);
            } else {
                throw new UnitilsException("Static property named " + staticProperty + " not found on class " +
                        targetClass.getSimpleName());
            }
        }
    }

    /**
     * Sets the given value on the static property of the given targetClass
     *
     * @param targetClass
     * @param staticProperty
     * @param value
     * @return True if a static property with the given property name was found and the value could be set, false
     *         otherwise
     */
    private static boolean setValueStatic(Class targetClass, String staticProperty, Object value) {
        Method staticSetter = ReflectionUtils.getSetter(targetClass, staticProperty, true);
        if (staticSetter != null) {
            ReflectionUtils.invokeMethod(targetClass, staticSetter, value);
            return true;
        } else {
            Field staticField = ReflectionUtils.getFieldWithName(targetClass, staticProperty, true);
            if (staticField != null) {
                ReflectionUtils.setFieldValue(targetClass, staticField, value);
                return true;
            } else {
                return false;
            }
        }
    }
}
