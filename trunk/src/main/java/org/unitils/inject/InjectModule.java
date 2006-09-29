package org.unitils.inject;


import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.TestContext;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsModule;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.*;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * todo javadoc
 */
public class InjectModule implements UnitilsModule {


    public TestListener createTestListener() {
        return new InjectTestListener();
    }

    private class InjectTestListener extends TestListener {
        public void beforeTestMethod() {
            injectObjects(TestContext.getTestObject());
        }
    }

    void injectObjects(Object test) {
        injectAll(test);
        autoInjectAll(test);
        injectAllStatic(test);
        autoInjectAllStatic(test);
    }

    private void injectAll(Object test) {
        List<Field> fieldsToInject = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), Inject.class);
        for (Field fieldToInject : fieldsToInject) {
            inject(test, fieldToInject);
        }
    }

    private void autoInjectAll(Object test) {
        List<Field> fieldsToAutoInject = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), AutoInject.class);
        for (Field fieldToAutoInject : fieldsToAutoInject) {
            autoInject(test, fieldToAutoInject);
        }
    }

    private void injectAllStatic(Object test) {
        List<Field> fieldsToInjectStatic = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), InjectStatic.class);
        for (Field fieldToInjectStatic : fieldsToInjectStatic) {
            injectStatic(test, fieldToInjectStatic);
        }
    }

    private void autoInjectAllStatic(Object test) {
        List<Field> fieldsToAutoInjectStatic = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), AutoInjectStatic.class);
        for (Field fieldToAutoInjectStatic : fieldsToAutoInjectStatic) {
            autoInjectStatic(test, fieldToAutoInjectStatic);
        }
    }

    private void inject(Object test, Field fieldToInject) {
        Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);

        List targets = getTargets(injectAnnotation, fieldToInject, injectAnnotation.target(), test);
        String property = injectAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, "Property cannot be empty"));
        }
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);

        try {
            for (Object target : targets) {
                setValueUsingOgnl(target, property, objectToInject);
            }
        } catch (OgnlException e) {
            throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, "Property could not be parsed"), e);
        }
    }

    private void injectStatic(Object test, Field fieldToInjectStatic) {
        InjectStatic injectStaticAnnotation = fieldToInjectStatic.getAnnotation(InjectStatic.class);

        Class targetClass = injectStaticAnnotation.target();
        String property = injectStaticAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic, "Property cannot be empty"));
        }
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInjectStatic);

        String staticProperty = StringUtils.substringBefore(property, ".");
        if (property.equals(staticProperty)) {
            // Simple property: directly set value on this property
            boolean succeeded = setValueStatic(targetClass, staticProperty, objectToInject);
            if (!succeeded) {
                throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic,
                        "Static property named " + property + " not found on " + objectToInject.getClass().getSimpleName()));
            }
        } else {
            // Multipart property: use ognl for remaining property part
            Object objectToInjectInto = getValueStatic(targetClass, staticProperty);
            String remainingPropertyPart = StringUtils.substringAfter(property, ".");
            try {
                setValueUsingOgnl(objectToInjectInto, remainingPropertyPart, objectToInject);
            } catch (OgnlException e) {
                throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic,
                        "Property named " + remainingPropertyPart + " not found on " + objectToInjectInto.getClass().getSimpleName()));
            }
        }
    }

    private void autoInject(Object test, Field fieldToInject) {
        AutoInject autoInjectAnnotation = fieldToInject.getAnnotation(AutoInject.class);

        List targets = getTargets(autoInjectAnnotation, fieldToInject, autoInjectAnnotation.target(), test);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);

        AutoInject.PropertyAccessType propertyAccessType = AnnotationUtils.getValueReplaceDefault(autoInjectAnnotation.propertyAccessType());

        for (Object target : targets) {
            if (propertyAccessType == AutoInject.PropertyAccessType.FIELD) {
                autoInjectToField(autoInjectAnnotation, target, target.getClass(), fieldToInject, objectToInject, false);
            } else {
                autoInjectToSetter(autoInjectAnnotation, target, target.getClass(), fieldToInject, objectToInject, false);
            }
        }
    }

    private void autoInjectStatic(Object test, Field fieldToAutoInjectStatic) {
        AutoInjectStatic autoInjectStaticAnnotation = fieldToAutoInjectStatic.getAnnotation(AutoInjectStatic.class);

        Class targetClass = autoInjectStaticAnnotation.target();
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToAutoInjectStatic);

        AutoInjectStatic.PropertyAccessType propertyAccessType = AnnotationUtils.getValueReplaceDefault(
                autoInjectStaticAnnotation.propertyAccessType());

        if (propertyAccessType == AutoInjectStatic.PropertyAccessType.FIELD) {
            autoInjectToField(autoInjectStaticAnnotation, null, targetClass, fieldToAutoInjectStatic, objectToInject, true);
        } else {
            autoInjectToSetter(autoInjectStaticAnnotation, null, targetClass, fieldToAutoInjectStatic, objectToInject, true);
        }

    }

    private void autoInjectToField(Annotation autoInjectAnnotation, Object target, Class targetClass, Field fieldToInject, Object objectToInject, boolean isStatic) {

        // Try to find a field with an exact matching type
        Field fieldToInjectTo = ReflectionUtils.getFieldOfType(targetClass, fieldToInject.getType(), isStatic);
        if (fieldToInjectTo == null) {
            // Try to find a supertype field:
            // If one field exist that has a type which is more specific than all other fields of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Field> fieldsOfType = ReflectionUtils.getFieldsAssignableFrom(targetClass, fieldToInject.getType(), isStatic);
            if (fieldsOfType.size() == 0) {
                throw new UnitilsException(getSituatedErrorMessage(autoInjectAnnotation, fieldToInject,
                        "No " + (isStatic?"static ":"") + "field with (super)type " + fieldToInject.getClass().getSimpleName() +
                        " found in " + targetClass.getSimpleName()));
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
                throw new UnitilsException(getSituatedErrorMessage(autoInjectAnnotation, fieldToInject,
                        "Multiple candidate target " + (isStatic?"static ":"") + "fields found in " + target.getClass().getSimpleName() +
                        ", with none of them more specific than all others: " + StringUtils.join(fieldsOfType.iterator(), ", ")));
            }
        }
        // Field to inject into found, inject the object
        ReflectionUtils.setFieldValue(target, fieldToInjectTo, objectToInject);
    }

    private void autoInjectToSetter(Annotation autoInjectAnnotation, Object target, Class targetClass, Field fieldToInject, Object objectToInject, boolean isStatic) {
        // Try to find a method with an exact matching type
        Method setterToInjectTo = ReflectionUtils.getSetterOfType(targetClass, fieldToInject.getType(), false);

        if (setterToInjectTo == null) {
            // Try to find a supertype setter:
            // If one setter exist that has a type which is more specific than all other setters of the given type,
            // this one is taken. Otherwise, an exception is thrown
            List<Method> settersOfType = ReflectionUtils.getSettersAssignableFrom(targetClass, fieldToInject.getType(), false);
            if (settersOfType.size() == 0) {
                throw new UnitilsException(getSituatedErrorMessage(autoInjectAnnotation, fieldToInject,
                    "No " + (isStatic?"static ":"") + "setter with (super)type " + fieldToInject.getClass().getSimpleName() +
                    " found in " + targetClass.getSimpleName()));
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
                throw new UnitilsException(getSituatedErrorMessage(autoInjectAnnotation, fieldToInject,
                    "Multiple candidate target " + (isStatic?"static ":"") + " setters found in " + targetClass.getSimpleName() +
                    ", with none of them more specific than all others: " + StringUtils.join(settersOfType.iterator(), ", ")));
            }
        }
        // Setter to inject into found, inject the object
        ReflectionUtils.invokeMethod(target, setterToInjectTo, objectToInject);
    }

    private Object getValueStatic(Class targetClass, String staticProperty) {
        Method staticGetter = ReflectionUtils.getGetter(staticProperty, targetClass, true);
        if (staticGetter != null) {
            return ReflectionUtils.invokeMethod(targetClass, staticGetter);
        } else {
            Field staticField = ReflectionUtils.getFieldWithName(staticProperty, targetClass, true);
            return ReflectionUtils.getFieldValue(targetClass, staticField);
        }
    }

    private boolean setValueStatic(Class targetClass, String staticProperty, Object value) {
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

    private List getTargets(Annotation annotation, Field annotatedField, String targetName, Object test) {
        List targets;
        if ("".equals(targetName)) {
            // Default targetName, so it is probably not specfied. Return all objects that are annotated with the
            // TestedObject annotation.
            targets = AnnotationUtils.getFieldValuesAnnotatedWith(test, TestedObject.class);
        } else {
            Field field = ReflectionUtils.getFieldWithName(targetName, test.getClass(), false);
            if (field == null) {
                throw new UnitilsException(getSituatedErrorMessage(annotation, annotatedField, "Target with name " + targetName + " does not exist"));
            }
            Object target = ReflectionUtils.getFieldValue(test, field);
            targets = Collections.singletonList(target);
        }
        return targets;
    }

    private void setValueUsingOgnl(Object target, String ognlExprStr, Object objectToInject) throws OgnlException {
        OgnlContext ognlContext = new OgnlContext();
        ognlContext.setMemberAccess(new DefaultMemberAccess(true));
        Object ognlExpression = Ognl.parseExpression(ognlExprStr);
        Ognl.setValue(ognlExpression, ognlContext, target, objectToInject);
    }

    private String getSituatedErrorMessage(Annotation processedAnnotation, Field annotatedField, String errorDescription) {
        return "Error while processing @" + processedAnnotation.getClass().getSimpleName() + " annotation on field " +
                annotatedField.getName() + ": " + errorDescription;
    }



}
