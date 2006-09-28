package org.unitils.inject;


import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.TestContext;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsModule;
import org.unitils.inject.annotation.*;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * todo javadoc
 *
 * @author Filip Neven
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
        inject(test);
        autoInject(test);
        injectStatic(test);
        autoInjectStatic(test);
    }

    private void inject(Object test) {
        List<Field> fieldsToInject = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), Inject.class);
        for (Field fieldToInject : fieldsToInject) {
            inject(test, fieldToInject);
        }
    }

    private void autoInject(Object test) {
        List<Field> fieldsToAutoInject = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), AutoInject.class);
        for (Field fieldToAutoInject : fieldsToAutoInject) {
            autoInject(test, fieldToAutoInject);
        }
    }

    private void injectStatic(Object test) {
        List<Field> fieldsToInjectStatic = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), InjectStatic.class);
        for (Field fieldToInjectStatic : fieldsToInjectStatic) {
            injectStatic(test, fieldToInjectStatic);
        }
    }

    private void autoInjectStatic(Object test) {
        List<Field> fieldsToAutoInjectStatic = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), AutoInjectStatic.class);
        for (Field fieldToAutoInjectStatic : fieldsToAutoInjectStatic) {
            autoInjectStatic(test, fieldToAutoInjectStatic);
        }
    }

    private void inject(Object test, Field fieldToInject) {
        Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);
        String targetName = injectAnnotation.target();
        List targets = getTargets(targetName, test);
        String property = injectAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("Property cannot be empty");
        }
        try {
            for (Object target : targets) {
                setValueUsingOgnl(property, target, objectToInject);
            }
        } catch (OgnlException e) {
            throw new RuntimeException("Property could not be parsed", e);
        }
    }

    private void autoInject(Object test, Field fieldToInject) {
        AutoInject autoInjectAnnotation = fieldToInject.getAnnotation(AutoInject.class);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInject);
        Class objectToInjectType = fieldToInject.getType();
        String targetName = autoInjectAnnotation.target();
        List targets = getTargets(targetName, test);
        AutoInject.PropertyAccessType propertyAccessType = AnnotationUtils.getValueReplaceDefault(autoInjectAnnotation.propertyAccessType());
        for (Object target : targets) {
            if (propertyAccessType == AutoInject.PropertyAccessType.FIELD) {
                Field fieldToInjectTo = ReflectionUtils.getFieldOfType(target.getClass(), objectToInjectType, false);
                if (fieldToInjectTo == null) {
                    // If one field exist that has a type which is more specific than all other fields of the given type,
                    // this one is taken. Otherwise, an exception is thrown
                    List<Field> fieldsOfType = ReflectionUtils.getFieldsAssignableFrom(target.getClass(), objectToInjectType);
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
                }
                ReflectionUtils.setFieldValue(target, fieldToInjectTo, objectToInject);
            } else {
                Method setterToInjectTo = ReflectionUtils.getSetterOfType(target.getClass(), objectToInjectType, false);
                if (setterToInjectTo == null) {
                    // If one setter exist that has a type which is more specific than all other setters of the given type,
                    // this one is taken. Otherwise, an exception is thrown
                    List<Method> settersOfType = ReflectionUtils.getSettersAssignableFrom(target.getClass(), objectToInjectType, false);
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
                }
                ReflectionUtils.invokeMethod(target, setterToInjectTo, objectToInject);
            }
        }
    }

    private void injectStatic(Object test, Field fieldToInjectStatic) {
        InjectStatic injectAnnotation = fieldToInjectStatic.getAnnotation(InjectStatic.class);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToInjectStatic);
        Class targetClass = injectAnnotation.target();
        String property = injectAnnotation.property();
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("Property cannot be empty");
        }
        String staticProperty = StringUtils.substringBefore(property, ".");
        if (property.equals(staticProperty)) {
            setValueStatic(staticProperty, targetClass, objectToInject);
        } else {
            Object objectToInjectInto = getValueStatic(staticProperty, targetClass);
            String remainingPropertyPart = StringUtils.substringAfter(property, ".");
            try {
                setValueUsingOgnl(remainingPropertyPart, objectToInjectInto, objectToInject);
            } catch (OgnlException e) {
                throw new RuntimeException("Property named " + remainingPropertyPart + " not found on object " + objectToInjectInto);
            }
        }
    }

    private Object getValueStatic(String staticProperty, Class targetClass) {
        Method staticGetter = ReflectionUtils.getGetter(staticProperty, targetClass, true);
        if (staticGetter != null) {
            return ReflectionUtils.invokeMethod(targetClass, staticGetter);
        } else {
            return null;
        }
    }

    private void setValueStatic(String staticProperty, Class targetClass, Object value) {
        Method staticSetter = ReflectionUtils.getSetter(staticProperty, targetClass, value.getClass(), true);
        if (staticSetter != null) {
            ReflectionUtils.invokeMethod(targetClass, staticSetter, value);
        } else {
            Field staticField = ReflectionUtils.getFieldWithName(staticProperty, targetClass, true);
            ReflectionUtils.setFieldValue(targetClass, staticField, value);
        }
    }

    private void autoInjectStatic(Object test, Field fieldToAutoInjectStatic) {
        AutoInjectStatic autoInjectStaticAnnotation = fieldToAutoInjectStatic.getAnnotation(AutoInjectStatic.class);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToAutoInjectStatic);
        Class objectToInjectType = fieldToAutoInjectStatic.getType();
        Class targetClass = autoInjectStaticAnnotation.target();
        AutoInjectStatic.PropertyAccessType propertyAccessType = AnnotationUtils.getValueReplaceDefault(
                autoInjectStaticAnnotation.propertyAccessType());
        if (propertyAccessType == AutoInjectStatic.PropertyAccessType.FIELD) {
            Field fieldToInjectTo = ReflectionUtils.getFieldOfType(targetClass, objectToInjectType, true);
            if (fieldToInjectTo == null) {
                // If one field exist that has a type which is more specific than all other fields of the given type,
                // this one is taken. Otherwise, an exception is thrown
                List<Field> fieldsOfType = ReflectionUtils.getFieldsAssignableFrom(targetClass, objectToInjectType);
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
            }
            ReflectionUtils.setFieldValue(targetClass, fieldToInjectTo, objectToInject);
        } else {
            Method setterToInjectTo = ReflectionUtils.getSetterOfType(targetClass, objectToInjectType, true);
            if (setterToInjectTo == null) {
                // If one setter exist that has a type which is more specific than all other setters of the given type,
                // this one is taken. Otherwise, an exception is thrown
                List<Method> settersOfType = ReflectionUtils.getSettersAssignableFrom(targetClass, objectToInjectType, true);
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
            }
            ReflectionUtils.invokeMethod(targetClass, setterToInjectTo, objectToInject);
        }

    }

    private List getTargets(String targetName, Object test) {
        List targets;
        if ("".equals(targetName)) {
            targets = getTestedObjects(test);
        } else {
            Field field = ReflectionUtils.getFieldWithName(targetName, test.getClass(), false);
            if (field == null) {
                throw new IllegalArgumentException("Target with name " + targetName + " does not exist");
            }
            Object target = ReflectionUtils.getFieldValue(test, field);
            targets = Collections.singletonList(target);
        }
        return targets;
    }

    private void setValueUsingOgnl(String ognlExprStr, Object target, Object objectToInject) throws OgnlException {
        OgnlContext ognlContext = new OgnlContext();
        ognlContext.setMemberAccess(new DefaultMemberAccess(true));
        Object ognlExpression = Ognl.parseExpression(ognlExprStr);
        Ognl.setValue(ognlExpression, ognlContext, target, objectToInject);
    }

    private List getTestedObjects(Object test) {
        return AnnotationUtils.getFieldValuesAnnotatedWith(test, TestedObject.class);
    }

}
