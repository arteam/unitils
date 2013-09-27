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

package org.unitils.mock.listener;

import org.unitils.core.FieldAnnotationListener;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.TestPhase;
import org.unitils.core.reflect.Annotations;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.DummyService;

import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class DummyFieldAnnotationListener extends FieldAnnotationListener<Dummy> {

    protected DummyService dummyService;


    public DummyFieldAnnotationListener(DummyService dummyService) {
        this.dummyService = dummyService;
    }


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<Dummy> annotations) {
        String dummyName = testField.getName();
        Class<?> dummyType = testField.getType();

        Object dummy = dummyService.createDummy(dummyName, dummyType);
        testField.setValue(dummy);
    }
}
