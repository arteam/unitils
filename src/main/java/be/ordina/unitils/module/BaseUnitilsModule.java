package be.ordina.unitils.module;

/**
 * todo javadoc
 */
public abstract class BaseUnitilsModule implements UnitilsModule {

    public void beforeAll() throws Exception {
        // empty
    }

    public void beforeTestClass(Object test) throws Exception {
        // empty
    }

    public void beforeTestMethod(Object test, String methodName) throws Exception {
        // empty
    }

    public void afterTestMethod(Object test, String methodName) throws Exception {
        // empty
    }

    public void afterTestClass(Object test) throws Exception {
        // empty
    }

    public void afterAll() throws Exception {
        // empty
    }
}
