/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.reflectionassert.util;

import org.unitils.core.UnitilsException;

/**
 * Utility class for handling Hibernate proxies during the comparison.
 * <p/>
 * Every operation is performed through reflection to avoid a direct link to Hibernate. This way you do not
 * need Hibernate in the classpath to use the reflection comparator.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateUtil {

    /**
     * The hibernate proxy type, null if the class is not found in the classpath
     */
    protected static Class<?> hibernateProxyClass;

    static {
        try {
            hibernateProxyClass = Class.forName("org.hibernate.proxy.HibernateProxy");

        } catch (ClassNotFoundException e) {
            // hibernate not found in the classpath
            hibernateProxyClass = null;
        }
    }


    /**
     * Checks whether the given ojbect is a HibernateProxy instance.
     *
     * @param object The object
     * @return True if the object is a proxy
     */
    public static boolean isHibernateProxy(Object object) {
        return hibernateProxyClass != null && hibernateProxyClass.isInstance(object);
    }

    /**
     * Checks whether the given proxy object has been loaded.
     *
     * @param object The object or proxy
     * @return True if the object is a proxy and has been loaded
     */
    public static boolean isUninitialized(Object object) {
        if (!isHibernateProxy(object)) {
            return false;
        }
        return (Boolean) invokeLazyInitializerMethod("isUninitialized", object);
    }


    /**
     * Gets the class name of the proxied object
     *
     * @param object The object or proxy
     * @return The class name of the object, null if the object is null
     */
    public static String getEntityName(Object object) {
        if (!isHibernateProxy(object)) {
            return object == null ? null : object.getClass().getName();
        }
        return (String) invokeLazyInitializerMethod("getEntityName", object);
    }

    /**
     * Gets the unique identifier of the given proxy object.
     *
     * @param object The object or proxy
     * @return The identifier or null if the object was not a proxy
     */
    public static Object getIdentifier(Object object) {
        if (!isHibernateProxy(object)) {
            return null;
        }
        return invokeLazyInitializerMethod("getIdentifier", object);
    }

    /**
     * Gets (and loads) the wrapped object out of a given hibernate proxy.
     * <p/>
     * If the given object is not a proxy or if Hibernate is not found in the classpath, this method just returns
     * the given object. If the given object is a proxy, the proxy is initialized (loaded) and the un-wrapped object
     * is returned.
     *
     * @param object The object or proxy
     * @return The unproxied object or the object itself if it was no proxy
     */
    public static Object getUnproxiedValue(Object object) {
        // check whether object is a proxy
        if (!isHibernateProxy(object)) {
            return object;
        }
        // found a proxy, load and un-wrap
        Object session = invokeLazyInitializerMethod("getSession", object);
        if (session == null) {
            // detached, do not try to initialize
            return object;
        }
        return invokeLazyInitializerMethod("getImplementation", object);
    }


    /**
     * Invokes the given method on the LazyInitializer that is associated with the given proxy.
     *
     * @param methodName The method to invoke, not null
     * @param proxy      The hibernate proxy instance, not null
     * @return The result value of the method call
     */
    protected static Object invokeLazyInitializerMethod(String methodName, Object proxy) {
        try {
            Object lazyInitializer = hibernateProxyClass.getMethod("getHibernateLazyInitializer").invoke(proxy);
            return lazyInitializer.getClass().getMethod(methodName).invoke(lazyInitializer);
        } catch (Exception e) {
            throw new UnitilsException("Unable to invoke method on lazy initializer of Hibernate proxy. Method: " + methodName + ", proxy: " + proxy, e);
        }
    }

}