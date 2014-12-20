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

package org.unitils.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.reflect.ClassWrapper;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetMethodAnnotationsTest {

    /* Tested object */
    private TestInstance testInstance;

    private ClassWrapper classWrapper;
    private Method annotationsMethod;
    private Method noAnnotationsMethod;


    @Before
    public void initialize() throws Exception {
        annotationsMethod = MyClass.class.getMethod("annotations");
        noAnnotationsMethod = MyClass.class.getMethod("noAnnotations");
        classWrapper = new ClassWrapper(MyClass.class);
    }


    @Test
    public void annotations() {
        testInstance = new TestInstance(classWrapper, null, annotationsMethod);

        List<Annotation> result = testInstance.getMethodAnnotations();
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Annotation1);
        assertTrue(result.get(1) instanceof Annotation2);
    }

    @Test
    public void emptyWhenNoAnnotations() {
        testInstance = new TestInstance(classWrapper, null, noAnnotationsMethod);

        List<Annotation> result = testInstance.getMethodAnnotations();
        assertTrue(result.isEmpty());
    }


    @Retention(RUNTIME)
    private @interface Annotation1 {
    }

    @Retention(RUNTIME)
    private @interface Annotation2 {
    }

    private static class MyClass {

        @Annotation1
        @Annotation2
        public void annotations() {
        }

        public void noAnnotations() {
        }
    }
}
