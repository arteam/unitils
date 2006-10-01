package org.unitils.inject;

import ognl.OgnlException;
import ognl.OgnlContext;
import ognl.DefaultMemberAccess;
import ognl.Ognl;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Filip Neven
 */
public class InjectionUtils {

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

    public static void autoInject(Object objectToInject, Class objectToInjectType, Object target, PropertyAccessType propertyAccessType) {
        if (propertyAccessType == PropertyAccessType.FIELD) {
            autoInjectToField(objectToInject, objectToInjectType, target, target.getClass(), false);
        } else {
            autoInjectToSetter(objectToInject, objectToInjectType, target, target.getClass(), false);
        }
    }

    public static void autoInjectStatic(Object objectToInject, Class objectToInjectType, Class targetClass, PropertyAccessType propertyAccessType) {
        if (propertyAccessType == PropertyAccessType.FIELD) {
            autoInjectToField(objectToInject, objectToInjectType, null, targetClass, true);
        } else {
            autoInjectToSetter(objectToInject, objectToInjectType, null, targetClass, true);
        }
    }

    private static void autoInjectToField(Object objectToInject, Class objectToInjectType, Object target, Class targetClass, boolean isStatic) {
        // Try to find a field with an exact matching type
        Field fieldToInjectTo = ReflectionUtils.getFieldOfType(targetClass, objectToInjectType, isStatic);
        if (fieldToInjectTo == null) {
            // Try to find a supertype field:
            // If one field exist that has a type which is more specific than all other fields of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Field> fieldsOfType = ReflectionUtils.getFieldsAssignableFrom(targetClass, objectToInjectType, isStatic);
            if (fieldsOfType.size() == 0) {
                throw new UnitilsException("No " + (isStatic?"static ":"") + "field with (super)type " + objectToInjectType.getSimpleName() +
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
                throw new UnitilsException("Multiple candidate target " + (isStatic?"static ":"") + "fields found in " +
                        target.getClass().getSimpleName() + ", with none of them more specific than all others: " +
                        StringUtils.join(fieldsOfType.iterator(), ", "));
            }
        }
        // Field to inject into found, inject the object
        ReflectionUtils.setFieldValue(target, fieldToInjectTo, objectToInject);
    }

    private static void autoInjectToSetter(Object objectToInject, Class objectToInjectType, Object target, Class targetClass, boolean isStatic) {
        // Try to find a method with an exact matching type
        Method setterToInjectTo = ReflectionUtils.getSetterOfType(targetClass, objectToInjectType, false);

        if (setterToInjectTo == null) {
            // Try to find a supertype setter:
            // If one setter exist that has a type which is more specific than all other setters of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Method> settersOfType = ReflectionUtils.getSettersAssignableFrom(targetClass, objectToInjectType, false);
            if (settersOfType.size() == 0) {
                throw new UnitilsException("No " + (isStatic?"static ":"") + "setter with (super)type " +
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
                throw new UnitilsException("Multiple candidate target " + (isStatic?"static ":"") + " setters found in " +
                    targetClass.getSimpleName() +", with none of them more specific than all others: " +
                        StringUtils.join(settersOfType.iterator(), ", "));
            }
        }
        // Setter to inject into found, inject the object
        ReflectionUtils.invokeMethod(target, setterToInjectTo, objectToInject);
    }

    private static Object getValueStatic(Class targetClass, String staticProperty) {
        Method staticGetter = ReflectionUtils.getGetter(staticProperty, targetClass, true);
        if (staticGetter != null) {
            return ReflectionUtils.invokeMethod(targetClass, staticGetter);
        } else {
            Field staticField = ReflectionUtils.getFieldWithName(staticProperty, targetClass, true);
            return ReflectionUtils.getFieldValue(targetClass, staticField);
        }
    }

    private static boolean setValueStatic(Class targetClass, String staticProperty, Object value) {
        Method staticSetter = ReflectionUtils.getSetter(staticProperty, targetClass, value.getClass(), true);
        if (staticSetter != null) {
            ReflectionUtils.invokeMethod(targetClass, staticSetter, value);
            return true;
        } else {
            Field staticField = ReflectionUtils.getFieldWithName(staticProperty, targetClass, true);
            if (staticField != null) {
                ReflectionUtils.setFieldValue(targetClass, staticField, value);
                return true;
            } else {
                return false;
            }
        }
    }
}
