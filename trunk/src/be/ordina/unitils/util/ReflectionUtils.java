/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.util;

import java.lang.reflect.Constructor;

/**
 * Utility methods that use reflection in some way.
 */
public class ReflectionUtils {

    /**
     * Creates an instance of the class with the given name. The class's no argument constructor is used to create an
     * instance.
     *
     * @param className The name of the class
     * @return An instance of this class
     */
    public static <T> T getInstance(String className) {
        try {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor();
            return (T) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class " + className + " identified not found");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + className + " does not contain no-argument constructor");
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create object of class " + className);
        }
    }
}
