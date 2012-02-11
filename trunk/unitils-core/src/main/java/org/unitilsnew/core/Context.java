/*
 * Copyright 2011,  Unitils.org
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

package org.unitilsnew.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;
import org.unitilsnew.core.annotation.Classifier;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tim Ducheyne
 */
public class Context {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(Context.class);

    protected Configuration configuration;
    protected Map<Key, Object> instances = new HashMap<Key, Object>();


    public Context(Configuration configuration) {
        this.configuration = configuration;
        setInstanceOfType(Configuration.class, configuration);
    }

    public <T> void setInstanceOfType(Class<T> type, T implementationType, String... classifiers) {
        Key key = new Key(type, classifiers);
        instances.put(key, implementationType);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getInstanceOfType(Class<T> type, String... classifiers) {
        if (classifiers != null && classifiers.length == 0) {
            classifiers = null;
        }
        Key key = new Key(type, classifiers);

        T instance = (T) instances.get(key);
        if (instance == null) {
            assertNoCycleDependency(key);

            instances.put(key, null);
            try {
                instance = createInstanceOfType(type, classifiers);
                logger.debug("Created instance for type " + key + ": " + instance.getClass().getName());

            } catch (Exception e) {
                throw new UnitilsException("Unable to create instance for type " + key + "\n" + e.getMessage(), e);
            }
            instances.put(key, instance);
        }
        return instance;
    }


    @SuppressWarnings("unchecked")
    protected <T> T createInstanceOfType(Class<T> type, String... classifiers) {
        Class<?> implementationType = getImplementationType(type, classifiers);
        Constructor<?>[] constructors = implementationType.getConstructors();
        if (constructors.length > 1) {
            throw new UnitilsException("Found more than 1 constructor in implementation type " + implementationType.getName());
        }

        Object[] arguments;
        Class<?>[] argumentTypes;
        if (constructors.length == 0) {
            arguments = new Object[0];
            argumentTypes = new Class[0];

        } else {
            Constructor<?> constructor = constructors[0];
            argumentTypes = constructor.getParameterTypes();
            Annotation[][] argumentAnnotations = constructor.getParameterAnnotations();

            arguments = new Object[argumentTypes.length];
            for (int i = 0; i < argumentTypes.length; i++) {
                arguments[i] = getArgumentInstance(argumentTypes[i], argumentAnnotations[i]);
            }
        }
        Object instance = ReflectionUtils.createInstanceOfType(implementationType, true, argumentTypes, arguments);
        if (instance instanceof Factory) {
            instance = ((Factory) instance).create();
        }
        if (!type.isAssignableFrom(instance.getClass())) {
            throw new UnitilsException("Implementation type " + instance.getClass().getName() + " is not of type " + type.getName());
        }
        return (T) instance;
    }

    protected Object getArgumentInstance(Class<?> argumentType, Annotation[] argumentAnnotations) {
        Property propertyAnnotation = getPropertyAnnotation(argumentAnnotations);
        Classifier classifierAnnotation = getClassifierAnnotation(argumentAnnotations);

        String[] argumentClassifiers = classifierAnnotation == null ? null : classifierAnnotation.value();

        Object instance;
        if (propertyAnnotation != null) {
            instance = configuration.getValueOfType(argumentType, propertyAnnotation.value(), argumentClassifiers);
        } else {
            instance = getInstanceOfType(argumentType, argumentClassifiers);
        }
        return instance;
    }

    protected Property getPropertyAnnotation(Annotation[] argumentAnnotations) {
        for (Annotation argumentAnnotation : argumentAnnotations) {
            if (argumentAnnotation instanceof Property) {
                return (Property) argumentAnnotation;
            }
        }
        return null;
    }

    protected Classifier getClassifierAnnotation(Annotation[] argumentAnnotations) {
        for (Annotation argumentAnnotation : argumentAnnotations) {
            if (argumentAnnotation instanceof Classifier) {
                return (Classifier) argumentAnnotation;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected Class<?> getImplementationType(Class<?> type, String... classifiers) {
        String propertyName = type.getName();
        String value = configuration.getOptionalString(propertyName, classifiers);
        if (value == null) {
            // no config found, default to given type
            if (type.isInterface()) {
                throw new UnitilsException("No implementation type configured for given interface type " + type.getName());
            }
            return type;
        }

        Class<?> implementationType;
        try {
            implementationType = Class.forName(value);
        } catch (Exception e) {
            throw new UnitilsException("Invalid implementation type " + value, e);
        }
        if (implementationType.isInterface()) {
            throw new UnitilsException("Interface found as implementation type of " + implementationType.getName());
        }
        if (!type.isAssignableFrom(implementationType) && !Factory.class.isAssignableFrom(implementationType)) {
            throw new UnitilsException("Implementation type " + implementationType.getName() + " is not of type " + type.getName() + " or " + Factory.class.getName() + "<" + type.getName() + ">");
        }
        return implementationType;
    }


    protected <T> void assertNoCycleDependency(Key key) {
        if (!instances.containsKey(key)) {
            return;
        }

        StringBuilder message = new StringBuilder("Unable to create instance for type " + key + ": cyclic dependency detected between following types:\n");
        for (Map.Entry<Key, Object> entry : instances.entrySet()) {
            if (entry.getValue() == null) {
                message.append(entry.getKey());
                message.append('\n');
            }
        }
        throw new UnitilsException(message.toString());
    }


    protected static class Key {

        private Class<?> type;
        private String[] classifiers;

        public Key(Class<?> type, String... classifiers) {
            this.type = type;
            this.classifiers = classifiers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;
            if (type != null ? !type.equals(key.type) : key.type != null) {
                return false;
            }
            if (!Arrays.equals(classifiers, key.classifiers)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (classifiers != null ? Arrays.hashCode(classifiers) : 0);
            return result;
        }

        @Override
        public String toString() {
            String typeName = type == null ? "null" : type.getName();
            if (classifiers == null || classifiers.length == 0) {
                return typeName;
            }
            return typeName + " (classifiers: " + Arrays.toString(classifiers) + ")";
        }
    }
}
