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

package org.unitils.inject.listener;

import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.core.InjectionService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.inject.core.TargetService;
import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.reflect.Annotations;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitilsnew.core.TestPhase.INJECTION;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoFieldAnnotationListener extends FieldAnnotationListener<InjectInto> {

    protected TargetService targetService;
    protected InjectionService injectionService;


    public InjectIntoFieldAnnotationListener(TargetService targetService, InjectionService injectionService) {
        this.targetService = targetService;
        this.injectionService = injectionService;
    }


    @Override
    public TestPhase getTestPhase() {
        return INJECTION;
    }


    @Override
    public void beforeTestMethod(TestInstance testInstance, TestField testField, Annotations<InjectInto> annotations) {
        InjectInto annotation = annotations.getAnnotationWithDefaults();

        Object value = testField.getValue();
        String[] targetNamesArray = annotation.target();
        List<String> targetNames = targetNamesArray == null ? null : asList(targetNamesArray);
        String property = annotation.property();
        boolean autoCreateInnerFields = annotation.autoCreateInnerFields();

        ObjectToInject objectToInject = new ObjectToInject(value);
        List<?> targets = targetService.getTargetsForInjection(targetNames, testInstance);
        injectionService.injectIntoAll(targets, property, objectToInject, autoCreateInnerFields);
    }
}
