package org.unitils.util;

import org.apache.commons.configuration.Configuration;

/**
 * Class containing configuration related utilities
 */
public class ConfigUtils {

    /**
     * @param type
     * @param configuration
     * @return The instance of the given class, as configured by the given <code>Configuration</code> instance.
     *  The configuration should contain a property with as key the fully qualified name of the interface type followed by
     * '.impl.className', and as value the fully qualified classname of the implementation type.
     */
    public static <T> T getConfiguredInstance(Class type, Configuration configuration) {
        String propKey = type.getName() + ".implClassName";
        return (T) ReflectionUtils.createInstanceOfType(configuration.getString(propKey));
    }
}
