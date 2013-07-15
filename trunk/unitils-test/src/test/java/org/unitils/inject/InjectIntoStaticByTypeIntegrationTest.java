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
package org.unitils.inject;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoStaticByType;

import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoStaticByTypeIntegrationTest extends UnitilsJUnit4 {

    @InjectIntoStaticByType(target = InjectionTarget.class)
    public StringBuilder value1;
    @InjectIntoStaticByType(target = SubClass.class)
    public Properties value2;
    @InjectIntoStaticByType(target = InjectionTarget.class, failWhenNoMatch = false)
    public Date noMatchFoundIgnoredValue;


    @Before
    public void initialize() {
        value1 = new StringBuilder("value1");
        value2 = new Properties();

        InjectionTarget.value1 = null;
        InjectionTarget.value2 = null;
    }


    @Test
    public void injectIntoStaticByType() {
        // field in target class
        assertSame(value1, InjectionTarget.value1);
        // field in superclass
        assertSame(value2, SubClass.getValue2());
    }


    private static class InjectionTarget {

        private static StringBuilder value1;
        private static Properties value2;

        public static Properties getValue2() {
            return value2;
        }
    }

    private static class SubClass extends InjectionTarget {
    }
}