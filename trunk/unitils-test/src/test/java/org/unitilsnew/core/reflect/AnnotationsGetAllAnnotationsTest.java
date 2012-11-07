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

package org.unitilsnew.core.reflect;

import org.junit.Test;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class AnnotationsGetAllAnnotationsTest extends UnitilsJUnit4 {

    /* Tested object */
    private Annotations<Target> annotations;

    @Dummy
    private Target annotation;
    @Dummy
    private Target classAnnotation1;
    @Dummy
    private Target classAnnotation2;
    @Dummy
    private Configuration configuration;


    @Test
    public void getAllAnnotations() {
        annotations = new Annotations<Target>(annotation, asList(classAnnotation1, classAnnotation2), configuration);

        List<Target> result = annotations.getAllAnnotations();
        assertReflectionEquals(asList(annotation, classAnnotation1, classAnnotation2), result);
    }

    @Test
    public void onlyClassAnnotations() {
        annotations = new Annotations<Target>(null, asList(classAnnotation1, classAnnotation2), configuration);

        List<Target> result = annotations.getAllAnnotations();
        assertReflectionEquals(asList(classAnnotation1, classAnnotation2), result);
    }

    @Test
    public void noClassAnnotations() {
        annotations = new Annotations<Target>(annotation, Collections.<Target>emptyList(), configuration);

        List<Target> result = annotations.getAllAnnotations();
        assertReflectionEquals(asList(annotation), result);
    }

    @Test
    public void noAnnotations() {
        annotations = new Annotations<Target>(null, Collections.<Target>emptyList(), configuration);

        List<Target> result = annotations.getAllAnnotations();
        assertTrue(result.isEmpty());
    }
}
