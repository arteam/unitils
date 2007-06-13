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

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.getFieldValue;
import static org.unitils.util.ReflectionUtils.getFieldWithName;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.InjectIntoStatic;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.inject.util.PropertyAccess;
import org.unitils.inject.util.Restore;
import org.unitils.inject.util.ValueToRestore;
import org.unitils.util.PropertyUtils;

/**
 * Module for injecting annotated objects into other objects. The intended usage is to inject mock objects, but it can
 * be used for regular objects too.
 * <p/>
 * Both explicit injection and automatic injection by type are supported. An object annotated with {@link InjectInto} is
 * explicitly injected into a target object. An object annotated with {@link InjectIntoByType} is automatically injected into a
 * target property with the same type as the declared type of the annotated object.
 * <p/>
 * Explicit and automatic injection into static fields is also supported, by means of the {@link InjectIntoStatic} and {@link
 * InjectIntoStaticByType} annotations.
 * <p/>
 * The target object can either be specified explicitly, or implicitly by annotating an object with {@link TestedObject}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModule implements Module {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(InjectModule.class);

    /* Property key indicating if the tested objects should automatically be created if they are not created yet */
    private static final String PROPKEY_CREATE_TESTEDOBJECTS_IF_NULL_ENABLED = "InjectModule.TestedObject.createIfNull.enabled";

    /* Map holding the default configuration of the inject annotations */
    private Map<Class<? extends Annotation>, Map<Method, String>> defaultAnnotationPropertyValues;

    /* List holding all values to restore after test was performed */
    private List<ValueToRestore> valuesToRestoreAfterTest = new ArrayList<ValueToRestore>();

    /* Indicates if tested object instance should be created if they are not created yet */
    private boolean createTestedObjectsIfNullEnabled;


    /**
     * Initializes this module using the given configuration.
     *
     * @param configuration The configuration, not null
     */
    public void init(Properties configuration) {
        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(InjectModule.class, configuration, InjectInto.class, InjectIntoStatic.class, InjectIntoByType.class, InjectIntoStaticByType.class);
        createTestedObjectsIfNullEnabled = PropertyUtils.getBoolean(PROPKEY_CREATE_TESTEDOBJECTS_IF_NULL_ENABLED, configuration);
    }


    /**
     * For all fields annotated with {@link TestedObject} that are still null after the test fixture, an object is
     * created of the field's declared type and assigned to the field. If the field's declared type is an interface or
     * abstract class, or if the type doesn't have a default constructor, a warning is produced.
     *
     * @param testObject The test instance, not null
     */
    public void createTestedObjectsIfNull(Object testObject) {
        List<Field> testedObjectFields = getFieldsAnnotatedWith(testObject.getClass(), TestedObject.class);
        for (Field testedObjectField : testedObjectFields) {
            if (getFieldValue(testObject, testedObjectField) == null) {
                createObjectForField(testObject, testedObjectField);
            }
        }
    }


    /**
     * Creates an objects of the given fields' declared type and assigns it to this field on the given testObject
     *
     * @param testObject        The test instance, not null
     * @param testedObjectField The tested object field, not null
     */
    protected void createObjectForField(Object testObject, Field testedObjectField) {
        Class<?> declaredClass = testedObjectField.getType();
        if (declaredClass.isInterface()) {
            logger.warn("Field " + testedObjectField.getName() + " (annotated with @TestedObject) has type " + testedObjectField.getType().getSimpleName()
                    + " which is an interface type. It is not automatically instantiated.");
        } else if (Modifier.isAbstract(declaredClass.getModifiers())) {
            logger.warn("Field " + testedObjectField.getName() + " (annotated with @TestedObject) has type " + testedObjectField.getDeclaringClass().getSimpleName()
                    + " which is an abstract class. It is not automatically instantiated.");
        } else {
            try {
                declaredClass.getDeclaredConstructor();
                Object instance = createInstanceOfType(declaredClass, true);
                setFieldValue(testObject, testedObjectField, instance);
            } catch (NoSuchMethodException e) {
                logger.warn("Field " + testedObjectField.getName() + " (annotated with @TestedObject) has type " + testedObjectField.getDeclaringClass().getSimpleName()
                        + " which has no default (parameterless) constructor. It is not automatically instantiated.");
            }
        }
    }


    /**
     * Performs all supported kinds of injection on the given object's fields
     *
     * @param test The instance to inject into, not null
     */
    public void injectObjects(Object test) {
        injectAll(test);
        injectAllByType(test);
        injectAllStatic(test);
        injectAllStaticByType(test);
    }


    /**
     * Injects all fields that are annotated with {@link InjectInto}.
     *
     * @param test The instance to inject into, not null
     */
    public void injectAll(Object test) {
        List<Field> fields = getFieldsAnnotatedWith(test.getClass(), InjectInto.class);
        for (Field field : fields) {
            inject(test, field);
        }
    }


    /**
     * Auto-injects all fields that are annotated with {@link InjectIntoByType}
     *
     * @param test The instance to inject into, not null
     */
    public void injectAllByType(Object test) {
        List<Field> fields = getFieldsAnnotatedWith(test.getClass(), InjectIntoByType.class);
        for (Field field : fields) {
            injectByType(test, field);
        }
    }


    /**
     * Injects all fields that are annotated with {@link InjectIntoStatic}.
     *
     * @param test The instance to inject into, not null
     */
    public void injectAllStatic(Object test) {
        List<Field> fields = getFieldsAnnotatedWith(test.getClass(), InjectIntoStatic.class);
        for (Field field : fields) {
            injectStatic(test, field);
        }
    }


    /**
     * Auto-injects all fields that are annotated with {@link InjectIntoStaticByType}
     *
     * @param test The instance to inject into, not null
     */
    public void injectAllStaticByType(Object test) {
        List<Field> fields = getFieldsAnnotatedWith(test.getClass(), InjectIntoStaticByType.class);
        for (Field field : fields) {
            injectStaticByType(test, field);
        }
    }


    /**
     * Restores the values that were stored using {@link #storeValueToRestoreAfterTest}.
     */
    public void restoreStaticInjectedObjects() {
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
        InjectInto injectIntoAnnotation = fieldToInject.getAnnotation(InjectInto.class);

        String ognlExpression = injectIntoAnnotation.property();
        if (StringUtils.isEmpty(ognlExpression)) {
            throw new UnitilsException(getSituatedErrorMessage(InjectInto.class, fieldToInject, "Property cannot be empty"));
        }
        Object objectToInject = getFieldValue(test, fieldToInject);

        List targets = getTargets(InjectInto.class, fieldToInject, injectIntoAnnotation.target(), test);
        if (targets.size() == 0) {
            throw new UnitilsException(getSituatedErrorMessage(InjectInto.class, fieldToInject, "The target should either be " +
                    "specified explicitly using the target property, or by using the @" + TestedObject.class.getSimpleName() +
                    " annotation"));
        }

        for (Object target : targets) {
            try {
                InjectionUtils.inject(objectToInject, target, ognlExpression);

            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(InjectInto.class, fieldToInject, e.getMessage()), e);
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
        InjectIntoStatic injectIntoStaticAnnotation = fieldToInjectStatic.getAnnotation(InjectIntoStatic.class);

        Class targetClass = injectIntoStaticAnnotation.target();
        String property = injectIntoStaticAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new UnitilsException(getSituatedErrorMessage(InjectIntoStatic.class, fieldToInjectStatic, "Property cannot be empty"));
        }
        Object objectToInject = getFieldValue(test, fieldToInjectStatic);

        Restore restore = getEnumValueReplaceDefault(InjectIntoStatic.class, "restore", injectIntoStaticAnnotation.restore(), defaultAnnotationPropertyValues);
        try {
            Object oldValue = InjectionUtils.injectStatic(objectToInject, targetClass, property);
            storeValueToRestoreAfterTest(targetClass, property, fieldToInjectStatic.getType(), null, oldValue, restore);

        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(InjectIntoStatic.class, fieldToInjectStatic, e.getMessage()), e);
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
    protected void injectByType(Object test, Field fieldToInject) {
        InjectIntoByType injectIntoByTypeAnnotation = fieldToInject.getAnnotation(InjectIntoByType.class);

        Object objectToInject = getFieldValue(test, fieldToInject);
        PropertyAccess propertyAccess = getEnumValueReplaceDefault(InjectIntoByType.class, "propertyAccess",
                injectIntoByTypeAnnotation.propertyAccess(), defaultAnnotationPropertyValues);

        List targets = getTargets(InjectIntoByType.class, fieldToInject, injectIntoByTypeAnnotation.target(), test);
        if (targets.size() == 0) {
            throw new UnitilsException(getSituatedErrorMessage(InjectIntoByType.class, fieldToInject, "The target should either be " +
                    "specified explicitly using the target property, or by using the @" + TestedObject.class.getSimpleName() +
                    " annotation"));
        }

        for (Object target : targets) {
            try {
                InjectionUtils.autoInject(objectToInject, fieldToInject.getType(), target, propertyAccess);

            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(InjectIntoByType.class, fieldToInject, e.getMessage()), e);
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
    protected void injectStaticByType(Object test, Field fieldToAutoInjectStatic) {
        InjectIntoStaticByType injectIntoStaticByTypeAnnotation = fieldToAutoInjectStatic.getAnnotation(InjectIntoStaticByType.class);

        Class targetClass = injectIntoStaticByTypeAnnotation.target();
        Object objectToInject = getFieldValue(test, fieldToAutoInjectStatic);

        Restore restore = getEnumValueReplaceDefault(InjectIntoStaticByType.class, "restore", injectIntoStaticByTypeAnnotation.restore(), defaultAnnotationPropertyValues);
        PropertyAccess propertyAccess = getEnumValueReplaceDefault(InjectIntoStaticByType.class, "propertyAccess",
                injectIntoStaticByTypeAnnotation.propertyAccess(), defaultAnnotationPropertyValues);
        try {
            Object oldValue = InjectionUtils.autoInjectStatic(objectToInject, fieldToAutoInjectStatic.getType(), targetClass, propertyAccess);
            storeValueToRestoreAfterTest(targetClass, null, fieldToAutoInjectStatic.getType(), propertyAccess, oldValue, restore);

        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(InjectIntoStaticByType.class, fieldToAutoInjectStatic, e.getMessage()), e);
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
     * @param targetClass    The target class, not null
     * @param property       The OGNL expression that defines where the object will be injected, null for auto inject
     * @param fieldType      The type, not null
     * @param propertyAccess The access type in case auto injection is used
     * @param oldValue       The value that was replaced during the injection
     * @param restore        The type of reset, not DEFAULT
     */
    protected void storeValueToRestoreAfterTest(Class targetClass, String property, Class fieldType, PropertyAccess propertyAccess, Object oldValue, Restore restore) {
        if (Restore.NO_RESTORE == restore || Restore.DEFAULT == restore) {
            return;
        }

        ValueToRestore valueToRestore;
        if (Restore.OLD_VALUE == restore) {
            valueToRestore = new ValueToRestore(targetClass, property, fieldType, propertyAccess, oldValue);

        } else if (Restore.NULL_OR_0_VALUE == restore) {
            valueToRestore = new ValueToRestore(targetClass, property, fieldType, propertyAccess, fieldType.isPrimitive() ? 0 : null);

        } else {
            throw new RuntimeException("Unkown value for " + Restore.class.getSimpleName() + " " + restore);
        }
        valuesToRestoreAfterTest.add(valueToRestore);
    }


    /**
     * Returns the target(s) for the injection, given the specified name of the target and the test object. If
     * targetName is not equal to an empty string, the targets are the testObject's fields that are annotated with
     * {@link TestedObject}.
     *
     * @param annotationClass The class of the annotation, not null
     * @param annotatedField  The annotated field, not null
     * @param targetName      The explicit target name or empty string for TestedObject targets
     * @param test            The test instance
     * @return The target(s) for the injection
     */
    protected List<Object> getTargets(Class<? extends Annotation> annotationClass, Field annotatedField, String targetName, Object test) {

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
                throw new UnitilsException(getSituatedErrorMessage(annotationClass, annotatedField, "Target with name " + targetName + " does not exist"));
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
     * @param annotationClass  The injection annotation, not null
     * @param annotatedField   The annotated field, not null
     * @param errorDescription A custom description, not null
     * @return A situated error message
     */
    protected String getSituatedErrorMessage(Class<? extends Annotation> annotationClass, Field annotatedField, String errorDescription) {
        return "Error while processing @" + annotationClass.getSimpleName() + " annotation on field " + annotatedField.getName()
                + " of class " + annotatedField.getDeclaringClass().getSimpleName() + ": " + errorDescription;
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
    protected class InjectTestListener extends TestListener {

        /**
         * Before executing a test method (i.e. after the fixture methods), the injection is performed, since
         * objects to inject or targets are possibly instantiated during the fixture.
         *
         * @param testObject The test object, not null
         * @param testMethod The test method, not null
         */
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {

            if (createTestedObjectsIfNullEnabled) {
                createTestedObjectsIfNull(testObject);
            }
            injectObjects(testObject);
        }

        /**
         * After test execution, if requested restore all values that were replaced in the static injection.
         *
         * @param testObject The test object, not null
         * @param testMethod The test method, not null
         * @param throwable  The throwable thrown during the test, null if none was thrown
         */
        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable throwable) {
            restoreStaticInjectedObjects();
        }
    }

}
