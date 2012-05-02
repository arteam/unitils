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
package org.unitils.inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitilsnew.UnitilsJUnit4;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.inject.util.Restore.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoStaticByTypeRestoreIntegrationTest extends UnitilsJUnit4 {

    @InjectIntoStaticByType(target = InjectionTarget.class, restore = DEFAULT)
    private Type1 type1 = new Type1("new value 1");

    @InjectIntoStaticByType(target = InjectionTarget.class, restore = NO_RESTORE)
    private Type2 type2 = new Type2("new value 2");

    @InjectIntoStaticByType(target = InjectionTarget.class, restore = NULL_OR_0_VALUE)
    private Type3 type3 = new Type3("new value 3");

    @InjectIntoStaticByType(target = InjectionTarget.class, restore = NULL_OR_0_VALUE)
    private int value4 = 999;

    @InjectIntoStaticByType(target = InjectionTarget.class, restore = OLD_VALUE)
    private Type5 type5 = new Type5("new value 5");

    @InjectIntoStaticByType(target = InjectionTarget.class, restore = OLD_VALUE, failWhenNoMatch = false)
    private Properties noFailureDuringRestoreWhenNoMatch = new Properties();


    @Before
    public void initialize() {
        InjectionTarget.type1 = new Type1("original value 1");
        InjectionTarget.type2 = new Type2("original value 2");
        InjectionTarget.type3 = new Type3("original value 3");
        InjectionTarget.value4 = 444;
        InjectionTarget.type5 = new Type5("original value 5");
    }


    @Test
    public void injectIntoStaticByType() {
        // restore is verified in the after method
    }

    @After
    public void assertValuesAreRestored() {
        // default is old value
        assertEquals("original value 1", InjectionTarget.type1.value);
        // no restore
        assertEquals("new value 2", InjectionTarget.type2.value);
        // restore to null
        assertNull(InjectionTarget.type3);
        // restore to 0
        assertEquals(0, InjectionTarget.value4);
        // restore original value
        assertEquals("original value 5", InjectionTarget.type5.value);
    }


    private static class InjectionTarget {

        private static Type1 type1;
        private static Type2 type2;
        private static Type3 type3;
        private static int value4;
        private static Type5 type5;
    }


    private static class Type1 {
        private String value;

        private Type1(String value) {
            this.value = value;
        }
    }

    private static class Type2 {
        private String value;

        private Type2(String value) {
            this.value = value;
        }
    }

    private static class Type3 {
        private String value;

        private Type3(String value) {
            this.value = value;
        }
    }

    private static class Type5 {
        private String value;

        private Type5(String value) {
            this.value = value;
        }
    }
}
