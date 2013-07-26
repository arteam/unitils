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

import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * This test class is intended to be used while tuning the error messages of reflection assert. These tests
 * don't fail but they print out the reflection assert error messages on the console.
 * <p/>
 * todo convert to real test => implement assertions
 *
 * @author Filip Neven
 */
public class ReflectionAssertErrorMessagesTest {

    @Test
    public void testActualMoreElements() {
        List<String> expected = asList("a", "b");
        List<String> actual = asList("a", "b", "c");
        logErrorForAssertLenientEquals("actual more elements", expected, actual);
    }

    @Test
    public void testExpectedMoreElements() {
        List<String> expected = asList("a", "b", "c");
        List<String> actual = asList("a", "b");
        logErrorForAssertLenientEquals(expected, actual);
    }

    @Test
    public void testDifferentField() {
        Person expected = new Person("John", "Doe", "jDoe");
        Person actual = new Person("Jane", "Roe", "jDoe");
        logErrorForAssertLenientEquals(expected, actual);
    }

    @Test
    public void testDifferentType() {
        Person expected = new Person("John", "Doe", "jDoe");
        Car actual = new Car("Fiat");
        logErrorForAssertLenientEquals(expected, actual);
    }

    @Test
    public void testDifferentNestedCollection() {
        Person expected = new Person("John", "Doe", "jDoe", new Car("BMW"));
        Person actual = new Person("John", "Doe", "jDoe", new Car("Audi"), new Car("VW"));
        logErrorForAssertLenientEquals(expected, actual);
    }

    @Test
    public void testListOneDifferentField() {
        List<Person> expected = asList(new Person("Jack", "Ripper", "jRipper"), new Person("John", "Doe", "jDoe"));
        List<Person> actual = asList(new Person("Jack", "Ripper", "jRipper"), new Person("Jane", "Doe", "jDoe"));
        logErrorForAssertLenientEquals(expected, actual);
    }

    @Test
    public void testBestMatchInList() {
        List<Person> expected = asList(new Person("Jack", "Ripper", "jRipper"), new Person("Jane", "Doe", "jDoe"));
        List<Person> actual = asList(new Person("Ben", "Ripper", "bRipper"), new Person("John", "Doe", "jDoe"));
        logErrorForAssertLenientEquals(expected, actual);
    }

    private void logErrorForAssertLenientEquals(Object expected, Object actual) {
        logErrorForAssertLenientEquals(null, expected, actual);
    }

    private void logErrorForAssertLenientEquals(String message, Object expected, Object actual) {
        try {
            assertLenientEquals(message, expected, actual);
        } catch (AssertionFailedError e) {
            System.out.println(e);
        }
    }

}
