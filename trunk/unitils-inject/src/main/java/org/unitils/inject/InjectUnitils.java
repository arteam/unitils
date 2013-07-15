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

package org.unitils.inject;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.reflect.OriginalFieldValue;
import org.unitils.inject.core.InjectionByTypeService;
import org.unitils.inject.core.InjectionService;
import org.unitils.inject.core.ObjectToInject;

/**
 * @author Tim Ducheyne
 */
public class InjectUnitils {

    protected static InjectionService injectionService = Unitils.getInstanceOfType(InjectionService.class);
    protected static InjectionByTypeService injectionByTypeService = Unitils.getInstanceOfType(InjectionByTypeService.class);


    public static OriginalFieldValue injectInto(Object target, String property, Object value) {
        return injectInto(target, property, value, true);
    }

    public static OriginalFieldValue injectInto(Object target, String property, Object value, boolean autoCreateInnerFields) {
        ObjectToInject objectToInject = new ObjectToInject(value);
        return injectionService.injectInto(target, property, objectToInject, autoCreateInnerFields);
    }


    public static OriginalFieldValue injectIntoStatic(Class<?> targetClass, String property, Object value) {
        return injectIntoStatic(targetClass, property, value, true);
    }

    public static OriginalFieldValue injectIntoStatic(Class<?> targetClass, String property, Object value, boolean autoCreateInnerFields) {
        ObjectToInject objectToInject = new ObjectToInject(value);
        return injectionService.injectIntoStatic(targetClass, property, objectToInject, autoCreateInnerFields);
    }


    public static OriginalFieldValue injectIntoByType(Object target, Object value) {
        if (value == null) {
            throw new UnitilsException("Unable to inject into by type. Unable to determine type from value: value is null. Please specify a type explicitly.");
        }
        Class<?> type = value.getClass();
        return injectIntoByType(target, value, type);
    }

    public static OriginalFieldValue injectIntoByType(Object target, Object value, Class<?> type) {
        return injectIntoByType(target, value, type, true);
    }

    public static OriginalFieldValue injectIntoByType(Object target, Object value, Class<?> type, boolean failWhenNoMatch) {
        ObjectToInject objectToInject = new ObjectToInject(value, type);
        return injectionByTypeService.injectIntoByType(target, objectToInject, failWhenNoMatch);
    }


    public static OriginalFieldValue injectIntoStaticByType(Class<?> targetClass, Object value) {
        if (value == null) {
            throw new UnitilsException("Unable to inject into static by type. Unable to determine type from value: value is null. Please specify a type explicitly.");
        }
        Class<?> type = value.getClass();
        return injectIntoStaticByType(targetClass, value, type);
    }

    public static OriginalFieldValue injectIntoStaticByType(Class<?> targetClass, Object value, Class<?> type) {
        return injectIntoStaticByType(targetClass, value, type, true);
    }

    public static OriginalFieldValue injectIntoStaticByType(Class<?> targetClass, Object value, Class<?> type, boolean failWhenNoMatch) {
        ObjectToInject objectToInject = new ObjectToInject(value, type);
        return injectionByTypeService.injectIntoStaticByType(targetClass, objectToInject, failWhenNoMatch);
    }
}
