package be.ordina.unitils.inject;

import be.ordina.unitils.module.UnitilsModule;
import be.ordina.unitils.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public class InjectModule implements UnitilsModule {

    public void beforeSuite(Properties unitilsProperties) throws Exception {
        // Nothing to do
    }

    public void beforeClass(Object test) throws Exception {
        // Nothing to do
    }

    public void beforeTestMethod(Object test, String methodName) {
        List<Field> fieldsToInject = AnnotationUtils.getFieldsAnnotatedWith(test.getClass(), Inject.class);
        for (Field fieldToInject : fieldsToInject) {
            Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);
            List targets;
            String targetName = injectAnnotation.target();

        }
    }

    public Class[] getModulesDependingOn() {
        return new Class[]{};
    }
}
