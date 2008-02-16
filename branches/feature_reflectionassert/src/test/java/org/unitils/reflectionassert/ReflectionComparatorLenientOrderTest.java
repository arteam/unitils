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
import static java.util.Arrays.asList;


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
        Primitives primitives1 = new Primitives("test1", "test2", asList(new Primitives("innerTest1", "innerTest2", null)));
        Primitives primitives2 = new Primitives("test1dd", "test2", asList(new Primitives("innerTekst1", "innerTest2", null)));

        List list1 = asList(asList("222", asList("+++", "bb")), asList("111", asList("ac", "***")));
        List list2 = asList(asList("111", asList("ac", "bb")), asList("222", asList("aa", "bb")));
        //Difference result = reflectionComparator.getDifference(list1, list2);
        //Difference result = reflectionComparator.getDifference(Arrays.asList("test", "222"), Arrays.asList("222", "test 33"));
        Difference result = reflectionComparator.getAllDifferences(primitives1, primitives2);
        //Difference result = reflectionComparator.getDifference(1, 2);
        System.out.println(DifferenceReport.createReport(null, result));
        //assertNull(result);
        //Assert.fail(DifferenceReport.createReport(null, result));
    }


    private static class Primitives {

        /* A fist int value */
        private String intValue1;

        /* A second int value */
        private String intValue2;

        /* An inner object */
        private Object inner;


        /**
         * Creates and initializes the element.
         *
         * @param intValue1 the first int value
         * @param intValue2 the second int value
         * @param inner     the inner collection
         */
        public Primitives(String intValue1, String intValue2, Object inner) {
            this.intValue1 = intValue1;
            this.intValue2 = intValue2;
            this.inner = inner;
        }

        /**
         * Always returns false
         *
         * @param o the object to compare to
         */
        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

}