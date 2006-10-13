package org.unitils.inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.TestContext;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.core.UnitilsModule;
import org.unitils.inject.annotation.*;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * todo javadoc
 */
public class InjectModule implements UnitilsModule {


    public void init(Configuration configuration) {

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
        String ognlExpression = injectAnnotation.property();
        if (StringUtils.isEmpty(ognlExpression)) {
            throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, "Property cannot be empty"));
        }
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);

        for (Object target : targets) {
            try {
                InjectionUtils.inject(objectToInject, target, ognlExpression);
            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(injectAnnotation, fieldToInject, e.getMessage()), e);
            }
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

        try {
            InjectionUtils.injectStatic(objectToInject, targetClass, property);
        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(injectStaticAnnotation, fieldToInjectStatic, e.getMessage()), e);
        }
    }

    private void autoInject(Object test, Field fieldToInject) {
        AutoInject autoInjectAnnotation = fieldToInject.getAnnotation(AutoInject.class);

        List targets = getTargets(autoInjectAnnotation, fieldToInject, autoInjectAnnotation.target(), test);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);

        PropertyAccessType propertyAccessType = PropertyAccessType.valueOf(AnnotationUtils.getValueReplaceDefault(autoInjectAnnotation.propertyAccessType()).name());

        for (Object target : targets) {
            try {
                InjectionUtils.autoInject(objectToInject, fieldToInject.getType(), target, propertyAccessType);
            } catch (UnitilsException e) {
                throw new UnitilsException(getSituatedErrorMessage(autoInjectAnnotation, fieldToInject, e.getMessage()), e);
            }
        }
    }

    private void autoInjectStatic(Object test, Field fieldToAutoInjectStatic) {
        AutoInjectStatic autoInjectStaticAnnotation = fieldToAutoInjectStatic.getAnnotation(AutoInjectStatic.class);

        Class targetClass = autoInjectStaticAnnotation.target();
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToAutoInjectStatic);

        PropertyAccessType propertyAccessType = PropertyAccessType.valueOf(AnnotationUtils.getValueReplaceDefault(
                autoInjectStaticAnnotation.propertyAccessType()).name());

        try {
            InjectionUtils.autoInjectStatic(objectToInject, fieldToAutoInjectStatic.getType(), targetClass, propertyAccessType);
        } catch (UnitilsException e) {
            throw new UnitilsException(getSituatedErrorMessage(autoInjectStaticAnnotation, fieldToAutoInjectStatic,
                    e.getMessage()), e);
        }
    }

    private List<Object> getTargets(Annotation annotation, Field annotatedField, String targetName, Object test) {
        List<Object> targets;
        if ("".equals(targetName)) {
            // Default targetName, so it is probably not specfied. Return all objects that are annotated with the
            // TestedObject annotation.
            List<Field> testedObjectFields = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), TestedObject.class);
            targets = new ArrayList<Object>(testedObjectFields.size());
            for (Field testedObjectField : testedObjectFields) {
                targets.add(ReflectionUtils.getFieldValue(test, testedObjectField));
            }
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

    private String getSituatedErrorMessage(Annotation processedAnnotation, Field annotatedField, String errorDescription) {
        return "Error while processing @" + processedAnnotation.getClass().getSimpleName() + " annotation on field " +
                annotatedField.getName() + ": " + errorDescription;
    }

    public TestListener createTestListener() {
        return new InjectTestListener();
    }

    private class InjectTestListener extends TestListener {

        @Override
        public void beforeTestMethod(TestContext testContext) {

            injectObjects(testContext.getTestObject());
        }
    }

}
