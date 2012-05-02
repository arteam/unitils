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
package org.unitils.inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.inject.annotation.InjectIntoStatic;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.*;
import static org.unitils.inject.util.Restore.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoStaticRestoreIntegrationTest extends UnitilsJUnit4 {

    @InjectIntoStatic(target = InjectionTarget.class, property = "value1", restore = DEFAULT)
    private String value1 = "new value 1";

    @InjectIntoStatic(target = InjectionTarget.class, property = "value2", restore = NO_RESTORE)
    private String value2 = "new value 2";

    @InjectIntoStatic(target = InjectionTarget.class, property = "value3", restore = NULL_OR_0_VALUE)
    private String value3 = "new value 3";

    @InjectIntoStatic(target = InjectionTarget.class, property = "value4", restore = NULL_OR_0_VALUE)
    private int value4 = 999;

    @InjectIntoStatic(target = InjectionTarget.class, property = "value5", restore = OLD_VALUE)
    private String value5 = "new value 5";

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner1.value", restore = OLD_VALUE)
    private String value6 = "new value 6";

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner2.inner.value", restore = OLD_VALUE, autoCreateInnerFields = true)
    private String value7 = "new value 7";

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner3.inner.value", restore = OLD_VALUE, autoCreateInnerFields = true)
    private String value8 = "new value 8";

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner4.inner.value", restore = OLD_VALUE, autoCreateInnerFields = true)
    private String value9 = "new value 9";


    private Inner inner1;
    private Inner inner2;
    private Inner inner2inner;
    private Inner inner4;


    @Before
    public void initialize() {
        InjectionTarget.value1 = "original value 1";
        InjectionTarget.value2 = "original value 2";
        InjectionTarget.value3 = "original value 3";
        InjectionTarget.value4 = 444;
        InjectionTarget.value5 = "original value 5";

        inner1 = new Inner();
        inner2 = new Inner();
        inner2inner = new Inner();
        inner4 = new Inner();
        InjectionTarget.inner1 = inner1;
        InjectionTarget.inner1.value = "original value 6";
        InjectionTarget.inner2 = inner2;
        InjectionTarget.inner2.inner = inner2inner;
        InjectionTarget.inner2.inner.value = "original value 7";
        InjectionTarget.inner4 = inner4;
    }


    @Test
    public void injectIntoStatic() {
        // restore is verified in the after method
    }

    @After
    public void assertValuesAreRestored() {
        // default is old value
        assertEquals("original value 1", InjectionTarget.value1);
        // no restore
        assertEquals("new value 2", InjectionTarget.value2);
        // restore to null
        assertNull(InjectionTarget.value3);
        // restore to 0
        assertEquals(0, InjectionTarget.value4);
        // restore original value
        assertEquals("original value 5", InjectionTarget.value5);
        // composite field with existing inner objects
        assertSame(inner1, InjectionTarget.inner1);
        assertEquals("original value 6", InjectionTarget.inner1.value);
        // 2 level composite field with existing inner objects
        assertSame(inner2, InjectionTarget.inner2);
        assertSame(inner2inner, InjectionTarget.inner2.inner);
        assertEquals("original value 7", InjectionTarget.inner2.inner.value);
        // auto created fields are reset to null
        assertNull(InjectionTarget.inner3);
        // 1st inner object existed and stays untouched, 2nd level auto created field is reset to null
        assertSame(inner4, InjectionTarget.inner4);
        assertNull(InjectionTarget.inner4.inner);
    }


    private static class InjectionTarget {

        private static String value1;
        private static String value2;
        private static String value3;
        private static int value4;
        private static String value5;

        private static Inner inner1;
        private static Inner inner2;
        private static Inner inner3;
        private static Inner inner4;
    }

    private static class Inner {

        private String value;

        private Inner inner;
    }
}
