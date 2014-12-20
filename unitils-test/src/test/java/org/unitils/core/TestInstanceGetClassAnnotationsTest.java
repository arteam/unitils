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
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetClassAnnotationsTest {

    /* Tested object */
    private TestInstance testInstance;


    @Before
    public void initialize() throws Exception {
        ClassWrapper classWrapper = new ClassWrapper(MyClass.class);
        testInstance = new TestInstance(classWrapper, null, null);
    }


    @Test
    public void annotationsOfType() {
        List<MyAnnotation1> result = testInstance.getClassAnnotations(MyAnnotation1.class);

        assertEquals(2, result.size());
        assertEquals("annotation1", result.get(0).value());
        assertEquals("annotation1-super", result.get(1).value());
    }

    @Test
    public void noAnnotationsOfTypeFound() {
        List<Target> result = testInstance.getClassAnnotations(Target.class);

        assertTrue(result.isEmpty());
    }

    @Test
    public void allAnnotations() {
        List<Annotation> result = testInstance.getClassAnnotations();

        assertEquals(4, result.size());
        assertEquals("annotation1", ((MyAnnotation1) result.get(0)).value());
        assertEquals("annotation2", ((MyAnnotation2) result.get(1)).value());
        assertEquals("annotation1-super", ((MyAnnotation1) result.get(2)).value());
        assertEquals("annotation2-super", ((MyAnnotation2) result.get(3)).value());
    }

    @Test
    public void noAnnotationsFound() {
        testInstance = new TestInstance(new ClassWrapper(NoAnnotationsClass.class), null, null);

        List<Annotation> result = testInstance.getClassAnnotations();
        assertTrue(result.isEmpty());
    }


    @Retention(RUNTIME)
    private @interface MyAnnotation1 {
        String value();
    }

    @Retention(RUNTIME)
    private @interface MyAnnotation2 {
        String value();
    }

    @MyAnnotation1("annotation1-super")
    @MyAnnotation2("annotation2-super")
    private static class SuperClass {
    }

    @MyAnnotation1("annotation1")
    @MyAnnotation2("annotation2")
    private static class MyClass extends SuperClass {
    }

    private static class NoAnnotationsClass {
    }
}
