package be.ordina.unitils.module;

/**
 * todo javadoc
 *
 * @author Filip Neven
 */
public interface UnitilsModule {

    public void beforeAll() throws Exception;

    public void beforeTestClass(Object test) throws Exception;

    public void beforeTestMethod(Object test, String methodName) throws Exception;

    public void afterTestMethod(Object test, String methodName) throws Exception;

    public void afterTestClass(Object test) throws Exception;

    public void afterAll() throws Exception;

}
