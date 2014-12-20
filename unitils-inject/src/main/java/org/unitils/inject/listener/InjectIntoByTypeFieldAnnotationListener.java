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
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.core.InjectionByTypeService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.inject.core.TargetService;

import java.lang.reflect.Type;
import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.core.TestPhase.INJECTION;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoByTypeFieldAnnotationListener extends FieldAnnotationListener<InjectIntoByType> {

    protected TargetService targetService;
    protected InjectionByTypeService injectionByTypeService;


    public InjectIntoByTypeFieldAnnotationListener(TargetService targetService, InjectionByTypeService injectionByTypeService) {
        this.injectionByTypeService = injectionByTypeService;
        this.targetService = targetService;
    }


    @Override
    public TestPhase getTestPhase() {
        return INJECTION;
    }


    @Override
    public void beforeTestMethod(TestInstance testInstance, TestField testField, Annotations<InjectIntoByType> annotations) {
        InjectIntoByType annotation = annotations.getAnnotationWithDefaults();

        Object value = testField.getValue();
        Type genericType = testField.getGenericType();
        String[] targetNamesArray = annotation.target();
        List<String> targetNames = targetNamesArray == null ? null : asList(targetNamesArray);
        boolean failWhenNoMatch = annotation.failWhenNoMatch();

        ObjectToInject objectToInject = new ObjectToInject(value, genericType);
        List<?> targets = targetService.getTargetsForInjection(targetNames, testInstance);
        injectionByTypeService.injectIntoAllByType(targets, objectToInject, failWhenNoMatch);
    }
}
