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

package org.unitilsnew.core.listener.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitilsnew.core.annotation.AnnotationDefault;
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitilsnew.core.listener.impl.AnnotationDefaultInvocationHandlerInvokeTest.TestEnum.*;

/**
 * @author Tim Ducheyne
 */
public class AnnotationDefaultInvocationHandlerInvokeTest {

    /* Tested object */
    private AnnotationDefaultInvocationHandler<MyAnnotation> annotationDefaultInvocationHandler;

    private Configuration configuration;

    private MyAnnotation noDefaultsAnnotation;
    private MyAnnotation allDefaultsAnnotation;
    private MyAnnotation oneDefaultAnnotation;
    private MyAnnotation defaultsSpecifiedAnnotation;

    private Method stringMethod;
    private Method primitiveMethod;
    private Method enumValueMethod;
    private Method stringNoDefaultMethod;
    private Method primitiveNoDefaultMethod;
    private Method enumNoDefaultMethod;
    private Method propertyNotFoundMethod;
    private Method emptyPropertyNameMethod;

    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("stringDefaultPropertyName", "someDefault");
        properties.setProperty("primitiveDefaultPropertyName", "333");
        properties.setProperty("enumDefaultPropertyName", "VALUE2");
        configuration = new Configuration(properties);

        noDefaultsAnnotation = TestClass.class.getMethod("noDefaults").getAnnotation(MyAnnotation.class);
        allDefaultsAnnotation = TestClass.class.getMethod("allDefaults").getAnnotation(MyAnnotation.class);
        oneDefaultAnnotation = TestClass.class.getMethod("oneDefault").getAnnotation(MyAnnotation.class);
        defaultsSpecifiedAnnotation = TestClass.class.getMethod("defaultsSpecified").getAnnotation(MyAnnotation.class);

        stringMethod = MyAnnotation.class.getMethod("string");
        primitiveMethod = MyAnnotation.class.getMethod("primitive");
        enumValueMethod = MyAnnotation.class.getMethod("enumValue");
        stringNoDefaultMethod = MyAnnotation.class.getMethod("stringNoDefault");
        primitiveNoDefaultMethod = MyAnnotation.class.getMethod("primitiveNoDefault");
        enumNoDefaultMethod = MyAnnotation.class.getMethod("enumNoDefault");
        propertyNotFoundMethod = MyAnnotation.class.getMethod("propertyNotFound");
        emptyPropertyNameMethod = MyAnnotation.class.getMethod("emptyPropertyName");
    }

    @Test
    public void noDefaults() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(noDefaultsAnnotation), configuration);

        Object stringResult = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        Object primitiveResult = annotationDefaultInvocationHandler.invoke(null, primitiveMethod, new Object[]{});
        Object enumResult = annotationDefaultInvocationHandler.invoke(null, enumValueMethod, new Object[]{});
        assertEquals("value", stringResult);
        assertEquals(4, primitiveResult);
        Assert.assertEquals(VALUE1, enumResult);
    }

    @Test
    public void defaultsToSecondAnnotation() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation, noDefaultsAnnotation), configuration);

        Object stringResult = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        Object primitiveResult = annotationDefaultInvocationHandler.invoke(null, primitiveMethod, new Object[]{});
        Object enumResult = annotationDefaultInvocationHandler.invoke(null, enumValueMethod, new Object[]{});
        assertEquals("value", stringResult);
        assertEquals(4, primitiveResult);
        Assert.assertEquals(VALUE1, enumResult);
    }

    @Test
    public void defaultsToSecondAndThirdAnnotation() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation, oneDefaultAnnotation, noDefaultsAnnotation), configuration);

        Object stringResult = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        Object primitiveResult = annotationDefaultInvocationHandler.invoke(null, primitiveMethod, new Object[]{});
        Object enumResult = annotationDefaultInvocationHandler.invoke(null, enumValueMethod, new Object[]{});
        assertEquals("value", stringResult);
        assertEquals(4, primitiveResult);
        Assert.assertEquals(VALUE1, enumResult);
    }

    @Test
    public void defaultsSpecifiedInAnnotation() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(defaultsSpecifiedAnnotation, noDefaultsAnnotation), configuration);

        Object stringResult = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        Object primitiveResult = annotationDefaultInvocationHandler.invoke(null, primitiveMethod, new Object[]{});
        Object enumResult = annotationDefaultInvocationHandler.invoke(null, enumValueMethod, new Object[]{});
        assertEquals("value", stringResult);
        assertEquals(4, primitiveResult);
        Assert.assertEquals(VALUE1, enumResult);
    }

    @Test
    public void defaultsFromProperties() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation), configuration);

        Object stringResult = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        Object primitiveResult = annotationDefaultInvocationHandler.invoke(null, primitiveMethod, new Object[]{});
        Object enumResult = annotationDefaultInvocationHandler.invoke(null, enumValueMethod, new Object[]{});
        assertEquals("someDefault", stringResult);
        assertEquals(333, primitiveResult);
        Assert.assertEquals(VALUE2, enumResult);
    }

    @Test
    public void propertyNotFound() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation), configuration);

        Object result = annotationDefaultInvocationHandler.invoke(null, propertyNotFoundMethod, new Object[]{});
        assertNull(result);
    }

    @Test
    public void emptyPropertyName() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation), configuration);

        Object result = annotationDefaultInvocationHandler.invoke(null, emptyPropertyNameMethod, new Object[]{});
        assertEquals("a", result);
    }

    @Test
    public void fieldsWithoutDefaultAnnotation() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation), configuration);

        Object stringResult = annotationDefaultInvocationHandler.invoke(null, stringNoDefaultMethod, new Object[]{});
        Object primitiveResult = annotationDefaultInvocationHandler.invoke(null, primitiveNoDefaultMethod, new Object[]{});
        Object enumResult = annotationDefaultInvocationHandler.invoke(null, enumNoDefaultMethod, new Object[]{});
        assertEquals("a", stringResult);
        assertEquals(1, primitiveResult);
        Assert.assertEquals(DEFAULT_VALUE, enumResult);
    }

    @Test
    public void emptyAnnotationsList() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(new ArrayList<MyAnnotation>(), configuration);

        Object result = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        assertNull(result);
    }

    @Test
    public void nullAnnotationsList() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(null, configuration);

        Object result = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        assertNull(result);
    }

    @Test
    public void nullConfiguration() throws Throwable {
        annotationDefaultInvocationHandler = new AnnotationDefaultInvocationHandler<MyAnnotation>(asList(allDefaultsAnnotation), null);

        Object result = annotationDefaultInvocationHandler.invoke(null, stringMethod, new Object[]{});
        assertEquals("a", result);
    }


    @Target(METHOD)
    @Retention(RUNTIME)
    private static @interface MyAnnotation {

        @AnnotationDefault("stringDefaultPropertyName") String string() default "a";

        @AnnotationDefault("primitiveDefaultPropertyName") int primitive() default 1;

        @AnnotationDefault("enumDefaultPropertyName") TestEnum enumValue() default DEFAULT_VALUE;

        String stringNoDefault() default "a";

        int primitiveNoDefault() default 1;

        TestEnum enumNoDefault() default DEFAULT_VALUE;

        @AnnotationDefault("xxx") String propertyNotFound() default "a";

        @AnnotationDefault("") String emptyPropertyName() default "a";
    }

    private static class TestClass {

        @MyAnnotation(string = "value", primitive = 4, enumValue = VALUE1)
        public void noDefaults() {
        }

        @MyAnnotation
        public void allDefaults() {
        }

        @MyAnnotation(primitive = 4)
        public void oneDefault() {
        }

        @MyAnnotation(string = "a", primitive = 1, enumValue = DEFAULT_VALUE)
        public void defaultsSpecified() {
        }
    }

    public static enum TestEnum {

        DEFAULT_VALUE, VALUE1, VALUE2

    }
}
