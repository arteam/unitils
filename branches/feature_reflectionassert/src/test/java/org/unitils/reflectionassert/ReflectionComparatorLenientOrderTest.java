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
package org.unitils.reflectionassert;

import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.formatter.DifferenceReport;

import java.util.Arrays;
import java.util.List;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests for lenient order.
 *                                                  
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorLenientOrderTest extends TestCase {

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();
        reflectionComparator = createRefectionComparator(ReflectionComparatorMode.LENIENT_ORDER);
        //reflectionComparator = createRefectionComparator();
    }


    /**
     * Test for two equal collections.
     * todo implement
     */
    public void testGetDifference_equals() {
        List list1 = Arrays.asList(Arrays.asList("111", Arrays.asList("aa", "bb")), Arrays.asList("222", Arrays.asList("aa", "bb")));
        List list2 = Arrays.asList(Arrays.asList("222", Arrays.asList("ac", "bb")), Arrays.asList("111", Arrays.asList("aa", "***")));
        Difference result = reflectionComparator.getDifference(list1, list2);
        //Difference result = reflectionComparator.getDifference(Arrays.asList(1, 2), Arrays.asList(2, 1, 4, 2));
       // Difference result = reflectionComparator.getDifference(1, 2);
        System.out.println(DifferenceReport.createReport(null, result));
        //assertNull(result);
    }


}