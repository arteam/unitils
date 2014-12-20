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

package org.unitils.core.reflect;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.config.Configuration;
import org.unitils.mock.annotation.Dummy;

import java.lang.annotation.Target;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class AnnotationsGetFirstAnnotationTest extends UnitilsJUnit4 {

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
    public void getFirstAnnotation() {
        annotations = new Annotations<Target>(annotation, asList(classAnnotation1, classAnnotation2), configuration);

        Target result = annotations.getFirstAnnotation();
        assertSame(annotation, result);
    }

    @Test
    public void onlyClassAnnotations() {
        annotations = new Annotations<Target>(null, asList(classAnnotation1, classAnnotation2), configuration);

        Target result = annotations.getFirstAnnotation();
        assertSame(classAnnotation1, result);
    }

    @Test
    public void noAnnotations() {
        annotations = new Annotations<Target>(null, Collections.<Target>emptyList(), configuration);

        Target result = annotations.getFirstAnnotation();
        assertNull(result);
    }
}
