package org.unitils.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 */
public class AnnotationUtils {

    //todo javadoc
    public static final String DEFAULT_ENUM_VALUE_NAME = "DEFAULT";

    /**
     * Returns the given class's declared fields that are marked with the given annotation
     *
     * @param clazz
     * @param annotation
     * @return A List containing fields annotated with the given annotation
     */
    public static <T extends Annotation> List<Field> getFieldsAnnotatedWith(Class clazz, Class<T> annotation) {
        List<Field> annotatedFields = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

    /**
     * Returns the values of all the given objects' fields that are annotated with the given annotation
     *
     * @param object
     * @param annotation
     * @return the values of all the given objects' fields that are annotated with the given annotation
     */
    public static <T extends Annotation> List getFieldValuesAnnotatedWith(Object object, Class<T> annotation) {
        List fieldValues = new ArrayList();
        List<Field> annotatedFields = getFieldsAnnotatedWith(object.getClass(), annotation);
        for (Field annotatedField : annotatedFields) {
            try {
                annotatedField.setAccessible(true);
                fieldValues.add(annotatedField.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while accessing field", e);
            }
        }
        return fieldValues;
    }

    /**
     * Returns the given class's declared methods that are marked with the given annotation
     *
     * @param clazz
     * @param annotation
     * @return A List containing methods annotated with the given annotation
     */
    public static <T extends Annotation> List<Method> getMethodsAnnotatedWith(Class clazz, Class<T> annotation) {

        List<Method> annotatedMethods = new ArrayList<Method>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(annotation) != null) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }


    // todo javadoc
    @SuppressWarnings({"unchecked"})
    public static <T extends Enum> T getValueReplaceDefault(T enumValue) {

        if (DEFAULT_ENUM_VALUE_NAME.equalsIgnoreCase(enumValue.name())) {

            return getDefaultValue((Class<T>) enumValue.getClass());
        }
        return enumValue;
    }


    // todo javadoc
    private static <T extends Enum> T getDefaultValue(Class<T> enumClass) {

        String enumClassName = enumClass.getName();
        String defaultValueName = UnitilsConfiguration.getInstance().getString(enumClassName);

        T[] enumValues = enumClass.getEnumConstants();
        for (T enumValue : enumValues) {
            if (defaultValueName.equalsIgnoreCase(enumValue.name())) {

                return enumValue;
            }
        }
        throw new RuntimeException("todo");
    }
}
