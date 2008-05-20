/*
 * Copyright 2006-2007,  Unitils.org
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
     * Gets (and loads) the wrapped object out of a given hibernate proxy.
     * <p/>
     * If the given object is not a proxy or if Hibernate is not found in the classpath, this method just returns
     * the given object. If the given object is a proxy, the proxy is initialized (loaded) and the un-wrapped object
     * is returned.
     *
     * @param object The object or proxy
     * @return The uproxied object or the object itself if it was no proxy
     */
    public static Object getUnproxiedValue(Object object) {
        // check whether object is a proxy
        if (hibernateProxyClass == null || !hibernateProxyClass.isInstance(object)) {
            return object;
        }
        try {
            // found a proxy, load and un-wrap
            Object lazyInitializer = hibernateProxyClass.getMethod("getHibernateLazyInitializer").invoke(object);
            return lazyInitializer.getClass().getMethod("getImplementation").invoke(lazyInitializer);

        } catch (Exception e) {
            throw new UnitilsException("Unable to get unproxied value. Object: " + object, e);
        }
    }

}