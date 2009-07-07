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

import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for correct handling of traversed instances pairs in ReflectionAssert.
 * <p/>
 * Special thanks to Dmitry Sidorenko, who first reported this issue and also indicated how to solve it.
 *
 * @author Dmitry Sidorenko
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertTraversedInstanceTest {


    /**
     * Test comparing objects with two lists which have intersecting list elements
     * <p/>
     * When traversing first collection child1 and child2 are marked as already compared. Child1 and child2 were found
     * not equal, but the reflection assert class only stores the fact that they were already traversed, not the
     * outcome (not equal). When traversing second collection, child1 and child2 are compared again but because they were
     * already once compared, they are found equal. Which is ofcourse incorrect.
     * <p/>
     * The reflection assert class has been changed to also remember the outcome of traversed instance pairs.
     */
    @Test
    public void doubleCheckTest() {
        Parent root1 = new Parent("root");
        Parent root2 = new Parent("root");

        Parent child1 = new Parent("child1");
        Parent child2 = new Parent("child2");

        // Adding children in forward order to first root
        root1.getChildren1().add(child1);
        root1.getChildren1().add(child2);

        // Adding children in reverse order to second root
        root2.getChildren1().add(child2);
        root2.getChildren1().add(child1);

        // Adding children in forward order to first root
        root1.getChildren2().add(child1);
        root1.getChildren2().add(child2);

        // Adding children in reverse order to second root
        root2.getChildren2().add(child2);
        root2.getChildren2().add(child1);

        ReflectionAssert.assertLenientEquals(root1, root2);
    }


    /**
     * Test comparing objects with two lists which have intersecting list elements
     * <p/>
     * Same as doubleCheckTest(), but showing a case when assert should actually fail, but it doesn't.
     * This test case is highly dependent on order of fields in collection returned by {@link Class#getDeclaredFields()}
     */
    @Test(expected = AssertionFailedError.class)
    public void doubleCheckTestShouldFail() {
        Parent root1 = new Parent("root");
        Parent root2 = new Parent("root");

        Parent child1 = new Parent("child1");
        Parent child2 = new Parent("child2");

        // Adding children in forward order to first root
        root1.getChildren1().add(child1);
        root1.getChildren1().add(child2);

        // Adding children in reverse order to second root
        root2.getChildren1().add(child2);
        root2.getChildren1().add(child1);

        // Adding children in forward order to first root
        root1.getChildren2().add(child1);

        // Adding children in reverse order to second root
        root2.getChildren2().add(child2);

        // Should fail
        ReflectionAssert.assertLenientEquals(root1, root2);
    }


    /**
     * Test class with to child lists.
     */
    private class Parent {

        private String name;

        private List<Parent> children1;
        private List<Parent> children2;

        public Parent(String name) {
            this.name = name;
            children1 = new ArrayList<Parent>();
            children2 = new ArrayList<Parent>();
        }

        public List<Parent> getChildren1() {
            return children1;
        }

        public List<Parent> getChildren2() {
            return children2;
        }

        public String toString() {
            return name;
        }
    }

}