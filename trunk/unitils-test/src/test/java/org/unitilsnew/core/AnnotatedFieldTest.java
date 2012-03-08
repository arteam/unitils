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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class AnnotatedFieldTest extends UnitilsJUnit4 {

    /* Tested object */
    private AnnotatedField<Target> annotatedField;

    private Mock<TestInstance> testInstanceMock;
    private Field field;
    @Dummy
    private Target annotation;


    @Before
    public void initialize() throws Exception {
        field = getClass().getDeclaredField("field");

        annotatedField = new AnnotatedField<Target>(field, annotation);
    }


    @Test
    public void setFieldValue() {
        annotatedField.setFieldValue(testInstanceMock.getMock(), "value");
        testInstanceMock.assertInvoked().setFieldValue(field, "value");
    }

    @Test
    public void getAnnotation() {
        Target result = annotatedField.getAnnotation();
        assertSame(annotation, result);
    }

    @Test
    public void getField() {
        Field result = annotatedField.getField();
        assertSame(field, result);
    }
}
