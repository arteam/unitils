package be.ordina.unitils.inject;

import be.ordina.unitils.module.TestContext;
import be.ordina.unitils.module.TestListener;
import be.ordina.unitils.module.UnitilsModule;
import be.ordina.unitils.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Filip Neven
 */
public class InjectModule implements UnitilsModule {


    public TestListener createTestListener() {
        return new InjectTestListener();
    }

    private class InjectTestListener extends TestListener {
        public void beforeTestMethod() {

            List<Field> fieldsToInject = AnnotationUtils.getFieldsAnnotatedWith(TestContext.getTestClass(), Inject.class);
            for (Field fieldToInject : fieldsToInject) {
                Inject injectAnnotation = fieldToInject.getAnnotation(Inject.class);

                injectAnnotation.target();
            }
        }
    }

}
