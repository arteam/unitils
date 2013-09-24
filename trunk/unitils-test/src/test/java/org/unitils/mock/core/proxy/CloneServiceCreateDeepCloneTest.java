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
package org.unitils.mock.core.proxy;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.mock.core.proxy.CloneServiceCreateDeepCloneTest.TestEnum.VALUE;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class CloneServiceCreateDeepCloneTest extends UnitilsJUnit4 {

    private CloneService cloneService;

    private Mock<ObjectFactory> objectFactoryMock;

    private SimpleValues simpleValues1;
    private SimpleValues simpleValues2;
    private Collections collections;
    private References references;


    @Before
    public void initialize() {
        cloneService = new CloneService(objectFactoryMock.getMock());

        simpleValues1 = new SimpleValues("testString", 1L, 2, new char[]{'a', 'b', 'c'});
        simpleValues2 = new SimpleValues(null, 0L, 0, null);

        List<SimpleValues> listValue = new ArrayList<SimpleValues>(asList(simpleValues1, simpleValues2));
        Map<SimpleValues, SimpleValues> mapValue = new HashMap<SimpleValues, SimpleValues>();
        mapValue.put(simpleValues1, simpleValues2);
        collections = new Collections(listValue, mapValue);

        references = new References(new References(null));
        references.references = references;
    }


    @Test
    public void simpleValues() {
        objectFactoryMock.returns(new SimpleValues()).createWithoutCallingConstructor(SimpleValues.class);

        SimpleValues result = cloneService.createDeepClone(simpleValues1);
        assertReflectionEquals(simpleValues1, result);
        assertNotSame(simpleValues1, result);
        assertSame(simpleValues1.stringValue, result.stringValue);
        assertSame(simpleValues1.longValue, result.longValue);
        assertSame(simpleValues1.intValue, result.intValue);
        assertNotSame(simpleValues1.arrayValue, result.arrayValue);
    }

    @Test
    public void clonedTwice() {
        objectFactoryMock.onceReturns(new SimpleValues()).createWithoutCallingConstructor(SimpleValues.class);
        objectFactoryMock.onceReturns(new SimpleValues()).createWithoutCallingConstructor(SimpleValues.class);

        SimpleValues result1 = cloneService.createDeepClone(simpleValues1);
        SimpleValues result2 = cloneService.createDeepClone(result1);
        assertNotSame(result1, result2);
        assertNotSame(simpleValues1, result1);
        assertNotSame(simpleValues1, result2);
        assertReflectionEquals(simpleValues1, result1);
        assertReflectionEquals(simpleValues1, result2);
    }

    @Test
    public void topLevelCollection() {
        List<?> aList = new ArrayList<Object>(asList(1, new int[]{1, 2}));
        List<?> result = cloneService.createDeepClone(aList);

        assertReflectionEquals(aList, result);
        assertNotSame(aList, result);
        assertSame(aList.get(0), result.get(0));
        assertSame(aList.get(1), result.get(1));
    }

    @Test
    public void collections() {
        objectFactoryMock.returns(new Collections()).createWithoutCallingConstructor(Collections.class);
        objectFactoryMock.returns(new ArrayList()).createWithoutCallingConstructor(ArrayList.class);

        Collections result = cloneService.createDeepClone(collections);
        assertReflectionEquals(collections, result);
        assertNotSame(collections, result);
        assertNotSame(collections.listValue, result.listValue);
        assertNotSame(collections.mapValue, result.mapValue);
    }

    @Test
    public void innerClass() {
        ClassWithInnerClass classWithInnerClass = new ClassWithInnerClass();
        ClassWithInnerClass result = cloneService.createDeepClone(classWithInnerClass);
        assertReflectionEquals(classWithInnerClass, result);
        result.inner.setString();
    }

    @Test
    public void anonymousClass() {
        Comparable<String> anonymousClass = new Comparable<String>() {
            public int compareTo(String o) {
                return 0;
            }
        };
        Comparable<String> result = cloneService.createDeepClone(anonymousClass);
        assertReflectionEquals(anonymousClass, result);
    }

    @Test
    public void cyclesAreDetected() {
        objectFactoryMock.returns(new References()).createWithoutCallingConstructor(References.class);

        References result = cloneService.createDeepClone(references);
        assertReflectionEquals(references, result);
        assertNotSame(references, result);
        assertNotSame(references.references, result.references);
        assertSame(references.references, references);
    }

    @Test
    public void nestedArray() {
        Object[] array = new Object[2];
        array[0] = "string";
        array[1] = array;
        Object[] clone = cloneService.createDeepClone(array);
        assertSame(clone, clone[1]);
    }

    @Test
    public void doNotClonePrimitive() {
        Integer result = cloneService.createDeepClone(1);
        assertSame(1, result);
    }

    @Test
    public void doNotCloneEnum() {
        TestEnum result = cloneService.createDeepClone(VALUE);
        assertSame(VALUE, result);
    }

    @Test
    public void doNotCloneAnnotation() throws Exception {
        Test annotation = getClass().getMethod("doNotCloneAnnotation").getAnnotation(Test.class);
        Test result = cloneService.createDeepClone(annotation);
        assertSame(annotation, result);
    }

    @Test
    public void doNotCloneJdkClasses() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        OutputStream result = cloneService.createDeepClone(outputStream);
        assertSame(outputStream, result);
    }

    @Test
    public void useCloneWhenCloneable() {
        CloneClass instance = new CloneClass("value");

        CloneClass result = cloneService.createDeepClone(instance);
        assertNotSame(instance, result);
        assertEquals("cloned value", result.value);
    }

    @Test
    public void stillCloneWhenExceptionDuringClone() {
        ExceptionDuringCloneClass originalInstance = new ExceptionDuringCloneClass();
        ExceptionDuringCloneClass clonedInstance = new ExceptionDuringCloneClass();
        objectFactoryMock.returns(clonedInstance).createWithoutCallingConstructor(ExceptionDuringCloneClass.class);

        ExceptionDuringCloneClass result = cloneService.createDeepClone(originalInstance);
        assertSame(clonedInstance, result);
        assertReflectionEquals(originalInstance, result);
    }

    @Test
    public void doNotCloneWhenUnableToCreate() {
        objectFactoryMock.raises(NullPointerException.class).createWithoutCallingConstructor(MyClass.class);
        MyClass instance = new MyClass("value");

        MyClass result = cloneService.createDeepClone(instance);
        assertSame(instance, result);
    }

    @Test
    public void exceptionWhenFailureDuringClone() {
        CloneService cloneService = new CloneService(null) {
            @Override
            protected boolean isImmutable(Object instanceToClone) {
                throw new NullPointerException("expected");
            }
        };
        try {
            cloneService.createDeepClone(5);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unexpected exception during cloning of 5\n" +
                    "Reason: NullPointerException: expected", e.getMessage());
        }
    }


    protected static class SimpleValues {

        private String stringValue;
        private Long longValue;
        private int intValue;
        private char[] arrayValue;

        public SimpleValues() {
        }

        public SimpleValues(String stringValue, Long longValue, int intValue, char[] arrayValue) {
            this.stringValue = stringValue;
            this.longValue = longValue;
            this.intValue = intValue;
            this.arrayValue = arrayValue;
        }
    }


    protected static class Collections {

        private List<SimpleValues> listValue;
        private Map<SimpleValues, SimpleValues> mapValue;

        public Collections() {
        }

        public Collections(List<SimpleValues> listValue, Map<SimpleValues, SimpleValues> mapValue) {
            this.listValue = listValue;
            this.mapValue = mapValue;
        }
    }


    protected static class References {

        private References references;

        public References() {
        }

        public References(References references) {
            this.references = references;
        }
    }

    protected static class ClassWithInnerClass {

        String str;
        Inner inner = new Inner();

        class Inner {
            void setString() {
                str = "value";
            }
        }
    }

    protected static enum TestEnum {
        VALUE
    }

    protected static class CloneClass implements Cloneable {

        protected String value;

        public CloneClass(String value) {
            this.value = value;
        }

        @Override
        @SuppressWarnings("CloneDoesntCallSuperClone")
        protected Object clone() throws CloneNotSupportedException {
            return new CloneClass("cloned value");
        }
    }

    protected static class ExceptionDuringCloneClass implements Cloneable {

        protected String value = "test";

        @Override
        @SuppressWarnings("CloneDoesntCallSuperClone")
        protected Object clone() throws CloneNotSupportedException {
            throw new NullPointerException("expected");
        }
    }

    protected static class MyClass {

        private String value = "test";

        public MyClass(String value) {
            this.value = value;
        }
    }
}