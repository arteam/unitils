package be.ordina.unitils.inject;

import be.ordina.unitils.module.TestContext;
import be.ordina.unitils.module.TestListener;
import be.ordina.unitils.module.UnitilsModule;
import be.ordina.unitils.util.AnnotationUtils;
import be.ordina.unitils.util.ReflectionUtils;
import be.ordina.unitils.util.UnitilsConfiguration;
import be.ordina.unitils.inject.annotation.Inject;
import be.ordina.unitils.inject.annotation.TestedObject;
import be.ordina.unitils.inject.annotation.AutoInject;
import be.ordina.unitils.inject.annotation.InjectStatic;
import be.ordina.unitils.inject.annotation.AutoInjectStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Collections;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.DefaultMemberAccess;
import ognl.OgnlContext;
import org.apache.commons.lang.StringUtils;

/**
 * @author Filip Neven
 */
public class InjectModule implements UnitilsModule {

    private static final String PROPKEY_PROPERTYACCESSTYPE_DEFAULT = "inject.propertyaccesstype.default";

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
                setValue(property, target, objectToInject);
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
        for (Object target : targets) {
            if (autoInjectAnnotation.propertyAccessType() == AutoInject.PropertyAccessType.FIELD) {
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
        setValueStatic(property, targetClass, objectToInject);
    }

    private void setValueStatic(String property, Class targetClass, Object value) {
        // TODO
    }

    private void autoInjectStatic(Object test, Field fieldToAutoInjectStatic) {
        AutoInjectStatic autoInjectStaticAnnotation = fieldToAutoInjectStatic.getAnnotation(AutoInjectStatic.class);
        Object objectToInject = ReflectionUtils.getFieldValue(test, fieldToAutoInjectStatic);
        Class objectToInjectType = fieldToAutoInjectStatic.getType();
        Class targetClass = autoInjectStaticAnnotation.target();
        if (autoInjectStaticAnnotation.propertyAccessType() == AutoInjectStatic.PropertyAccessType.FIELD) {
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
            try {
                Object target = ReflectionUtils.getFieldValueWithName(test, targetName);
                targets = Collections.singletonList(target);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Target with name " + targetName + " does not exist");
            }
        }
        return targets;
    }

    private void setValue(String ognlExprStr, Object target, Object objectToInject) throws OgnlException {
        OgnlContext ognlContext = new OgnlContext();
        ognlContext.setMemberAccess(new DefaultMemberAccess(true));
        Object ognlExpression = Ognl.parseExpression(ognlExprStr);
        Ognl.setValue(ognlExpression, ognlContext, target, objectToInject);
    }

    private List getTestedObjects(Object test) {
        return AnnotationUtils.getFieldValuesAnnotatedWith(test, TestedObject.class);
    }

    private PropertyAccessType getPropertyAccessType(PropertyAccessType annotatedAccessType) {
        if (annotatedAccessType == PropertyAccessType.DEFAULT) {
            String accessTypeStr = (String) UnitilsConfiguration.getInstance().getProperty(PROPKEY_PROPERTYACCESSTYPE_DEFAULT);
            if ("setter".equals(accessTypeStr)) {
                return PropertyAccessType.SETTER;
            } else if ("field".equals(accessTypeStr)) {
                return PropertyAccessType.FIELD;
            } else {
                throw new IllegalArgumentException("Invalid value for option " + PROPKEY_PROPERTYACCESSTYPE_DEFAULT +
                        ": should be 'setter' or 'field'");
            }
        } else {
            return annotatedAccessType;
        }
    }

}
