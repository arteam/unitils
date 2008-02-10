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
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.ReflectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;

/**
 * todo javadoc + cleanup 
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionComparatorSharedReferencesTest extends TestCase {

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    private References leaf1 = new References("Leaf1", null, null);

    private References leaf1Copy = new References("Leaf1", null, null);

    private References leaf2 = new References("Leaf2", null, null);

    private References doubleReferenced = new References("Trunk", leaf1, leaf1);

    private References equalToDoubleReferenced = new References("Trunk", leaf1, leaf1Copy);

    private References notEqualToDoubleReferenced = new References("Trunk", leaf1, leaf2);

    private References nestedDoubleReferenced = new References("Trunk", leaf1, new References("Branch", leaf1, null));

    private References equalToNestedDoubleReferenced = new References("Trunk", leaf1Copy, new References("Branch", leaf1Copy, null));

    private References notEqualToNestedDoubleReferenced1 = new References("Trunk", leaf1Copy, new References("Branch", leaf2, null));

    private References circularReferenced = new References("Trunk", leaf1, null); // circular reference created in setUp

    private References equalToCircularReferenced = new References("Trunk", leaf1, new References("Trunk", leaf1Copy, circularReferenced));


    protected void setUp() throws Exception {
        super.setUp();

        reflectionComparator = createRefectionComparator();

        // Create circular reference
        circularReferenced.setRef2(circularReferenced);
    }

    public void testDoubleReferenced_equal() {
        Difference diff = reflectionComparator.getDifference(doubleReferenced, equalToDoubleReferenced);
        assertNull(diff);
    }

    public void testDoubleReferenced_notEqual() {
        Difference diff = reflectionComparator.getDifference(doubleReferenced, notEqualToDoubleReferenced);
        assertNotNull(diff);
    }

    public void testNestedDoubleReferenced_equal() {
        Difference diff = reflectionComparator.getDifference(nestedDoubleReferenced, equalToNestedDoubleReferenced);
        assertNull(diff);
    }

    public void testNestedDoubleReferenced_notEqual() {
        Difference diff = reflectionComparator.getDifference(nestedDoubleReferenced, notEqualToNestedDoubleReferenced1);
        // todo failing test, failing statement commented out
        assertNotNull(diff);
    }

    public void testCircularReferenced_equal() {
        Difference diff = reflectionComparator.getDifference(circularReferenced, equalToCircularReferenced);
        assertNull(diff);
    }

    public void testCircularReferenced_notEqual() {
        Difference diff = reflectionComparator.getDifference(circularReferenced, nestedDoubleReferenced);
        // todo failing test, failing statement commented out
        assertNotNull(diff);
    }

    @SuppressWarnings({"unused", "UnusedDeclaration"})
    private static class References {

        private String name;

        private References ref1;

        private References ref2;

        public References(String name, References ref1, References ref2) {
            this.name = name;
            this.ref1 = ref1;
            this.ref2 = ref2;
        }

        public void setRef1(References ref1) {
            this.ref1 = ref1;
        }

        public void setRef2(References ref2) {
            this.ref2 = ref2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            References that = (References) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

    }
}
