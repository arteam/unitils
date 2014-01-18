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
package org.unitils.core.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.ObjectFactory;

import java.io.File;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.core.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 */
public class ObjectFormatterFormatTest {

    private ObjectFormatter objectFormatter;

    private ProxyService proxyService;


    @Before
    public void initialize() {
        objectFormatter = new ObjectFormatter(2, 4);

        proxyService = new ProxyService(new CloneService(new ObjectFactory()));
    }


    @Test
    public void nullValue() {
        String result = objectFormatter.format(null);
        assertEquals("null", result);
    }

    @Test
    public void string() {
        String result = objectFormatter.format("value");
        assertEquals("\"value\"", result);
    }

    @Test
    public void number() {
        String result = objectFormatter.format(new Integer("5"));
        assertEquals("5", result);
    }

    @Test
    public void date() {
        Date date = new Date();
        String result = objectFormatter.format(date);
        assertEquals(date.toString(), result);
    }

    @Test
    public void objectToFormat() {
        String result = objectFormatter.format(new MyObjectToFormat());
        assertEquals("formatted", result);
    }

    @Test
    public void character() {
        String result = objectFormatter.format('c');
        assertEquals("'c'", result);
    }

    @Test
    @SuppressWarnings("UnnecessaryBoxing")
    public void characterWrapper() {
        String result = objectFormatter.format(new Character('c'));
        assertEquals("'c'", result);
    }

    @Test
    public void primitive() {
        String result = objectFormatter.format(5);
        assertEquals("5", result);
    }

    @Test
    public void enumValue() {
        String result = objectFormatter.format(RetentionPolicy.RUNTIME);
        assertEquals("RUNTIME", result);
    }

    @Test
    public void jdkProxy() {
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{MyInterface.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
        String result = objectFormatter.format(proxy);
        assertEquals("Proxy<?>", result);
    }

    @Test
    public void cglibProxy() {
        Object proxy = proxyService.createProxy("1", "name", false, new ProxyInvocationHandler() {
            public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
                return null;
            }
        }, Object.class, MyInterface.class);
        String result = objectFormatter.format(proxy);
        assertEquals("Proxy<name>", result);
    }

    @Test
    public void javaLang() {
        Class<?> clazz = getClass();
        String result = objectFormatter.format(clazz);
        assertEquals(clazz.toString(), result);
    }

    @Test
    public void array() {
        String result = objectFormatter.format(new int[]{3, 2, 1});
        assertEquals("[3, 2, 1]", result);
    }

    @Test
    public void arrayTruncatedWhenTooManyElements() {
        String result = objectFormatter.format(new int[]{1, 2, 3, 4, 5});
        assertEquals("[1, 2, 3, 4, ...]", result);
    }

    @Test
    public void list() {
        String result = objectFormatter.format(asList(3, 2, 1));
        assertEquals("[3, 2, 1]", result);
    }

    @Test
    public void listTruncatedWhenTooManyElements() {
        String result = objectFormatter.format(asList(1, 2, 3, 4, 5));
        assertEquals("[1, 2, 3, 4, ...]", result);
    }

    @Test
    public void set() {
        String result = objectFormatter.format(asSet(3, 2, 1));
        assertEquals("[3, 2, 1]", result);
    }

    @Test
    public void setTruncatedWhenTooManyElements() {
        String result = objectFormatter.format(asSet(1, 2, 3, 4, 5));
        assertEquals("[1, 2, 3, 4, ...]", result);
    }

    @Test
    public void map() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("1", "11");
        map.put("2", "22");
        map.put("3", "33");
        String result = objectFormatter.format(map);
        assertEquals("{\"1\"=\"11\", \"2\"=\"22\", \"3\"=\"33\"}", result);
    }

    @Test
    public void mapTruncatedWhenTooManyElements() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("1", "11");
        map.put("2", "22");
        map.put("3", "33");
        map.put("4", "44");
        map.put("5", "55");
        String result = objectFormatter.format(map);
        assertEquals("{\"1\"=\"11\", \"2\"=\"22\", \"3\"=\"33\", \"4\"=\"44\", ...}", result);
    }

    @Test
    public void file() {
        String result = objectFormatter.format(new File("path"));
        assertEquals("File<path>", result);
    }

    @Test
    public void object() {
        MyClass nestedValue = new MyClass();
        nestedValue.field1 = 55;
        nestedValue.field2 = "nested value";
        MyClass object = new MyClass();
        object.field1 = 44;
        object.field2 = "value";
        object.field3 = nestedValue;

        String result = objectFormatter.format(object);
        assertEquals("ObjectFormatterFormatTest.MyClass<field1=44, field2=\"value\", field3=ObjectFormatterFormatTest.MyClass<field1=55, field2=\"nested value\", field3=null>>", result);
    }

    @Test
    public void objectTruncatedWhenNestingTooDeep() {
        MyClass nestedValue1 = new MyClass();
        nestedValue1.field1 = 55;
        nestedValue1.field2 = "nested value 1";
        MyClass nestedValue2 = new MyClass();
        nestedValue2.field1 = 66;
        nestedValue2.field2 = "nested value 2";
        nestedValue2.field3 = nestedValue1;
        MyClass object = new MyClass();
        object.field1 = 44;
        object.field2 = "value";
        object.field3 = nestedValue2;

        String result = objectFormatter.format(object);
        assertEquals("ObjectFormatterFormatTest.MyClass<field1=44, field2=\"value\", field3=ObjectFormatterFormatTest.MyClass<field1=66, field2=\"nested value 2\", field3=ObjectFormatterFormatTest.MyClass<...>>>", result);
    }

    @Test
    public void hierarchy() {
        ClassA object = new ClassD();

        String result = objectFormatter.format(object);
        assertEquals("ObjectFormatterFormatTest.ClassD<fieldC=\"C\", fieldXX=\"33\", fieldB=\"B\", fieldXX=\"22\", fieldA=\"A\", fieldXX=\"11\">", result);
    }

    @Test
    public void exceptionWhenIllegalAccess() {
        ObjectFormatter objectFormatter = new ObjectFormatter() {
            @Override
            protected Object getFieldValue(Object object, Field field) throws IllegalAccessException {
                throw new IllegalAccessException("expected");
            }
        };
        try {
            objectFormatter.format(new MyClass());
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to format field private int org.unitils.core.util.ObjectFormatterFormatTest$MyClass.field1\n" +
                    "Reason: IllegalAccessException: expected", e.getMessage());
        }
    }

    @Test
    public void transientFieldsAreIgnored() {
        TransientFieldClass object = new TransientFieldClass();
        object.transientField = 777;

        String result = objectFormatter.format(object);
        assertEquals("ObjectFormatterFormatTest.TransientFieldClass<>", result);
    }

    @Test
    public void staticFieldsAreIgnored() {
        StaticFieldClass object = new StaticFieldClass();
        StaticFieldClass.staticField = 777;

        String result = objectFormatter.format(object);
        assertEquals("ObjectFormatterFormatTest.StaticFieldClass<>", result);
    }

    @Test
    public void syntheticFieldsAreIgnored() {
        NonStaticSyntheticFieldClass object = new NonStaticSyntheticFieldClass();

        String result = objectFormatter.format(object);
        assertEquals("ObjectFormatterFormatTest.NonStaticSyntheticFieldClass<>", result);
    }


    private static class MyObjectToFormat implements ObjectToFormat {

        public String $formatObject() {
            return "formatted";
        }
    }

    public static interface MyInterface {
    }

    public static class MyClass {

        private int field1;
        private String field2;
        private MyClass field3;
    }


    public static class ClassA {
        private String fieldA = "A";
        protected String fieldXX = "11";
    }

    public static class ClassB extends ClassA {
        private String fieldB = "B";
        protected String fieldXX = "22";
    }

    public static class ClassC extends ClassB {
        private String fieldC = "C";
        protected String fieldXX = "33";
    }

    public static class ClassD extends ClassC {
    }


    public class NonStaticSyntheticFieldClass {
    }

    public static class TransientFieldClass {

        private transient int transientField;
    }

    public static class StaticFieldClass {

        private static int staticField;
    }
}
