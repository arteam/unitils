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
package org.unitils.reflectionassert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.difference.UnorderedCollectionDifference;

import static java.util.Arrays.binarySearch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;


/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionComparator}.
 * Contains tests for ignore defaults and lenient dates.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorLenientOrderCollectionsTest {

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    @Before
    public void initialize() throws Exception {
        reflectionComparator = createRefectionComparator(LENIENT_ORDER);
    }


    /**
     * Test for UNI-156: ReflectionAssert.assertReflectionEquals is leading to an endless loop
     */
    @Test
    public void lenientOrderPerformance() {
        String[] expected = {"1", "2", "3", "4", "17", "18", "19", "20", "22", "23", "50"};
        String[] actual = {"1", "3", "4", "2", "17", "18", "19", "20", "21", "22", "23"};

        UnorderedCollectionDifference difference = (UnorderedCollectionDifference) reflectionComparator.getDifference(expected, actual);
        assertEquals(1, difference.getBestMatchingIndexes().size());
        assertBestMatch(expected, "50", actual, "21", difference);
    }

    @Test
    public void firstBestMatchIsPicked() {
        String[] expected = {"1", "2", "3"};
        String[] actual = {"4", "5", "6"};

        UnorderedCollectionDifference difference = (UnorderedCollectionDifference) reflectionComparator.getDifference(expected, actual);
        assertEquals(3, difference.getBestMatchingIndexes().size());
        assertBestMatch(expected, "1", actual, "4", difference);
        assertBestMatch(expected, "2", actual, "4", difference);
        assertBestMatch(expected, "3", actual, "4", difference);
    }


    @SuppressWarnings({"RedundantCast"})
    private void assertBestMatch(String[] expected, String expectedValue, String[] actual, String actualValue, UnorderedCollectionDifference difference) {
        int expectedIndex = binarySearch(expected, expectedValue);
        int actualIndex = binarySearch(actual, actualValue);
        Integer bestMatchingIndex = difference.getBestMatchingIndexes().get(expectedIndex);
        assertNotNull("Expected (" + expectedValue + "," + actualValue + ") as best match, but found no difference", bestMatchingIndex);
        assertEquals("Expected (" + expectedValue + "," + actualValue + ") as best match, but found (" + expected[bestMatchingIndex] + "," + actualValue + ").", actualIndex, (int) bestMatchingIndex);
    }

}