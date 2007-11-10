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
package org.unitils.reflectionassert.comparator;

import org.unitils.reflectionassert.comparator.Difference;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.comparator.Comparison;

import static java.lang.Boolean.TRUE;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Abstract superclass that defines a template for sub implementations that can compare objects of a certain kind.
 * Different instances of different subtypes will be chained to obtain a reflection comparator chain. This chain
 * will compare two objects with eachother through reflection. Depending on the composition of the chain, a number
 * of 'leniency levels' are in operation.
 * <p/>
 * If the check indicates that both objects are not equal, the first (and only the first!) found difference is returned.
 * The actual difference can then be retrieved by the fieldStack, leftValue and rightValue properties.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparator {

    /**
     * The comparator chain.
     */
    protected List<Comparator> comparators;


    /**
     * Constructs a new instance, with the given comparator as the next element in the chain. Makes sure that this
     * instance is registered as root comparator of the given chained comparator. Setting the root comparator gets
     * propagated to all elements in the chain. This way, all comparators share the same root at all times.
     *
     * @param comparators The comparator chain
     */
    public ReflectionComparator(List<Comparator> comparators) {
        this.comparators = comparators;
    }


    /**
     * Checks whether there is a difference between the left and right objects. Whether there is a difference, depends
     * on the concrete comparators in the chain.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return the difference, null if there is no difference
     */
    public Difference getDifference(Object left, Object right) {
        Comparison comparison = new ComparisonImpl(left, right, comparators, new Stack<String>(), new HashMap<TraversedInstancePair, Boolean>());
        return comparison.invokeNextComparator();
    }


    /**
     * Checks whether there is no difference between the left and right objects. The meaning of no difference is
     * determined by the set comparator modes. See class javadoc for more info.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return true if there is no difference, false otherwise
     */
    public boolean isEqual(Object left, Object right) {
        Difference difference = getDifference(left, right);
        return difference == null;
    }


    protected class ComparisonImpl implements Comparison {

        private Object left;

        private Object right;

        private List<Comparator> comparators;

        private int currentComparatorIndex;

        private Stack<String> fieldStack;

        private Map<TraversedInstancePair, Boolean> traversedInstancePairs;


        public ComparisonImpl(Object left, Object right, List<Comparator> comparators, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
            this.left = left;
            this.right = right;
            this.comparators = comparators;
            this.fieldStack = fieldStack;
            this.traversedInstancePairs = traversedInstancePairs;
        }


        public Object getLeft() {
            return left;
        }


        public Object getRight() {
            return right;
        }


        public Stack<String> getFieldStack() {
            return fieldStack;
        }


        public Difference createDifference(String message) {
            return new Difference(message, left, right, fieldStack);
        }


        public Map<TraversedInstancePair, Boolean> getTraversedInstancePairs() {
            return traversedInstancePairs;
        }


        public Difference invokeNextComparator() {
            // todo check on existence
            return comparators.get(currentComparatorIndex++).compare(this);
        }


        public Difference getInnerDifference(Object left, Object right) {
            return getDifferenceImpl(left, right, fieldStack);
        }


        public Difference getNewDifference(Object left, Object right) {
            return getDifferenceImpl(left, right, new Stack<String>());
        }


        private Difference getDifferenceImpl(Object left, Object right, Stack<String> fieldStack) {
            if (isTraversedInstancePairEqual(left, right, traversedInstancePairs)) {
                return null;
            }

            registerTraversedInstancePair(left, right, true, traversedInstancePairs);
            Comparison comparison = new ComparisonImpl(left, right, comparators, fieldStack, traversedInstancePairs);
            Difference difference = comparison.invokeNextComparator();
            registerTraversedInstancePair(left, right, (difference == null), traversedInstancePairs);
            return difference;
        }


        /**
         * Registers the fact that the given left and right object have been compared, to make sure the same two objects
         * will not be compared again (to avoid infinite loops in case of circular references)
         *
         * @param left                   The left instance
         * @param right                  The right instance
         * @param outcome                The outcome of the comparison
         * @param traversedInstancePairs Map with pairs of objects that have been compared with each other.
         */
        protected void registerTraversedInstancePair(Object left, Object right, boolean outcome, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
            if (left == null || right == null) {
                return;
            }
            traversedInstancePairs.put(new TraversedInstancePair(left, right), outcome);
        }


        /**
         * Checks whether the given left and right object have already been compared, according to the given set of
         * traversedInstancePairs. If so, this will return the outcome of the comparison. False will be returned
         * if the pair was not yet compared or is being compared.
         *
         * @param left                   the left instance
         * @param right                  the right instance
         * @param traversedInstancePairs Map with pairs of objects that have been compared with each other.
         * @return true if already compared and equal, false otherwise
         */
        protected boolean isTraversedInstancePairEqual(Object left, Object right, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
            if (left == null || right == null) {
                return false;
            }
            return traversedInstancePairs.get(new TraversedInstancePair(left, right)) == TRUE;
        }
    }


    /**
     * Value object that represents a pair of objects that have been compared with eachother. Two instances of this
     * class are equal when the leftObject and rightObject fields reference the same instances.
     */
    public static class TraversedInstancePair {

        /* The left object */
        private Object leftObject;

        /* The right object */
        private Object rightObject;


        /**
         * Constructs a new instance with the given left and right object
         *
         * @param leftObject  the left instance
         * @param rightObject the right instance
         */
        public TraversedInstancePair(Object leftObject, Object rightObject) {
            this.leftObject = leftObject;
            this.rightObject = rightObject;
        }

        /**
         * @return The left instance
         */
        public Object getLeftObject() {
            return leftObject;
        }

        /**
         * @return The right instance
         */
        public Object getRightObject() {
            return rightObject;
        }

        /**
         * @param o Another object
         * @return true when the other object is a TraversedInstancePair with the same left and right object instances.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TraversedInstancePair that = (TraversedInstancePair) o;

            if (!(leftObject == that.leftObject)) return false;
            return rightObject == that.rightObject;
        }

        /**
         * @return This object's hashcode
         */
        @Override
        public int hashCode() {
            int result;
            result = leftObject.hashCode();
            result = 31 * result + rightObject.hashCode();
            return result;
        }
    }
}
