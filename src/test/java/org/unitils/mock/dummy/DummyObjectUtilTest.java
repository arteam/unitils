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

import static junit.framework.Assert.*;
import org.junit.Test;
import org.unitils.core.util.CloneUtil;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptContentHandle;
import org.unitils.mock.MockUnitils;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
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

    @Test
    public void instanceOfDummyObject() {
        TestClass dummy = DummyObjectUtil.createDummy(TestClass.class);
        assertTrue(dummy instanceof DummyObject);
        List<?> dummyList = DummyObjectUtil.createDummy(List.class);
        assertTrue(dummyList instanceof DummyObject);
    }

    @Test
    public void refEquals() {
        Script script = new Script("01_script1.sql", 0L, MockUnitils.createDummy(ScriptContentHandle.class));
        ExecutedScript executedScript1 = new ExecutedScript(script, null, false);
        ExecutedScript executedScript2 = new ExecutedScript(script, null, false);
        assertLenientEquals(executedScript1, executedScript2);
    }

    @Test
    public void toStringMethod() {
        TestClass dummy = DummyObjectUtil.createDummy(TestClass.class);
        assertEquals("DUMMY TestClass@" + Integer.toHexString(dummy.hashCode()), dummy.toString());
    }

    @Test
    public void deepCloneEqualToOriginal() {
        TestClass dummy = DummyObjectUtil.createDummy(TestClass.class);
        TestClass clone = CloneUtil.createDeepClone(dummy);
        assertEquals(dummy, clone);
    }


    private class TestClass {

        public TestClass(String someValue) {
        }

        public List<?> getList() {
            return null;
        }

        public String getString() {
            return "someString";
        }

        public int getInt() {
            return 20;
        }
    }
}
