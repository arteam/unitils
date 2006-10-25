/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.*;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.inject.util.PropertyAccessType;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationEnumDefaults;
import static org.unitils.util.ModuleUtils.getValueReplaceDefault;
import org.unitils.util.ReflectionUtils;
import static org.unitils.util.ReflectionUtils.getFieldWithName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Module for injecting annotated objects into other objects. The intended usage is to inject mock objects, but it can
 * be used for regular objects too.
 * <p/>
 * Both explicit injection and automatic injection by type are supported. An object annotated with {@link Inject} is
 * explicitly injected into a target object. An object annotated with {@link AutoInject} is automatically injected into a
 * target property with the same type as the declared type of the annotated object.
 * <p/>
 * Explicit and automatic injection into static fields is also supported, by means of the {@link InjectStatic} and {@link
 * AutoInjectStatic} annotations.
 * <p/>
 * The target object can either be specified explicitly, or implicitly by annotating an object with {@link TestedObject}
 */
public class InjectModule implements Module {

    /**
     * Map holding the default configuration of the inject annotations
     */
    private Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> defaultEnumValues;

    /**
     * The core injection implementation
     */
    private InjectionUtils injectionUtils;

    /**
     * Initializes this module using the given <code>Configuration</code> object
     *
     * @param configuration
     */
    public void init(Configuration configuration) {

        injectionUtils = new InjectionUtils();
        defaultEnumValues = getAnnotationEnumDefaults(InjectModule.class, configuration, Inject.class, InjectStatic.class,
                AutoInject.class, AutoInjectStatic.class);
    }

    /**
     * Performs all supported kinds of injection on the given object's fields
     *
     * @param test
     */
    void injectObjects(Object test) {
        injectAll(test);
        autoInjectAll(test);
        injectAllStatic(test);
        autoInjectAllStatic(test);
    }

    /**
     * Injects all fields that are annotated with {@link Inject}.
     *
     * @param test
     */
    private void injectAll(Object test) {
        List<Field> fieldsToInject = getFieldsAnnotatedWith(test.getClass(), Inject.class);
        for (Field fieldToInject : fieldsToInject) {
            inject(test, fieldToInject);
        }
    }

    /**
     * Auto-injects all fields that are annotated with {@link AutoInject}
     *
     * @param test
     */
    private void autoInjectAll(Object test) {
        List<Field> fieldsToAutoInject = getFieldsAnnotatedWith(test.getClass(), AutoInject.class);
        for (Field fieldToAutoInject : fieldsToAutoInject) {
            autoInject(test, fieldToAutoInject);
        }
    }

    /**
     * Injects all fields that are annotated with {@link InjectStatic}.
     *
     * @param test
     */
    private void injectAllStatic(Object test) {
        List<Field> fieldsToInjectStatic = getFieldsAnnotatedWith(test.getClass(), InjectStatic.class);
        for (Field fieldToInjectStatic : fieldsToInjectStatic) {
            injectStatic(test, fieldToInjectStatic);
        }
    }

    /**
     * Auto-injects all fields that are annotated with {@link AutoInjectStatic}
     *
     * @param test
     */
    private void autoInjectAllStatic(Object test) {
        List<Field> fieldsToAutoInjectStatic = getFieldsAnnotatedWith(test.getClass(), AutoInjectStatic.class);
        for (Field fieldToAutoInjectStatic : fieldsToAutoInjectStatic) {
            autoInjectStatic(test, fieldToAutoInjectStatic);
        }
    }

    /**
     * Injects the fieldToInject. The target is either an explicitly specified target field of the test, or into the
     * field(s) that is/are annotated with {@link TestedObject}
     *
     * @param test The test object, not null
     * @param fieldToInject, The field from which the value is injected into the target, not null
     */
    private void inject(Object test, Field fieldToInject) {
        Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);

        List targets = getTargets(injectAnnotation, fieldToInject, injectAnnotation.target(), test);
        String ognlExpression = injectAnnotation.property();
        if (StringUtils.isEmpty(ognlExpression)) {
            throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, "Property cannot be empty"));
        }
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);

        for (Object target : targets) {
            try {
                injectionUtils.inject(objectToInject, target, ognlExpression);
            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, e.getMessage()), e);
            }
        }
    }

    /**
     * Injects the fieldToAutoInjectStatic into the specified target class.
     *
     * @param test
     * @param fieldToInjectStatic
     */
    private void injectStatic(Object test, Field fieldToInjectStatic) {
        InjectStatic injectStaticAnnotation = fieldToInjectStatic.getAnnotation(InjectStatic.class);

        Class targetClass = injectStaticAnnotation.target();
        String property = injectStaticAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic, "Property cannot be empty"));
        }
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInjectStatic);

        try {
            injectionUtils.injectStatic(objectToInject, targetClass, property);
        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic, e.getMessage()), e);
        }
    }

    /**
     * Auto-injects the fieldToInject by trying to match the fields declared type with a property of the target.
     * The target is either an explicitly specified target field of the test, or the field(s) that is/are annotated with
     * {@link TestedObject}
     *
     * @param test The test object, not null
     * @param fieldToInject, The field from which the value is injected into the target, not null
     */
    private void autoInject(Object test, Field fieldToInject) {
        AutoInject autoInjectAnnotation = fieldToInject.getAnnotation(AutoInject.class);

        List targets = getTargets(autoInjectAnnotation, fieldToInject, autoInjectAnnotation.target(), test);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);

        PropertyAccessType propertyAccessType = getValueReplaceDefault(AutoInject.class, autoInjectAnnotation.propertyAccessType(), defaultEnumValues);

        for (Object target : targets) {
            try {
                injectionUtils.autoInject(objectToInject, fieldToInject.getType(), target, propertyAccessType);
            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(autoInjectAnnotation, fieldToInject, e.getMessage()), e);
            }
        }
    }

    /**
     * Auto-injects the fieldToInject by trying to match the fields declared type with a property of the target class.
     * The target is either an explicitly specified target field of the test, or the field that is annotated with
     * {@link TestedObject}
     *
     * @param test The test object, not null
     * @param fieldToAutoInjectStatic, The field from which the value is injected into the target, not null
     */
    private void autoInjectStatic(Object test, Field fieldToAutoInjectStatic) {
        AutoInjectStatic autoInjectStaticAnnotation = fieldToAutoInjectStatic.getAnnotation(AutoInjectStatic.class);

        Class targetClass = autoInjectStaticAnnotation.target();
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToAutoInjectStatic);

        PropertyAccessType propertyAccessType = getValueReplaceDefault(AutoInjectStatic.class, autoInjectStaticAnnotation.propertyAccessType(), defaultEnumValues);

        try {
            injectionUtils.autoInjectStatic(objectToInject, fieldToAutoInjectStatic.getType(), targetClass, propertyAccessType);

        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(autoInjectStaticAnnotation, fieldToAutoInjectStatic,
                    e.getMessage()), e);
        }
    }

    /**
     * Returns the target(s) for the injection, given the specified name of the target and the test object. If
     * targetName is not equal to an empty string, the targets are the testObject's fields that are annotated with
     * {@link TestedObject}.
     *
     * @param annotation
     * @param annotatedField
     * @param targetName
     * @param test
     * @return The target(s) for the injection
     */
    private List<Object> getTargets(Annotation annotation, Field annotatedField, String targetName, Object test) {
        List<Object> targets;
        if ("".equals(targetName)) {
            // Default targetName, so it is probably not specfied. Return all objects that are annotated with the
            // TestedObject annotation.
            List<Field> testedObjectFields = getFieldsAnnotatedWith(test.getClass(), TestedObject.class);
            targets = new ArrayList<Object>(testedObjectFields.size());
            for (Field testedObjectField : testedObjectFields) {
                targets.add(ReflectionUtils.getFieldValue(test, testedObjectField));
            }
        } else {
            Field field = getFieldWithName(test.getClass(), targetName, false);
            if (field == null) {
                throw new UnitilsException(getSituatedErrorMessage(annotation, annotatedField, "Target with name " +
                        targetName + " does not exist"));
            }
            Object target = ReflectionUtils.getFieldValue(test, field);
            targets = Collections.singletonList(target);
        }
        return targets;
    }

    /**
     * Given the errorDescription, returns a situated error message, i.e. specifying the annotated field and the
     * annotation type that was used.
     * @param processedAnnotation
     * @param annotatedField
     * @param errorDescription
     * @return A situated error message
     */
    private String getSituatedErrorMessage(Annotation processedAnnotation, Field annotatedField, String errorDescription) {
        return "Error while processing @" + processedAnnotation.getClass().getSimpleName() + " annotation on field " +
                annotatedField.getName() + ": " + errorDescription;
    }

    /**
     * @return The {@link TestListener} for this module
     */
    public TestListener createTestListener() {
        return new InjectTestListener();
    }

    /**
     * {@link TestListener} for this module
     */
    private class InjectTestListener extends TestListener {

        /**
         * Before executing a test method (i.e. after the fixture methods), the injection is performed, since
         * objects to inject or targets are possibly instantiated during the fixture.
         * @param testObject The test object, not null
         * @param testMethod The test method, not null
         */
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {

            injectObjects(testObject);
        }
    }

}
