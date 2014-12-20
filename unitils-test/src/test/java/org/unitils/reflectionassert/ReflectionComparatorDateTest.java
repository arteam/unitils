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
package org.unitils.reflectionassert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.difference.Difference;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DECEMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;


/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionComparator}. Contains tests with date types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorDateTest {

    /* Test object */
    private Date dateA;
    /* Same as A but different instance */
    private Date dateB;
    /* Same as A but instance of java.sql.Date */
    private java.sql.Date sqlDate;
    /* Date with a different value */
    private Date differentDate;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void initialize() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, DECEMBER, 5);

        dateA = calendar.getTime();
        dateB = new Date(dateA.getTime());
        sqlDate = new java.sql.Date(dateA.getTime());
        differentDate = new Date();

        reflectionComparator = createRefectionComparator();
    }


    /**
     * Test for two equal dates.
     */
    @Test
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(dateA, dateB);
        assertNull(result);
    }

    /**
     * Test for two equal dates but of different type.
     */
    @Test
    public void testGetDifference_sqlDate() {
        Difference result = reflectionComparator.getDifference(dateA, sqlDate);
        assertNull(result);
    }

    /**
     * Test for two different dates.
     */
    @Test
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(dateA, differentDate);

        assertEquals(dateA, result.getLeftValue());
        assertEquals(differentDate, result.getRightValue());
    }
}