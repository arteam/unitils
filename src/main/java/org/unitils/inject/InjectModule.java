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
package org.unitils.inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.*;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.inject.util.PropertyAccess;
import org.unitils.inject.util.Restore;
import org.unitils.inject.util.ValueToRestore;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationEnumDefaults;
import static org.unitils.util.ModuleUtils.getValueReplaceDefault;
import static org.unitils.util.ReflectionUtils.getFieldValue;
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
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModule implements Module {

    /* Map holding the default configuration of the inject annotations */
    private Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> defaultEnumValues;

    /* List holding all values to restore after test was performed */
    private List<ValueToRestore> valuesToRestoreAfterTest = new ArrayList<ValueToRestore>();


    /**
     * Initializes this module using the given <code>Configuration</code> object
     *
     * @param configuration The configuration, not null
     */
    public void init(Configuration configuration) {
        defaultEnumValues = getAnnotationEnumDefaults(InjectModule.class, configuration, Inject.class, InjectStatic.class, AutoInject.class, AutoInjectStatic.class);
    }


    /**
     * Performs all supported kinds of injection on the given object's fields
     *
     * @param test The instance to inject into, not null
     */
    public void injectObjects(Object test) {
        injectAll(test);
        autoInjectAll(test);
        injectAllStatic(test);
        autoInjectAllStatic(test);
    }


    /**
     * Injects all fields that are annotated with {@link Inject}.
     *
     * @param test The instance to inject into, not null
     */
    public void injectAll(Object test) {
        List<Field> fieldsToInject = getFieldsAnnotatedWith(test.getClass(), Inject.class);
        for (Field fieldToInject : fieldsToInject) {
            inject(test, fieldToInject);
        }
    }


    /**
     * Auto-injects all fields that are annotated with {@link AutoInject}
     *
     * @param test The instance to inject into, not null
     */
    public void autoInjectAll(Object test) {
        List<Field> fieldsToAutoInject = getFieldsAnnotatedWith(test.getClass(), AutoInject.class);
        for (Field fieldToAutoInject : fieldsToAutoInject) {
            autoInject(test, fieldToAutoInject);
        }
    }


    /**
     * Injects all fields that are annotated with {@link InjectStatic}.
     *
     * @param test The instance to inject into, not null
     */
    public void injectAllStatic(Object test) {
        List<Field> fieldsToInjectStatic = getFieldsAnnotatedWith(test.getClass(), InjectStatic.class);
        for (Field fieldToInjectStatic : fieldsToInjectStatic) {
            injectStatic(test, fieldToInjectStatic);
        }
    }


    /**
     * Auto-injects all fields that are annotated with {@link AutoInjectStatic}
     *
     * @param test The instance to inject into, not null
     */
    public void autoInjectAllStatic(Object test) {
        List<Field> fieldsToAutoInjectStatic = getFieldsAnnotatedWith(test.getClass(), AutoInjectStatic.class);
        for (Field fieldToAutoInjectStatic : fieldsToAutoInjectStatic) {
            autoInjectStatic(test, fieldToAutoInjectStatic);
        }
    }


    /**
     * Restores the values that were stored using {@link #storeValueToRestoreAfterTest}.
     */
    public void restoreObjects() {
        for (ValueToRestore valueToRestore : valuesToRestoreAfterTest) {
            restore(valueToRestore);
        }
    }


    /**
     * Injects the fieldToInject. The target is either an explicitly specified target field of the test, or into the
     * field(s) that is/are annotated with {@link TestedObject}
     *
     * @param test          The instance to inject into, not null
     * @param fieldToInject The field from which the value is injected into the target, not null
     */
    protected void inject(Object test, Field fieldToInject) {
        Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);

        List targets = getTargets(injectAnnotation, fieldToInject, injectAnnotation.target(), test);
        String ognlExpression = injectAnnotation.property();
        if (StringUtils.isEmpty(ognlExpression)) {
            throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, "Property cannot be empty"));
        }
        Object objectToInject = getFieldValue(test, fieldToInject);

        for (Object target : targets) {
            try {
                InjectionUtils.inject(objectToInject, target, ognlExpression);

            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, e.getMessage()), e);
            }
        }
    }

    /**
     * Injects the fieldToAutoInjectStatic into the specified target class.
     *
     * @param test                Instance to inject into, not null
     * @param fieldToInjectStatic The field from which the value is injected into the target, not null
     */
    protected void injectStatic(Object test, Field fieldToInjectStatic) {
        InjectStatic injectStaticAnnotation = fieldToInjectStatic.getAnnotation(InjectStatic.class);

        Class targetClass = injectStaticAnnotation.target();
        String property = injectStaticAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic, "Property cannot be empty"));
        }
        Object objectToInject = getFieldValue(test, fieldToInjectStatic);

        Restore restore = getValueReplaceDefault(InjectStatic.class, injectStaticAnnotation.restore(), defaultEnumValues);
        try {
            Object oldValue = InjectionUtils.injectStatic(objectToInject, targetClass, property);
            storeValueToRestoreAfterTest(targetClass, property, fieldToInjectStatic.getType(), null, oldValue, restore);

        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic, e.getMessage()), e);
        }
    }


    /**
     * Auto-injects the fieldToInject by trying to match the fields declared type with a property of the target.
     * The target is either an explicitly specified target field of the test, or the field(s) that is/are annotated with
     * {@link TestedObject}
     *
     * @param test          The instance to inject into, not null
     * @param fieldToInject The field from which the value is injected into the target, not null
     */
    protected void autoInject(Object test, Field fieldToInject) {
        AutoInject autoInjectAnnotation = fieldToInject.getAnnotation(AutoInject.class);

        List targets = getTargets(autoInjectAnnotation, fieldToInject, autoInjectAnnotation.target(), test);
        Object objectToInject = getFieldValue(test, fieldToInject);

        PropertyAccess propertyAccess = getValueReplaceDefault(AutoInject.class, autoInjectAnnotation.propertyAccess(), defaultEnumValues);
        for (Object target : targets) {
            try {
                InjectionUtils.autoInject(objectToInject, fieldToInject.getType(), target, propertyAccess);

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
     * @param test                    The instance to inject into, not null
     * @param fieldToAutoInjectStatic The field from which the value is injected into the target, not null
     */
    protected void autoInjectStatic(Object test, Field fieldToAutoInjectStatic) {
        AutoInjectStatic autoInjectStaticAnnotation = fieldToAutoInjectStatic.getAnnotation(AutoInjectStatic.class);

        Class targetClass = autoInjectStaticAnnotation.target();
        Object objectToInject = getFieldValue(test, fieldToAutoInjectStatic);

        Restore restore = getValueReplaceDefault(AutoInjectStatic.class, autoInjectStaticAnnotation.restore(), defaultEnumValues);
        PropertyAccess propertyAccess = getValueReplaceDefault(AutoInjectStatic.class, autoInjectStaticAnnotation.propertyAccess(), defaultEnumValues);
        try {
            Object oldValue = InjectionUtils.autoInjectStatic(objectToInject, fieldToAutoInjectStatic.getType(), targetClass, propertyAccess);
            storeValueToRestoreAfterTest(targetClass, null, fieldToAutoInjectStatic.getType(), propertyAccess, oldValue, restore);

        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(autoInjectStaticAnnotation, fieldToAutoInjectStatic, e.getMessage()), e);
        }
    }


    /**
     * Restores the given value.
     *
     * @param valueToRestore the value, not null
     */
    protected void restore(ValueToRestore valueToRestore) {

        Object value = valueToRestore.getValue();
        Class targetClass = valueToRestore.getTargetClass();

        String property = valueToRestore.getProperty();
        if (property != null) {
            // regular injection
            InjectionUtils.injectStatic(value, targetClass, property);

        } else {
            // auto injection
            InjectionUtils.autoInjectStatic(value, valueToRestore.getFieldType(), targetClass, valueToRestore.getPropertyAccessType());
        }
    }


    /**
     * Stores the old value that was replaced during the injection so that it can be restored after the test was
     * performed. The value that is stored depends on the restore value: OLD_VALUE will store the value that was replaced,
     * NULL_OR_0_VALUE will store 0 or null depeding whether it is a primitive or not, NO_RESTORE stores nothing.
     *
     * @param targetClass        The target class, not null
     * @param property           The OGNL expression that defines where the object will be injected, null for auto inject
     * @param fieldType          The type, not null
     * @param propertyAccess The access type in case auto injection is used
     * @param oldValue           The value that was replaced during the injection
     * @param restore            The type of reset, not DEFAULT
     */
    protected void storeValueToRestoreAfterTest(Class targetClass, String property, Class fieldType, PropertyAccess propertyAccess, Object oldValue, Restore restore) {

        if (Restore.NO_RESTORE == restore || Restore.DEFAULT == restore) {
            return;
        }

        ValueToRestore valueToRestore = new ValueToRestore();
        valueToRestore.setTargetClass(targetClass);
        valueToRestore.setProperty(property);
        valueToRestore.setFieldType(fieldType);
        valueToRestore.setPropertyAccessType(propertyAccess);

        if (Restore.OLD_VALUE == restore) {
            valueToRestore.setValue(oldValue);

        } else if (Restore.NULL_OR_0_VALUE == restore) {
            valueToRestore.setValue(fieldType.isPrimitive() ? 0 : null);
        }
        valuesToRestoreAfterTest.add(valueToRestore);
    }


    /**
     * Returns the target(s) for the injection, given the specified name of the target and the test object. If
     * targetName is not equal to an empty string, the targets are the testObject's fields that are annotated with
     * {@link TestedObject}.
     *
     * @param annotation     The injection annotation for which the targets are meant, not null
     * @param annotatedField The annotated field, not null
     * @param targetName     The explicit target name or empty string for TestedObject targets
     * @param test           The test instance
     * @return The target(s) for the injection
     */
    protected List<Object> getTargets(Annotation annotation, Field annotatedField, String targetName, Object test) {

        List<Object> targets;
        if ("".equals(targetName)) {
            // Default targetName, so it is probably not specfied. Return all objects that are annotated with the TestedObject annotation.
            List<Field> testedObjectFields = getFieldsAnnotatedWith(test.getClass(), TestedObject.class);
            targets = new ArrayList<Object>(testedObjectFields.size());
            for (Field testedObjectField : testedObjectFields) {
                targets.add(getFieldValue(test, testedObjectField));
            }
        } else {
            Field field = getFieldWithName(test.getClass(), targetName, false);
            if (field == null) {
                throw new UnitilsException(getSituatedErrorMessage(annotation, annotatedField, "Target with name " + targetName + " does not exist"));
            }
            Object target = getFieldValue(test, field);
            targets = Collections.singletonList(target);
        }
        return targets;
    }


    /**
     * Given the errorDescription, returns a situated error message, i.e. specifying the annotated field and the
     * annotation type that was used.
     *
     * @param processedAnnotation The injection annotation, not null
     * @param annotatedField      The annotated field, not null
     * @param errorDescription    A custom description, not null
     * @return A situated error message
     */
    protected String getSituatedErrorMessage(Annotation processedAnnotation, Field annotatedField, String errorDescription) {
        return "Error while processing @" + processedAnnotation.getClass().getSimpleName() + " annotation on field " + annotatedField.getName() + ": " + errorDescription;
    }


    /**
     * @return The {@link TestListener} for this module
     */
    public TestListener createTestListener() {
        return new InjectTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    private class InjectTestListener extends TestListener {

        /**
         * Before executing a test method (i.e. after the fixture methods), the injection is performed, since
         * objects to inject or targets are possibly instantiated during the fixture.
         *
         * @param testObject The test object, not null
         * @param testMethod The test method, not null
         */
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            injectObjects(testObject);
        }

        /**
         * After test execution, if requested restore all values that were replaced in the injection.
         *
         * @param testObject The test object, not null
         * @param testMethod The test method, not null
         */
        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            restoreObjects();
        }
    }

}
