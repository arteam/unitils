/*
 * Copyright 2012,  Unitils.org
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

package org.unitils.inject.core;

import org.unitils.core.UnitilsException;
import org.unitilsnew.core.reflect.ClassWrapper;
import org.unitilsnew.core.reflect.CompositeFieldWrapper;
import org.unitilsnew.core.reflect.OriginalFieldValue;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectionService {


    public List<OriginalFieldValue> injectIntoAll(List<?> targets, String property, ObjectToInject objectToInject, boolean autoCreateInnerFields) {
        if (targets == null) {
            throw new UnitilsException("Unable to inject into all. Targets cannot be null.");
        }
        List<OriginalFieldValue> originalFieldValues = new ArrayList<OriginalFieldValue>();
        for (Object target : targets) {
            OriginalFieldValue originalFieldValue = injectInto(target, property, objectToInject, autoCreateInnerFields);
            originalFieldValues.add(originalFieldValue);
        }
        return originalFieldValues;
    }

    public OriginalFieldValue injectInto(Object target, String property, ObjectToInject objectToInject, boolean autoCreateInnerFields) {
        if (objectToInject == null) {
            throw new UnitilsException("Unable to inject into. Object to inject cannot be null.");
        }
        if (isBlank(property)) {
            throw new UnitilsException("Unable to inject into. Property cannot be null or empty.");
        }
        if (target == null) {
            throw new UnitilsException("Unable to inject into property '" + property + "'. Target cannot be null.");
        }
        try {
            return doInjectInto(target, target.getClass(), property, objectToInject, autoCreateInnerFields);

        } catch (Exception e) {
            throw new UnitilsException("Unable to inject into property '" + property + "' with target of type " + target.getClass().getName() + ". Reason:\n" + e.getMessage(), e);
        }
    }

    public OriginalFieldValue injectIntoStatic(Class<?> targetClass, String property, ObjectToInject objectToInject, boolean autoCreateInnerFields) {
        if (objectToInject == null) {
            throw new UnitilsException("Unable to inject into static. Object to inject cannot be null.");
        }
        if (isBlank(property)) {
            throw new UnitilsException("Unable to inject into static. Property cannot be null or empty.");
        }
        if (targetClass == null) {
            throw new UnitilsException("Unable to inject into static property '" + property + "'. Target class cannot be null.");
        }
        try {
            return doInjectInto(null, targetClass, property, objectToInject, autoCreateInnerFields);

        } catch (Exception e) {
            throw new UnitilsException("Unable to inject into static property '" + property + "' with target class " + targetClass.getName() + ". Reason:\n" + e.getMessage(), e);
        }
    }


    protected OriginalFieldValue doInjectInto(Object target, Class<?> targetClass, String property, ObjectToInject objectToInject, boolean autoCreateInnerFields) {
        Object value = objectToInject.getValue();

        ClassWrapper targetClassWrapper = new ClassWrapper(targetClass);
        CompositeFieldWrapper compositeField = targetClassWrapper.getCompositeField(property);
        return compositeField.setValue(value, target, autoCreateInnerFields);
    }
}
