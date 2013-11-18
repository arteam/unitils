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
package org.unitils.reflectionassert.comparator.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * @author Tim Ducheyne
 */
public class SimpleCasesComparatorCanCompareTest {

    private SimpleCasesComparator simpleCasesComparator;


    @Before
    public void initialize() {
        simpleCasesComparator = new SimpleCasesComparator();
    }


    @Test
    public void canNotCompareLeftExceptionInstance() {
        Exception e = new NullPointerException("a");

        boolean result = simpleCasesComparator.canCompare(e, "value");
        assertFalse(result);
    }

    @Test
    public void canNotCompareRightExceptionInstance() {
        Exception e = new NullPointerException("a");

        boolean result = simpleCasesComparator.canCompare("value", e);
        assertFalse(result);
    }
}
