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
import org.unitilsnew.core.reflect.FieldWrapper;
import org.unitilsnew.core.reflect.OriginalFieldValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectionByTypeService {


    public List<OriginalFieldValue> injectIntoAllByType(List<?> targets, ObjectToInject objectToInject, boolean failWhenNoMatch) {
        if (targets == null) {
            throw new UnitilsException("Unable to inject into by type. Targets cannot be null.");
        }
        List<OriginalFieldValue> originalFieldValues = new ArrayList<OriginalFieldValue>();
        for (Object target : targets) {
            OriginalFieldValue originalFieldValue = injectIntoByType(target, objectToInject, failWhenNoMatch);
            originalFieldValues.add(originalFieldValue);
        }
        return originalFieldValues;
    }


    /**
     * Performs auto-injection by type of the objectToInject on the target object.
     *
     * @param target         The object into which the objectToInject is injected, not null
     * @param objectToInject The object that is injected, not null
     * @return The object that was replaced by the injection
     */
    public OriginalFieldValue injectIntoByType(Object target, ObjectToInject objectToInject, boolean failWhenNoMatch) {
        if (objectToInject == null) {
            throw new UnitilsException("Unable to inject into by type. Object to inject cannot be null.");
        }
        Object value = objectToInject.getValue();
        Type type = objectToInject.getType();
        if (type == null) {
            throw new UnitilsException("Unable to inject into by type. Type cannot be null.");
        }
        if (target == null) {
            throw new UnitilsException("Unable to inject into by type '" + type + "'. Target cannot be null.");
        }

        ClassWrapper targetClassWrapper = new ClassWrapper(target.getClass());
        List<FieldWrapper> targetFieldWrappers = targetClassWrapper.getFieldsOfType(type);
        if (targetFieldWrappers.isEmpty()) {
            targetFieldWrappers = targetClassWrapper.getFieldsAssignableFrom(type);
        }
        if (targetFieldWrappers.isEmpty()) {
            if (failWhenNoMatch) {
                throw new UnitilsException("Unable to inject into by type '" + type + "'.\n" +
                        "No field of matching type exists on class " + targetClassWrapper + " or one of its superclasses.");
            }
            // ignored
            return null;
        }
        removeLeastMatchingFields(targetFieldWrappers);
        if (targetFieldWrappers.size() > 1) {
            throw new UnitilsException("Unable to inject into by type '" + type + "'.\n" +
                    "More than one field with matching type found in class " + targetClassWrapper + " or one of its superclasses. Matching fields: " + targetFieldWrappers + ".\n" +
                    "Specify the target field explicitly instead of injecting into by type.");
        }
        FieldWrapper targetFieldWrapper = targetFieldWrappers.get(0);
        try {
            return targetFieldWrapper.setValue(value, target);
        } catch (Exception e) {
            throw new UnitilsException("Unable to inject into by type '" + type + "'.", e);
        }
    }

    /**
     * Performs auto-injection by type of the objectToInject into the target class.
     *
     * @param targetClass    The class into which the objectToInject is injected, not null
     * @param objectToInject The object that is injected, not null
     * @return The object that was replaced by the injection
     */
    public OriginalFieldValue injectIntoStaticByType(Class<?> targetClass, ObjectToInject objectToInject, boolean failWhenNoMatch) {
        if (objectToInject == null) {
            throw new UnitilsException("Unable to inject into static by type. Object to inject cannot be null.");
        }
        Object value = objectToInject.getValue();
        Type type = objectToInject.getType();
        if (type == null) {
            throw new UnitilsException("Unable to inject into static by type. Type cannot be null.");
        }
        if (targetClass == null) {
            throw new UnitilsException("Unable to inject into static by type '" + type + "'. Target class cannot be null.");
        }

        ClassWrapper targetClassWrapper = new ClassWrapper(targetClass);
        List<FieldWrapper> targetFieldWrappers = targetClassWrapper.getStaticFieldsOfType(type);
        if (targetFieldWrappers.isEmpty()) {
            targetFieldWrappers = targetClassWrapper.getStaticFieldsAssignableFrom(type);
        }
        if (targetFieldWrappers.isEmpty()) {
            if (failWhenNoMatch) {
                throw new UnitilsException("Unable to inject into static by type '" + type + "'.\n" +
                        "No static field of matching type exists on class " + targetClassWrapper + " or one of its superclasses.");
            }
            // ignored
            return null;
        }
        removeLeastMatchingFields(targetFieldWrappers);
        if (targetFieldWrappers.size() > 1) {
            throw new UnitilsException("Unable to inject into static by type '" + type + "'.\n" +
                    "More than one static field with matching type found in class " + targetClassWrapper + " or one of its superclasses. Matching fields: " + targetFieldWrappers + ".\n" +
                    "Specify the target field explicitly instead of injecting into by type.");
        }
        FieldWrapper targetFieldWrapper = targetFieldWrappers.get(0);
        try {
            return targetFieldWrapper.setValue(value, null);
        } catch (Exception e) {
            throw new UnitilsException("Unable to inject into static by type '" + type + "'.", e);
        }
    }


    protected void removeLeastMatchingFields(List<FieldWrapper> fieldWrappers) {
        Iterator<FieldWrapper> iterator = fieldWrappers.iterator();
        while (iterator.hasNext()) {
            FieldWrapper fieldWrapper = iterator.next();

            for (FieldWrapper compareToFieldWrapper : fieldWrappers) {
                Type compareToType = compareToFieldWrapper.getGenericType();
                if (fieldWrapper != compareToFieldWrapper && !fieldWrapper.isOfType(compareToType) && fieldWrapper.isAssignableFrom(compareToType)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
