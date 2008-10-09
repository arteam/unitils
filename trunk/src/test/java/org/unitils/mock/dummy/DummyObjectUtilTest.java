/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.dummy;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class DummyObjectUtilTest {

    @Test
    public void createDummy_ConcreteClass() {
        ArrayList<?> list = DummyObjectUtil.createDummy(ArrayList.class);
        assertNotNull(list);
    }
    
    @Test
    public void createDummy_AbstractClass() {
        AbstractList<?> list = DummyObjectUtil.createDummy(AbstractList.class);
        assertNotNull(list);
    }
    
    @Test
    public void createDummy_Interface() {
        List<?> list = DummyObjectUtil.createDummy(List.class);
        assertNotNull(list);
    }
    
    @Test
    public void dummyEqualsHashcode() {
        ArrayList<?> list1 = DummyObjectUtil.createDummy(ArrayList.class);
        
        assertTrue(list1.equals(list1));
        assertTrue(list1.hashCode() == list1.hashCode());
        
        ArrayList<?> list2 = DummyObjectUtil.createDummy(ArrayList.class);
        assertFalse(list1.equals(list2));
        assertFalse(list1.hashCode() == list2.hashCode());
    }
    
    @Test
    public void noDefaultConstructor() {
        TestClass dummy = DummyObjectUtil.createDummy(TestClass.class);
        assertNotNull(dummy);
    }
    
    @Test
    public void defaultBehavior() {
        TestClass dummy = DummyObjectUtil.createDummy(TestClass.class);
        assertTrue(dummy.getList().isEmpty());
        assertNull(dummy.getString());
        assertEquals(0, dummy.getInt());
    }
    
    private class TestClass {
        
        public TestClass(String someValue) {}

        public List getList() {return null;}
        
        public String getString() {return "someString";}
        
        public int getInt() {return 20;}
    }
}
