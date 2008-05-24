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
import static org.unitils.reflectionassert.util.InnerDifferenceFinder.getInnerDifference;

/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with enums.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class ReflectionComparatorEnumsTest extends TestCase {

    /* Test object */
    private Enums enumsA;

    /* Same as A but different instance */
    private Enums enumsB;

    /* Same as A and B but different value */
    private Enums enumsDifferentValue;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        reflectionComparator = createRefectionComparator();

        enumsA = new Enums(Enums.TestEnum.TEST1);
        enumsB = new Enums(Enums.TestEnum.TEST1);
        enumsDifferentValue = new Enums(Enums.TestEnum.TEST2);
    }


    /**
     * Test for two equal enum values.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(enumsA, enumsB);
        assertNull(result);
    }


    /**
     * Test for two different enum values
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(enumsA, enumsDifferentValue);

        assertNotNull(result);
        Difference difference = getInnerDifference("testEnumValue", result);
        assertEquals(Enums.TestEnum.TEST1, difference.getLeftValue());
        assertEquals(Enums.TestEnum.TEST2, difference.getRightValue());
    }


    /**
     * Test class with enum field and failing equals.
     */
    private static class Enums {

        public enum TestEnum {
            TEST1, TEST2
        }

        @SuppressWarnings({"UnusedDeclaration"})
        private TestEnum testEnumValue;


        public Enums(TestEnum testEnumValue) {
            this.testEnumValue = testEnumValue;
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
