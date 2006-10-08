package org.unitils.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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
        if (Object.class.equals(clazz)) {
            return Collections.EMPTY_LIST;
        } else {
            List<Field> annotatedFields = new ArrayList<Field>();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(annotation) != null) {
                    annotatedFields.add(field);
                }
            }
            annotatedFields.addAll(getFieldsAnnotatedWith(clazz.getSuperclass(), annotation));
            return annotatedFields;
        }
    }

    /**
     * Returns the given class's declared methods that are marked with the given annotation
     *
     * @param clazz
     * @param annotation
     * @return A List containing methods annotated with the given annotation
     */
    public static <T extends Annotation> List<Method> getMethodsAnnotatedWith(Class clazz, Class<T> annotation) {

        if (Object.class.equals(clazz)) {
            return Collections.EMPTY_LIST;
        } else {
            List<Method> annotatedMethods = new ArrayList<Method>();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getAnnotation(annotation) != null) {
                    annotatedMethods.add(method);
                }
            }
            annotatedMethods.addAll(getMethodsAnnotatedWith(clazz.getSuperclass(), annotation));
            return annotatedMethods;
        }
    }

    /**
     * todo javadoc
     * @param clazz
     * @param annotation
     * @return
     */
    public static <T extends Annotation> T getClassAnnotation(Class clazz, Class<T> annotation) {

        if (Object.class.equals(clazz)) {
            return null;
        } else {
            T foundAnnotation = (T) clazz.getAnnotation(annotation);
            if (foundAnnotation != null) {
                return foundAnnotation;
            } else {
                return getClassAnnotation(clazz.getSuperclass(), annotation);
            }
        }
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
