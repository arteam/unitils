/*
 * Copyright 2006 the original author or authors.
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

import org.apache.commons.lang.ArrayUtils;
import org.unitils.core.UnitilsException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * A class for testing equality of 2 objects using reflection. <br>
 * The {@link Object#equals} method is often used for business logic equality checking. The {@link Object#equals} method
 * can for example return true when the id fields of 2 instances have equal values, no matter what the values of the
 * other fields are. This class offers another way to check equality of objects.
 * <p/>
 * The {@link #getDifference} methods will use reflection to get and compare the values of all fields in the objects. If
 * a field contains another object, the same reflection comparison will be done recursively on these inner objects. All
 * fields in superclasses will also be compared using reflection. Static and transient fields will be ignored.
 * <p/>
 * As an exception, the {@link Object#equals} method will be called instead of using reflection on all
 * java.lang.* type field values. Eg a field of type java.lang.Integer will be compared using its equals method. No
 * superclass comparison is done on java.lang.* type classes. Eg the java.lang.Ojbect class fields will not be compared.
 * <p/>
 * If an object is an array or a collection, all its elements will be traversed and compared with the other array or
 * collection in the same way using reflection. The actual type of collection or whether a collection is compared with
 * an array is not important. It will only go through the array or collection and compare the elements. For example, an
 * Arraylist can be compared with an array or a LinkedList.
 * <p/>
 * By default, a strict comparison is performed, but if needed, some leniency can be configured by setting one or more
 * comparator modes: <ul>
 * <li>ignore defaults: all fields that have a default java value for the left object will be ignored. Eg if
 * the left object contains an int field with value 0 it will not be compared to the value of the right object.</li>
 * <li>lenient dates: only check whether both Date objects contain a value or not, the value itself
 * is not compared. Eg. if the left object contained a date with value 1-1-2006 and the right object contained a date
 * with value 2-2-2006 they would still be considered equal.</li>
 * <li>lenient order: only check whether both collections or arrays contain the same value, the actual order of the
 * values is not compared. Eg. if the left object is int[]{ 1, 2} and the right value is int[]{2, 1} they would still
 * be considered equal.</li>
 * </ul>
 * If the check indicates that both objects are not equal, the first (and only the first!) found difference is returned.
 * The actual difference can then be retrieved by the fieldStack, leftValue and rightValue properties.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
abstract public class ReflectionComparator {

    /**
     * todo javadoc
     */
    protected ReflectionComparator rootComparator;

    /**
     * todo javadoc
     */
    protected ReflectionComparator chainedComparator;

    /**
     * todo javadoc
     *
     * @param chainedComparator
     */
    public ReflectionComparator(ReflectionComparator chainedComparator) {
        this.chainedComparator = chainedComparator;
        setRootComparator(this);
    }

    /**
     * todo javadoc
     *
     * @param rootComparator
     */
    protected void setRootComparator(ReflectionComparator rootComparator) {
        this.rootComparator = rootComparator;
        if (chainedComparator != null) {
            chainedComparator.setRootComparator(rootComparator);
        }
    }

    /**
     * Indicates whether this ReflectionComparator is able to check whether their is a difference in the given left
     * and right objects or not.
     *
     * @param left The left object
     * @param right The right object
     * @return true if this ReflectionComparator is able to check whether their is a difference in the given left
     * and right objects, false otherwise
     */
    abstract public boolean canHandle(Object left, Object right);


    /**
     * Checks whether there is a difference between the left and right objects. The meaning of no difference is
     * determined by the set comparator modes. See class javadoc for more info.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return the difference, null if there is no difference
     */
    public Difference getDifference(Object left, Object right) {
        return getDifference(left, right, new Stack<String>(), new HashSet<TraversedInstancePair>());
    }


    /**
     * If this ReflectionComparator is able to check whether their is a difference in the given left
     * and right objects (i.e. {@link #canHandle(Object, Object)} returns true), the objects are compared.
     * todo javadoc
     *
     * @param left
     * @param right
     * @param fieldStack
     * @param traversedInstancePairs
     * @return
     */
    protected Difference getDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        if (isAlreadyTraversedInstancePair(traversedInstancePairs, left, right)) return null;
        if (canHandle(left, right)) {
            registerTraversedInstancePair(left, right, traversedInstancePairs);
            return doGetDifference(left, right, fieldStack, traversedInstancePairs);
        } else {
            if (chainedComparator == null) {
                throw new UnitilsException("No ReflectionComparator found for objects " + left + " and" + right + " at " + fieldStack.toString());
            } else {
                return chainedComparator.getDifference(left, right, fieldStack, traversedInstancePairs);
            }
        }
    }

    /**
     * todo javadoc
     *
     * @param left
     * @param right
     * @param fieldStack
     * @param traversedInstancePairs
     * @return
     */
    abstract protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs);


    /**
     * Checks whether there is no difference between the left and right objects. The meaning of no difference is
     * determined by the set comparator modes. See class javadoc for more info.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return true if there is no difference, false otherwise
     */
    public boolean isEqual(Object left, Object right) {
        Difference difference = rootComparator.getDifference(left, right);
        return difference == null;
    }

    /**
     * todo javadoc
     *
     * @param left
     * @param right
     * @param traversedInstancePairs
     */
    private void registerTraversedInstancePair(Object left, Object right, Set<TraversedInstancePair> traversedInstancePairs) {
        if (left != null && right != null) {
            traversedInstancePairs.add(new TraversedInstancePair(left, right));
        }
    }

    /**
     * todo javadoc
     *
     * @param traversedInstancePairs
     * @param left
     * @param right
     * @return
     */
    private boolean isAlreadyTraversedInstancePair(Set<TraversedInstancePair> traversedInstancePairs, Object left, Object right) {
        if (left == null || right == null) {
            return false;
        } else {
            return traversedInstancePairs.contains(new TraversedInstancePair(left, right));
        }
    }


    /**
     * A class for holding the difference between two objects.
     */
    public static class Difference {

        /* A message describing the difference */
        private String message;

        /* When isEquals is false this will contain the stack of the fieldnames where the difference was found. <br>
         * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA". */
        private Stack fieldStack;

        /* When isEquals is false this will contain the left value of the field where the difference was found. */
        private Object leftValue;

        /* When isEquals is false, this will contain the right value of the field where the difference was found. */
        private Object rightValue;


        /**
         * Creates a difference.
         *
         * @param message    a message describing the difference
         * @param leftValue  the left instance
         * @param rightValue the right instance
         * @param fieldStack the current field names
         */
        protected Difference(String message, Object leftValue, Object rightValue, Stack fieldStack) {
            this.message = message;
            this.leftValue = leftValue;
            this.rightValue = rightValue;
            this.fieldStack = fieldStack;
        }

        /**
         * Gets a string representation of the field stack.
         * Eg primitiveFieldInB.fieldBinA.fieldA
         * The top-level element is an empty string.
         *
         * @return the field names as sting
         */
        public String getFieldStackAsString() {
            String result = "";
            Iterator iterator = fieldStack.iterator();
            while (iterator.hasNext()) {
                result += iterator.next();
                if (iterator.hasNext()) {
                    result += ".";
                }
            }
            return result;
        }


        /**
         * Gets the message indicating the kind of difference.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }


        /**
         * Gets the stack of the fieldnames where the difference was found.
         * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA".
         * The top-level element has an empty stack.
         *
         * @return the stack of field names, not null
         */
        public Stack getFieldStack() {
            return fieldStack;
        }


        /**
         * Gets the left value of the field where the difference was found.
         *
         * @return the value
         */
        public Object getLeftValue() {
            return leftValue;
        }


        /**
         * Gets the right value of the field where the difference was found.
         *
         * @return the value
         */
        public Object getRightValue() {
            return rightValue;
        }
    }

    /**
     * todo javadoc
     */
    protected static class TraversedInstancePair {

        private Object leftObject;

        private Object rightObject;

        public TraversedInstancePair(Object leftObject, Object rightObject) {
            this.leftObject = leftObject;
            this.rightObject = rightObject;
        }

        public Object getLeftObject() {
            return leftObject;
        }

        public Object getRightObject() {
            return rightObject;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TraversedInstancePair that = (TraversedInstancePair) o;

            if (!(leftObject == that.leftObject)) return false;
            if (!(rightObject == that.rightObject)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = leftObject.hashCode();
            result = 31 * result + rightObject.hashCode();
            return result;
        }
    }
}
