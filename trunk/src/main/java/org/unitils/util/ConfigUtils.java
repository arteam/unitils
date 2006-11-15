package org.unitils.util;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.UnitilsException;

/**
 * Class containing configuration related utilities
 */
public class ConfigUtils {

    /**
     * @param type The type of the instance
     * @param configuration The configuration containing the necessary properties for configuring the instance
     * @return The instance of the given class, as configured by the given <code>Configuration</code> instance.
     *  The configuration should contain a property with as key the fully qualified name of the interface type followed by
     * '.impl.className', and as value the fully qualified classname of the implementation type.
     */
    public static <T> T getConfiguredInstance(Class type, Configuration configuration) {
        String propKey = type.getName() + ".implClassName";
        return (T) ReflectionUtils.createInstanceOfType(configuration.getString(propKey));
    }

    /**
     * Retrieves the concrete instance of the class with the given type as configured by the given <code>Configuration</code>.
     * Tries to retrieve a specific implementation first (propery key = fully qualified name of the interface
     * type + '.impl.className.' + implementationDiscriminatorValue). If this key does not exist, the generally configured
     * instance is retrieved (same property key without the implementationDiscriminatorValue).
     *
     * @param type The type of the instance
     * @param configuration The configuration containing the necessary properties for configuring the instance
     * @param implementationDiscriminatorValue The value that defines which specific implementation class should be used.
     *              This is typically an environment specific property, like the DBMS that is used.
     * @return The configured instance
     */
    public static <T> T getConfiguredInstance(Class type, Configuration configuration, String implementationDiscriminatorValue) {
        String propKey = type.getName() + ".implClassName";
        String implementationSpecificPropKey = propKey + "." + implementationDiscriminatorValue;
        if (configuration.containsKey(implementationSpecificPropKey)) {
            return (T) ReflectionUtils.createInstanceOfType(configuration.getString(implementationSpecificPropKey));
        } else if (configuration.containsKey(propKey)) {
            return (T) ReflectionUtils.createInstanceOfType(configuration.getString(propKey));
        } else {
            throw new UnitilsException("Missing configuration for " + propKey);
        }
    }
}
