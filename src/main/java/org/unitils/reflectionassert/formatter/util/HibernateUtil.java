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
package org.unitils.reflectionassert.formatter.util;

import org.unitils.core.UnitilsException;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateUtil {


    private static Class<?> hibernateProxyClass;

    static {
        try {
            hibernateProxyClass = Class.forName("org.hibernate.proxy.HibernateProxy");

        } catch (ClassNotFoundException e) {
            hibernateProxyClass = null;
        }
    }


    /**
     * todo javadoc
     *
     * @param object The object or proxy
     * @return The uproxied object or the object itself if it was no proxy
     */
    public static Object getUnproxiedValue(Object object) {

        if (hibernateProxyClass == null || !hibernateProxyClass.isInstance(object)) {
            return object;
        }

        try {
            Object lazyInitializer = hibernateProxyClass.getMethod("getHibernateLazyInitializer").invoke(object);
            return lazyInitializer.getClass().getMethod("getImplementation").invoke(lazyInitializer);

        } catch (Exception e) {
            throw new UnitilsException("Unable to get unproxied value. Object: " + object, e);
        }
    }

}