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
import org.unitils.inject.annotation.InjectIntoStatic;
import org.unitils.inject.core.InjectionService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.inject.util.Restore;

import java.util.HashMap;
import java.util.Map;

import static org.unitils.core.TestPhase.INJECTION;
import static org.unitils.inject.util.Restore.NULL_OR_0_VALUE;
import static org.unitils.inject.util.Restore.OLD_VALUE;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoStaticFieldAnnotationListener extends FieldAnnotationListener<InjectIntoStatic> {

    protected InjectionService injectionService;

    protected Map<TestField, OriginalFieldValue> originalFieldValues = new HashMap<TestField, OriginalFieldValue>(3);


    public InjectIntoStaticFieldAnnotationListener(InjectionService injectionService) {
        this.injectionService = injectionService;
    }


    @Override
    public TestPhase getTestPhase() {
        return INJECTION;
    }


    @Override
    public void beforeTestMethod(TestInstance testInstance, TestField testField, Annotations<InjectIntoStatic> annotations) {
        InjectIntoStatic annotation = annotations.getAnnotationWithDefaults();

        Object value = testField.getValue();
        Class<?> targetClass = annotation.target();
        String property = annotation.property();
        boolean autoCreateInnerFields = annotation.autoCreateInnerFields();

        ObjectToInject objectToInject = new ObjectToInject(value);
        OriginalFieldValue originalFieldValue = injectionService.injectIntoStatic(targetClass, property, objectToInject, autoCreateInnerFields);
        originalFieldValues.put(testField, originalFieldValue);
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, TestField testField, Annotations<InjectIntoStatic> annotations, Throwable testThrowable) {
        OriginalFieldValue originalFieldValue = originalFieldValues.get(testField);
        if (originalFieldValue == null) {
            // ignore, nothing to restore
            return;
        }
        InjectIntoStatic annotation = annotations.getAnnotationWithDefaults();
        Restore restore = annotation.restore();
        if (restore == OLD_VALUE) {
            originalFieldValue.restoreToOriginalValue();
        } else if (restore == NULL_OR_0_VALUE) {
            originalFieldValue.restoreToNullOr0();
        }
    }
}
