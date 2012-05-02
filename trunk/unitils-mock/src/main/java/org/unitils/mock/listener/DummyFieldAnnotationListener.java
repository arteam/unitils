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

package org.unitils.mock.listener;

import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.dummy.DummyObjectFactory;
import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.reflect.Annotations;

import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class DummyFieldAnnotationListener extends FieldAnnotationListener<Dummy> {

    protected DummyObjectFactory dummyObjectFactory;


    public DummyFieldAnnotationListener(DummyObjectFactory dummyObjectFactory) {
        this.dummyObjectFactory = dummyObjectFactory;
    }


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<Dummy> annotations) {
        Class<?> dummyType = testField.getType();

        Object dummy = dummyObjectFactory.createDummy(dummyType);
        testField.setValue(dummy);
    }
}
