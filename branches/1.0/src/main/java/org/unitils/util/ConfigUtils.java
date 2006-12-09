package org.unitils.util;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.unitils.core.UnitilsException;

/**
 * Class containing configuration related utilities
 *
 * @author Filip Neven
 */
public class ConfigUtils {

    private static final Logger logger = Logger.getLogger(ConfigUtils.class);

    /**
     * @param type The type of the instance
     * @param configuration The configuration containing the necessary properties for configuring the instance
     * @return The instance of the given class, as configured by the given <code>Configuration</code> instance.
     *  The configuration should contain a property with as key the fully qualified name of the interface type followed by
     * '.impl.className', and as value the fully qualified classname of the implementation type.
     */
    public static <T> T getConfiguredInstance(Class type, Configuration configuration) {

        String propKey = type.getName() + ".implClassName";
        logger.debug("Creating instance of " + type + ". Concrete implementation class is defined by the property " + propKey);
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
        logger.debug("Creating instance of " + type + ". Trying to retrieve concrete implementation class from the property "
                + implementationSpecificPropKey);
        if (configuration.containsKey(implementationSpecificPropKey)) {
            return (T) ReflectionUtils.createInstanceOfType(configuration.getString(implementationSpecificPropKey));
        } else {
            logger.debug("Property " + implementationSpecificPropKey + " not specified. Trying to retrieve concrete " +
                    "implementation class from the property " + propKey);
            if (configuration.containsKey(propKey)) {
                return (T) ReflectionUtils.createInstanceOfType(configuration.getString(propKey));
            } else {
                throw new UnitilsException("Missing configuration for " + propKey);
            }
        }
    }
}
