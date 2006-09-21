package be.ordina.unitils.testing;

import java.util.Properties;
import java.util.List;

/**
 * @author Filip Neven
 */
public interface UnitilsModule {

    void beforeSuite(Properties unitilsProperties) throws Exception;

    void beforeClass(Object test) throws Exception;

    void beforeTestMethod(Object test, String methodName);

    Class[] getModulesDependingOn();

}
