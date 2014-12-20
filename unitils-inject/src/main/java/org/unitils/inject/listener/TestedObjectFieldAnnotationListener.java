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
import org.unitils.core.annotation.Property;
import org.unitils.core.reflect.Annotations;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.core.TestedObjectService;

import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class TestedObjectFieldAnnotationListener extends FieldAnnotationListener<TestedObject> {

    protected TestedObjectService testedObjectService;
    protected boolean createTestedObjectIfNull;


    public TestedObjectFieldAnnotationListener(TestedObjectService testedObjectService, @Property("inject.createTestedObjectIfNull") boolean createTestedObjectIfNull) {
        this.testedObjectService = testedObjectService;
        this.createTestedObjectIfNull = createTestedObjectIfNull;
    }


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<TestedObject> annotations) {
        if (!createTestedObjectIfNull) {
            // disabled, ignore
            return;
        }
        if (testField.getValue() != null) {
            // already has value, don't create new value
            return;
        }
        Object testedObject = testedObjectService.createTestedObject(testField.getType());
        testField.setValue(testedObject);
    }

}
