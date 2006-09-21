package be.ordina.unitils;

import be.ordina.unitils.module.UnitilsModule;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.ReflectionUtils;
import be.ordina.unitils.util.UnitilsProperties;

import java.util.*;

/**
 * @author Filip Neven
 */
public class Unitils {

    private static final String PROPKEY_MODULE_START = "module.";

    private static Properties unitilsProperties;

    List<UnitilsModule> modules;

    private static ThreadLocal currentTestHolder = new ThreadLocal();

    private static ThreadLocal<String> currentMethodNameHolder = new ThreadLocal<String>();

    public void beforeSuite() throws Exception {
        // Loading module will be done the first time only
        unitilsProperties = UnitilsProperties.loadProperties(UnitilsProperties.DEFAULT_PROPERTIES_FILE_NAME);
        loadModules(unitilsProperties);
        // For each module, invoke the init method
        for (UnitilsModule module : modules) {
            module.beforeSuite(unitilsProperties);
        }
    }

    private void loadModules(Properties unitilsProperties) {
        modules = new ArrayList<UnitilsModule>();
        Set<String> modulePropKeys = PropertiesUtils.getPropertyKeysStartingWith(unitilsProperties, PROPKEY_MODULE_START);
        for (String modulePropKey : modulePropKeys) {
            String moduleClassName = PropertiesUtils.getPropertyRejectNull(unitilsProperties, modulePropKey);
            UnitilsModule module = ReflectionUtils.getInstance(moduleClassName);
            modules.add(module);
        }
        Collections.sort(modules, new Comparator<UnitilsModule>() {
            public int compare(UnitilsModule module1, UnitilsModule module2) {
                if (dependentOn(module1, module2)) {
                    return 1;
                } else if (dependentOn(module2, module1)) {
                    return -1;
                } else {
                    return 0;
                }
            }

            private boolean dependentOn(UnitilsModule module1, UnitilsModule module2) {
                return Arrays.asList(module1.getModulesDependingOn()).contains(module2.getClass());
            }
        });
    }

    public void beforeClass(Object test) throws Exception {
        for (UnitilsModule module : modules) {
            module.beforeClass(test);
        }
    }

    public void beforeMethod(Object test, String methodName) {
        currentTestHolder.set(test);
        currentMethodNameHolder.set(methodName);
        // For each module, invoke the beforeTestMethod method
        for (UnitilsModule module : modules) {
            module.beforeTestMethod(test, methodName);
        }
    }

    public static Object getCurrentTest() {
        return currentTestHolder.get();
    }

    public static String getCurrentMethodName() {
        return currentMethodNameHolder.get();
    }

}