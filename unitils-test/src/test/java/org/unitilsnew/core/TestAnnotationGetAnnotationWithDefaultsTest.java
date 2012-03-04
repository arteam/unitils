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
import org.unitilsnew.core.annotation.AnnotationDefault;
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Properties;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitilsnew.core.TestAnnotationGetAnnotationWithDefaultsTest.TestEnum.DEFAULT_VALUE;
import static org.unitilsnew.core.TestAnnotationGetAnnotationWithDefaultsTest.TestEnum.VALUE1;

/**
 * @author Tim Ducheyne
 */
public class TestAnnotationGetAnnotationWithDefaultsTest {

    /* Tested object */
    private Annotations<MyAnnotation> annotations;

    private Configuration configuration;

    private MyAnnotation noDefaultsAnnotation;
    private MyAnnotation allDefaultsAnnotation;

    @Before
    public void initialize() throws Exception {
        configuration = new Configuration(new Properties());

        noDefaultsAnnotation = TestClass.class.getMethod("noDefaults").getAnnotation(MyAnnotation.class);
        allDefaultsAnnotation = TestClass.class.getMethod("allDefaults").getAnnotation(MyAnnotation.class);
    }


    @Test
    public void annotationWithDefaults() {
        annotations = new Annotations<MyAnnotation>(allDefaultsAnnotation, asList(noDefaultsAnnotation), configuration);

        MyAnnotation result = annotations.getAnnotationWithDefaults();
        assertEquals("value", result.string());
        assertEquals(4, result.primitive());
        assertEquals(VALUE1, result.enumValue());
    }


    private static class TestClass {

        @MyAnnotation(string = "value", primitive = 4, enumValue = VALUE1)
        public void noDefaults() {
        }

        @MyAnnotation
        public void allDefaults() {
        }
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    public static @interface MyAnnotation {

        @AnnotationDefault("stringDefaultPropertyName") String string() default "a";

        @AnnotationDefault("primitiveDefaultPropertyName") int primitive() default 1;

        @AnnotationDefault("enumDefaultPropertyName") TestEnum enumValue() default DEFAULT_VALUE;

    }

    public static enum TestEnum {

        DEFAULT_VALUE, VALUE1, VALUE2

    }
}

