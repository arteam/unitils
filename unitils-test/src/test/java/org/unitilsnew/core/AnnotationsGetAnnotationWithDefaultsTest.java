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
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.annotation.AnnotationDefault;
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collections;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class AnnotationsGetAnnotationWithDefaultsTest extends UnitilsJUnit4 {

    /* Tested object */
    private Annotations<MyAnnotation> annotations;

    private MyAnnotation annotation;
    private MyAnnotation classAnnotation;
    @Dummy
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Method method = TestClass.class.getMethod("method");
        classAnnotation = TestClass.class.getAnnotation(MyAnnotation.class);
        annotation = method.getAnnotation(MyAnnotation.class);
    }


    @Test
    public void getAnnotationWithDefaults() {
        annotations = new Annotations<MyAnnotation>(annotation, asList(classAnnotation), configuration);

        MyAnnotation result = annotations.getAnnotationWithDefaults();
        assertEquals("classValue", result.value());
    }

    @Test
    public void onlyClassAnnotations() {
        annotations = new Annotations<MyAnnotation>(null, asList(classAnnotation), configuration);

        MyAnnotation result = annotations.getAnnotationWithDefaults();
        assertEquals("classValue", result.value());
    }

    @Test
    public void noAnnotations() {
        annotations = new Annotations<MyAnnotation>(null, Collections.<MyAnnotation>emptyList(), configuration);

        MyAnnotation result = annotations.getAnnotationWithDefaults();
        assertNull(result);
    }


    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public static @interface MyAnnotation {

        @AnnotationDefault String value() default "";

    }

    @MyAnnotation("classValue")
    private static class TestClass {

        @MyAnnotation
        public void method() {
        }
    }

}
