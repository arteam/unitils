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
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import org.unitils.reflectionassert.difference.Difference;
import static org.unitils.reflectionassert.formatter.util.InnerDifferenceFinder.getInnerDifference;

import static java.lang.Boolean.FALSE;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;


/**
 * Test class for {@link ReflectionComparator}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorTest extends TestCase {

    /* Test object */
    private Objects objectsA;

    /* Same as A but different instance */
    private Objects objectsB;

    /* Same as A and B but different string value for stringValue2 */
    private Objects objectsDifferentValue;

    /* Test object containing a null value */
    private Objects objectsNullValue;

    /* Test object with inner object */
    private Objects objectsInnerA;

    /* Same as innerA but different instance */
    private Objects objectsInnerB;

    /* Same as innerA and innerB but different int value for inner intValue2 */
    private Objects objectsInnerDifferentValue;

    /* Test object containing a circular dependency to itself */
    private Objects objectsCircularDependencyA;

    /* Same as circularDependencyA but different instance */
    private Objects objectsCircularDependencyB;

    /* Class under test */
    private ReflectionComparator reflectionComparator, ignoreDefaultReflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        objectsA = new Objects("test 1", "test 2", null);
        objectsB = new Objects("test 1", "test 2", null);
        objectsDifferentValue = new Objects("test 1", "XXXXXX", null);
        objectsNullValue = new Objects("test 1", null, null);

        objectsInnerA = new Objects(null, null, objectsA);
        objectsInnerB = new Objects(null, null, objectsB);
        objectsInnerDifferentValue = new Objects(null, null, objectsDifferentValue);

        objectsCircularDependencyA = new Objects(null, null, new Objects(null, null, new Objects(null, null, null)));
        objectsCircularDependencyB = new Objects(null, null, new Objects(null, null, new Objects(null, null, null)));

        // create a circular dependency
        objectsCircularDependencyA.getInner().getInner().setInner(objectsCircularDependencyA);
        objectsCircularDependencyB.getInner().getInner().setInner(objectsCircularDependencyB);

        reflectionComparator = createRefectionComparator();
        ignoreDefaultReflectionComparator = createRefectionComparator(IGNORE_DEFAULTS);
    }


    /**
     * Test for two equal objects.
     */
    public void testGetAllDifferences_equals() {
        Difference result = reflectionComparator.getAllDifferences(objectsA, objectsB);
        assertNull(result);
    }


    /**
     * Test for two equal objects as an inner field of an object.
     */
    public void testGetAllDifferences_equalsInner() {
        Difference result = reflectionComparator.getAllDifferences(objectsInnerA, objectsInnerB);
        assertNull(result);
    }


    /**
     * Test case for 2 equal objects that contain a circular reference. This may not cause an infinite loop.
     */
    public void testGetAllDifferences_equalsCircularDependency() {
        Difference result = reflectionComparator.getAllDifferences(objectsCircularDependencyA, objectsCircularDependencyB);
        assertNull(result);
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testGetAllDifferences_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getAllDifferences(objectsA, objectsDifferentValue);

        Difference difference = getInnerDifference("string2", result);
        assertEquals("test 2", difference.getLeftValue());
        assertEquals("XXXXXX", difference.getRightValue());
    }


    /**
     * Test case for 2 objects with a right value null.
     */
    public void testGetAllDifferences_notEqualsRightNull() {
        Difference result = reflectionComparator.getAllDifferences(objectsA, objectsNullValue);

        Difference difference = getInnerDifference("string2", result);
        assertEquals("test 2", difference.getLeftValue());
        assertEquals(null, difference.getRightValue());
    }


    /**
     * Test case for 2 objects with a left value null.
     */
    public void testGetAllDifferences_notEqualsLeftNull() {
        Difference result = reflectionComparator.getAllDifferences(objectsNullValue, objectsA);

        Difference difference = getInnerDifference("string2", result);
        assertEquals(null, difference.getLeftValue());
        assertEquals("test 2", difference.getRightValue());
    }


    /**
     * Test for objects with inner objects that contain different values.
     */
    public void testGetAllDifferences_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getAllDifferences(objectsInnerA, objectsInnerDifferentValue);

        Difference difference = getInnerDifference("string2", getInnerDifference("inner", result));
        assertEquals("test 2", difference.getLeftValue());
        assertEquals("XXXXXX", difference.getRightValue());
    }


    /**
     * Test case for a null left-argument.
     */
    public void testGetAllDifferences_leftNull() {
        Difference result = reflectionComparator.getAllDifferences(null, objectsA);

        assertEquals(null, result.getLeftValue());
        assertSame(objectsA, result.getRightValue());
    }


    /**
     * Test case for a null right-argument.
     */
    public void testGetAllDifferences_rightNull() {
        Difference result = reflectionComparator.getAllDifferences(objectsA, null);

        assertSame(objectsA, result.getLeftValue());
        assertEquals(null, result.getRightValue());
    }


    /**
     * Test case for both null arguments.
     */
    public void testGetAllDifferences_null() {
        Difference result = reflectionComparator.getAllDifferences(null, null);
        assertNull(result);
    }


    /**
     * Test for two equal objects.
     */
    public void testIsEqual() {
        boolean result = reflectionComparator.isEqual(objectsA, objectsB);
        assertTrue(result);
    }


    /**
     * Test for ignored default left value and to check that the right value is not being evaluated (causing a lazy
     * loading).
     */
    public void testGetAllDifferences_equalsIgnoredDefaultNoLazyLoading() {
        // create a proxy, that will fail if is accessed
        Collection<?> collection = (Collection<?>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Collection.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("equals".equals(method.getName())) {
                    return FALSE;
                }
                if ("hashCode".equals(method.getName())) {
                    return -1;
                }
                fail("Should not be invoked");
                return null;
            }
        });

        Difference result = ignoreDefaultReflectionComparator.getAllDifferences(new CollectionWrapper(null), new CollectionWrapper(collection));
        assertNull(result);
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testGetAllDifference_notEqualsMultipleDifferentValues() {
        objectsDifferentValue.string1 = "YYYYYY";
        Difference result = reflectionComparator.getAllDifferences(objectsA, objectsDifferentValue);

        Difference difference1 = getInnerDifference("string1", result);
        assertEquals("test 1", difference1.getLeftValue());
        assertEquals("YYYYYY", difference1.getRightValue());

        Difference difference2 = getInnerDifference("string2", result);
        assertEquals("test 2", difference2.getLeftValue());
        assertEquals("XXXXXX", difference2.getRightValue());
    }


    /**
     * Test class with failing equals.
     */
    private class Objects {

        /* A fist object value */
        private String string1;

        /* A second object value */
        private String string2;

        /* An inner object */
        private Objects inner;


        /**
         * Creates and initializes the objects instance.
         *
         * @param stringValue1 the first object value
         * @param stringValue2 the second object value
         * @param inner        the inner collection
         */
        public Objects(String stringValue1, String stringValue2, Objects inner) {
            this.string1 = stringValue1;
            this.string2 = stringValue2;
            this.inner = inner;
        }

        /**
         * Gets the first object value
         *
         * @return the value
         */
        public String getString1() {
            return string1;
        }

        /**
         * Gets the second object value
         *
         * @return the value
         */
        public String getString2() {
            return string2;
        }

        /**
         * Gets the inner object
         *
         * @return the object
         */
        public Objects getInner() {
            return inner;
        }

        /**
         * Sets the inner object
         *
         * @param inner the object
         */
        public void setInner(Objects inner) {
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


    /**
     * Test class with a Collection as field. This is declared as interface so that a proxy can be installed in the
     * field.
     */
    private class CollectionWrapper {

        /* Collection instance */
        protected Collection<?> innerCollection;

        /**
         * Creates a wrapper for the given collection.
         *
         * @param innerCollection The collection
         */
        public CollectionWrapper(Collection<?> innerCollection) {
            this.innerCollection = innerCollection;
        }
    }


}