package be.ordina.unitils.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 */
public class AnnotationUtils {

    //todo javadoc
    public static final String DEFAULT_ENUM_VALUE_NAME = "DEFAULT";

    /**
     * Returns the given' class's declared fields that are marked with the given annotation
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
            if (defaultValueName.equals(enumValue.name())) {

                return enumValue;
            }
        }
        throw new RuntimeException("todo");
    }
}
