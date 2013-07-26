
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

package org.unitils.inject.listener;

import org.unitils.core.FieldAnnotationListener;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.TestPhase;
import org.unitils.core.reflect.Annotations;
import org.unitils.core.reflect.OriginalFieldValue;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.core.InjectionByTypeService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.inject.util.Restore;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.unitils.core.TestPhase.INJECTION;
import static org.unitils.inject.util.Restore.NULL_OR_0_VALUE;
import static org.unitils.inject.util.Restore.OLD_VALUE;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoStaticByTypeFieldAnnotationListener extends FieldAnnotationListener<InjectIntoStaticByType> {

    protected InjectionByTypeService injectionByTypeService;

    protected Map<TestField, OriginalFieldValue> originalFieldValues = new HashMap<TestField, OriginalFieldValue>(3);


    public InjectIntoStaticByTypeFieldAnnotationListener(InjectionByTypeService injectionByTypeService) {
        this.injectionByTypeService = injectionByTypeService;
    }


    @Override
    public TestPhase getTestPhase() {
        return INJECTION;
    }


    @Override
    public void beforeTestMethod(TestInstance testInstance, TestField testField, Annotations<InjectIntoStaticByType> annotations) {
        InjectIntoStaticByType annotation = annotations.getAnnotationWithDefaults();

        Object value = testField.getValue();
        Type genericType = testField.getGenericType();
        Class<?> targetClass = annotation.target();
        boolean failWhenNoMatch = annotation.failWhenNoMatch();

        ObjectToInject objectToInject = new ObjectToInject(value, genericType);
        OriginalFieldValue originalFieldValue = injectionByTypeService.injectIntoStaticByType(targetClass, objectToInject, failWhenNoMatch);
        originalFieldValues.put(testField, originalFieldValue);
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, TestField testField, Annotations<InjectIntoStaticByType> annotations, Throwable testThrowable) {
        OriginalFieldValue originalFieldValue = originalFieldValues.get(testField);
        if (originalFieldValue == null) {
            // ignore, nothing to restore
            return;
        }
        InjectIntoStaticByType annotation = annotations.getAnnotationWithDefaults();
        Restore restore = annotation.restore();
        if (restore == OLD_VALUE) {
            originalFieldValue.restoreToOriginalValue();

        } else if (restore == NULL_OR_0_VALUE) {
            originalFieldValue.restoreToNullOr0();
        }
    }
}
