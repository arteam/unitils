package be.ordina.unitils.module;

import java.util.Properties;

/**
 * @author Filip Neven
 */
public interface UnitilsModule {

    void beforeSuite(Properties unitilsProperties) throws Exception;

    void beforeClass(Object test) throws Exception;

    void beforeTestMethod(Object test, String methodName);

    Class[] getModulesDependingOn();

}
