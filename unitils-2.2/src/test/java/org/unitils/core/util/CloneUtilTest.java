/*
 * Copyright 2008,  Unitils.org
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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class CloneUtilTest {

    private SimpleValues simpleValues1;

    private SimpleValues simpleValues2;

    private Collections collections;

    private References references;

    @Before
    public void setup() {
        simpleValues1 = new SimpleValues("testString", 1L, 2, new char[]{'a', 'b', 'c'});
        simpleValues2 = new SimpleValues(null, 0L, 0, null);

        List<SimpleValues> listValue = asList(simpleValues1, simpleValues2);
        Map<SimpleValues, SimpleValues> mapValue = new HashMap<SimpleValues, SimpleValues>();
        mapValue.put(simpleValues1, simpleValues2);
        collections = new Collections(listValue, mapValue);

        references = new References(new References(null));
        references.references = references;
    }


    @Test
    public void testCreateDeepClone() {
        SimpleValues result = CloneUtil.createDeepClone(simpleValues1);
        assertReflectionEquals(simpleValues1, result);
        assertNotSame(simpleValues1, result);
        assertSame(simpleValues1.stringValue, result.stringValue);
        assertSame(simpleValues1.longValue, result.longValue);
        assertSame(simpleValues1.intValue, result.intValue);
        assertNotSame(simpleValues1.arrayValue, result.arrayValue);
    }


    @Test
    public void testCreateDeepClone_clonedTwice() {
        SimpleValues result = CloneUtil.createDeepClone(CloneUtil.createDeepClone(simpleValues1));
        assertReflectionEquals(simpleValues1, result);
        assertNotSame(simpleValues1, result);
    }


    @Test
    public void testCreateDeepClone_topLevelCollection() {
        List<?> aList = Arrays.asList(1, new int[]{1, 2});
        List<?> result = CloneUtil.createDeepClone(aList);

        assertReflectionEquals(aList, result);
        assertNotSame(aList, result);
        assertSame(aList.get(0), result.get(0));
        assertNotSame(aList.get(1), result.get(1));
    }


    @Test
    public void testCreateDeepClone_collections() {
        Collections result = CloneUtil.createDeepClone(collections);
        assertReflectionEquals(collections, result);
        assertNotSame(collections, result);
        assertNotSame(collections.listValue, result.listValue);
        assertNotSame(collections.mapValue, result.mapValue);
    }
    
    
    @Test
    public void testCreateDeepClone_InnerClass() throws IllegalArgumentException, IllegalAccessException {
        ClassWithInnerClass classWithInnerClass = new ClassWithInnerClass();
        ClassWithInnerClass result = CloneUtil.createDeepClone(classWithInnerClass);
        assertReflectionEquals(classWithInnerClass, result);
        result.inner.setString();
    }
    
    @Test
    public void testCreateDeepClone_AnonymousClass() throws IllegalArgumentException, IllegalAccessException {
        Comparable<String> anonymousClass = new Comparable<String>() {
            public int compareTo(String o) {
                return 0;
            }
        };
        Comparable<String> result = CloneUtil.createDeepClone(anonymousClass);
        assertReflectionEquals(anonymousClass, result);
    }


    @Test
    public void testCreateDeepClone_loop() {
        References result = CloneUtil.createDeepClone(references);
        assertReflectionEquals(references, result);
        assertNotSame(references, result);
        assertNotSame(references.references, result.references);
        assertSame(references.references, references);
    }


    protected static class SimpleValues {

        private String stringValue;
        private Long longValue;
        private int intValue;
        private char[] arrayValue;

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

        public Collections(List<SimpleValues> listValue, Map<SimpleValues, SimpleValues> mapValue) {
            this.listValue = listValue;
            this.mapValue = mapValue;
        }
    }


    protected static class References {

        private References references;

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


}