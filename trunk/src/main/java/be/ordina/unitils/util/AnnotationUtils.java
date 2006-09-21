package be.ordina.unitils.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 */
public class AnnotationUtils {

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
}
