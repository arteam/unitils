package be.ordina.unitils.inject;

import be.ordina.unitils.module.BaseUnitilsModule;
import be.ordina.unitils.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Filip Neven
 */
public class InjectModule extends BaseUnitilsModule {


    public void beforeTestMethod(Object test, String methodName) {
        List<Field> fieldsToInject = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), Inject.class);
        for (Field fieldToInject : fieldsToInject) {
            Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);

            injectAnnotation.target();
        }
    }

}
