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

package org.unitilsnew.core;

import org.junit.Test;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.engine.UnitilsTestListener;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTest extends UnitilsJUnit4 {


    @Test
    public void getUnitilsTestListener() {
        UnitilsTestListener result = Unitils.getUnitilsTestListener();
        assertNotNull(result);
    }

    @Test
    public void getUnitilsTestListenerCached() {
        UnitilsTestListener result1 = Unitils.getUnitilsTestListener();
        UnitilsTestListener result2 = Unitils.getUnitilsTestListener();
        assertSame(result1, result2);
    }

    @Test
    public void getInstanceOfType() {
        MyClass result = Unitils.getInstanceOfType(MyClass.class);
        assertNotNull(result);
    }

    @Test
    public void contextIsCached() {
        MyClass result1 = Unitils.getInstanceOfType(MyClass.class);
        MyClass result2 = Unitils.getInstanceOfType(MyClass.class);
        assertSame(result1, result2);
    }

    @Test
    public void constructionForCoverage() {
        new Unitils();
    }


    private static class MyClass {
    }
}
