/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.Properties;

/**
 * Class containing configuration related utilities
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ConfigUtils {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ConfigUtils.class);


    /**
     * Retrieves the concrete instance of the class with the given type as configured by the given <code>Configuration</code>.
     * Tries to retrieve a specific implementation first (propery key = fully qualified name of the interface
     * type + '.impl.className.' + implementationDiscriminatorValue). If this key does not exist, the generally configured
     * instance is retrieved (same property key without the implementationDiscriminatorValue).
     *
     * @param type          The type of the instance
     * @param configuration The configuration containing the necessary properties for configuring the instance
     * @param implementationDiscriminatorValues
     *                      The values that define which specific implementation class should be used.
     *                      This is typically an environment specific property, like the DBMS that is used.
     * @return The configured instance
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getConfiguredInstance(Class type, Properties configuration, String... implementationDiscriminatorValues) {
        String implClassName = getConfiguredClassName(type, configuration, implementationDiscriminatorValues);
        logger.debug("Creating instance of " + type + ". Implementation class " + implClassName);
        return (T) createInstanceOfType(implClassName);
    }


    /**
     * Retrieves the class name of the concrete instance of the class with the given type as configured by the given <code>Configuration</code>.
     * Tries to retrieve a specific implementation first (propery key = fully qualified name of the interface
     * type + '.impl.className.' + implementationDiscriminatorValue). If this key does not exist, the generally configured
     * instance is retrieved (same property key without the implementationDiscriminatorValue).
     *
     * @param type          The type of the instance
     * @param configuration The configuration containing the necessary properties for configuring the instance
     * @param implementationDiscriminatorValues
     *                      The values that define which specific implementation class should be used.
     *                      This is typically an environment specific property, like the DBMS that is used.
     * @return The configured class name
     */
    public static String getConfiguredClassName(Class type, Properties configuration, String... implementationDiscriminatorValues) {
        String propKey = type.getName() + ".implClassName";

        // first try specific instance using the given discriminators
        if (implementationDiscriminatorValues != null) {
            String implementationSpecificPropKey = propKey;
            for (String implementationDiscriminatorValue : implementationDiscriminatorValues) {
                implementationSpecificPropKey += '.' + implementationDiscriminatorValue;
            }
            if (configuration.containsKey(implementationSpecificPropKey)) {
                return PropertyUtils.getString(implementationSpecificPropKey, configuration);
            }
        }

        // specifig not found, try general configured instance
        if (configuration.containsKey(propKey)) {
            return PropertyUtils.getString(propKey, configuration);
        }

        // no configuration found
        throw new UnitilsException("Missing configuration for " + propKey);
    }

}
