/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.reflectionassert;

import org.apache.commons.lang.ArrayUtils;

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
 */
public class ReflectionComparator {


    /* True if fields should not be compared if the left value is a java default (0, null)
       LenientDates overrides ignoreDefaults for fields of type Date */
    private boolean ignoreDefaults = false;

    /* True if Date fields should not be checked on value. The values will be considered equal when they are
       both null or both not null */
    private boolean lenientDates = false;

    /* True if the order of arrays or collections should not be taken into account. Collections or arrays are
       considered equal when they both contain the same elements. */
    private boolean lenientOrder = false;

    /**
     * Creates a comparator using the given modes.
     * If no modes are given, a srict comparison will be performed.
     *
     * @param modes the comparator modes
     */
    public ReflectionComparator(ReflectionComparatorModes... modes) {

        if (modes == null) {
            return;
        }

        for (ReflectionComparatorModes mode : modes) {

            if (mode == ReflectionComparatorModes.IGNORE_DEFAULTS) {
                ignoreDefaults = true;

            } else if (mode == ReflectionComparatorModes.LENIENT_DATES) {
                lenientDates = true;

            } else if (mode == ReflectionComparatorModes.LENIENT_ORDER) {
                lenientOrder = true;
            }
        }
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

    /**
     * Checks whether there is a difference between the left and right objects. The meaning of no difference is
     * determined by the set comparator modes. See class javadoc for more info.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return the difference, null if there is no difference
     */
    public Difference getDifference(Object left, Object right) {
        return getDifferenceImpl(left, right, new Stack<String>(), new HashMap<Object, Object>());
    }


    /**
     * Implements {@link #getDifference(Object, Object)}.
     *
     * @param left                 the left instance for the comparison
     * @param right                the right instance for the comparison
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference getDifferenceImpl(Object left, Object right, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        // check same instances or both null
        if (left == right) {
            return null;
        }

        // check (lenient) dates
        // lenient dates overrides ignore defaults
        if ((left == null || left instanceof Date) && (right == null || right instanceof Date)) {
            return compareDates((Date) left, (Date) right, fieldStack);
        }

        // ignore default values if needed
        if (isIgnoredDefault(left)) {
            return null;
        }

        // check left or right is null
        if (left == null) {
            return new Difference("Left value null.", left, right, fieldStack);
        }
        if (right == null) {
            return new Difference("Right value null.", left, right, fieldStack);
        }

        // Ignore traversed instance to avoid infinite loops
        if (traversedInstanceMap.get(left) != null) {
            return null;
        }
        traversedInstanceMap.put(left, left);

        // check collections
        if (left instanceof Collection) {
            return compareCollections((Collection<Object>) left, (Collection<Object>) right, fieldStack, traversedInstanceMap);
        }

        // check maps
        if (left instanceof Map) {
            return compareMaps((Map<Object, Object>) left, (Map<Object, Object>) right, fieldStack, traversedInstanceMap);
        }

        // check primitive and object arrays
        Class clazz = left.getClass();
        if (clazz.isArray()) {
            return compareArrays(left, right, clazz, fieldStack, traversedInstanceMap);
        }

        // check objects
        return compareObjects(left, right, clazz, fieldStack, traversedInstanceMap);
    }


    /**
     * Checks equality of two dates.
     * If lenientDates is set both dates will be considered equals if they are both null or both not null.
     *
     * @param left       the left date for the comparison
     * @param right      the right date for the comparison
     * @param fieldStack the current field names
     * @return the difference, null if there is no difference
     */
    private Difference compareDates(Date left, Date right, Stack<String> fieldStack) {
        if (!lenientDates) {

            // check ignored default
            if (ignoreDefaults && left == null) {
                return null;
            }

            // check date values
            if (left != null) {
                if (left.equals(right)) {
                    return null;
                }
            } else if (right == null || right.equals(left)) {
                return null;
            }
            return new Difference("Different date values.", left, right, fieldStack);
        }

        if ((right == null && left != null) || (right != null && left == null)) {
            return new Difference("Lenient dates, but not both value or both null.", left, right, fieldStack);
        }
        return null;
    }


    /**
     * Checks equality of two arrays.
     *
     * @param left                 the left array for the comparison, not null and same type as right
     * @param right                the right array for the comparison, not null and same type as right
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference compareArrays(Object left, Object right, Class clazz, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        // If needed convert primitive array to object array
        Object[] leftObjectArray;
        Object[] rightObjectArray;
        if (left instanceof byte[]) {
            leftObjectArray = ArrayUtils.toObject((byte[]) left);
            rightObjectArray = ArrayUtils.toObject((byte[]) right);

        } else if (left instanceof short[]) {
            leftObjectArray = ArrayUtils.toObject((short[]) left);
            rightObjectArray = ArrayUtils.toObject((short[]) right);

        } else if (left instanceof int[]) {
            leftObjectArray = ArrayUtils.toObject((int[]) left);
            rightObjectArray = ArrayUtils.toObject((int[]) right);

        } else if (left instanceof long[]) {
            leftObjectArray = ArrayUtils.toObject((long[]) left);
            rightObjectArray = ArrayUtils.toObject((long[]) right);

        } else if (left instanceof char[]) {
            leftObjectArray = ArrayUtils.toObject((char[]) left);
            rightObjectArray = ArrayUtils.toObject((char[]) right);

        } else if (left instanceof float[]) {
            leftObjectArray = ArrayUtils.toObject((float[]) left);
            rightObjectArray = ArrayUtils.toObject((float[]) right);

        } else if (left instanceof double[]) {
            leftObjectArray = ArrayUtils.toObject((double[]) left);
            rightObjectArray = ArrayUtils.toObject((double[]) right);

        } else if (left instanceof boolean[]) {
            leftObjectArray = ArrayUtils.toObject((boolean[]) left);
            rightObjectArray = ArrayUtils.toObject((boolean[]) right);
        } else {
            leftObjectArray = (Object[]) left;
            rightObjectArray = (Object[]) right;
        }

        if (leftObjectArray.length != rightObjectArray.length) {
            return new Difference("Different array lengths. Left length: " + leftObjectArray.length + ", right size: " + rightObjectArray.length, left, right, fieldStack);
        }

        // Convert to list and compare as collection
        List<Object> leftList = Arrays.asList(leftObjectArray);
        List<Object> rightList = Arrays.asList(rightObjectArray);

        Difference difference = compareCollections(leftList, rightList, fieldStack, traversedInstanceMap);

        // If needed switch back array in place of list
        if (difference != null) {
            if (difference.leftValue == leftList) {
                difference.leftValue = left;
            }
            if (difference.rightValue == rightList) {
                difference.rightValue = right;
            }
        }
        return difference;
    }

    /**
     * Checks equality of two collections.
     *
     * @param left                 the left collection for the comparison, not null
     * @param right                the right collection for the comparison, not null
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference compareCollections(Collection<?> left, Collection<?> right, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        if (left.size() != right.size()) {
            return new Difference("Different collection sizes.", left, right, fieldStack);
        }

        if (lenientOrder) {
            return compareCollectionsLenientOrder(left, right, fieldStack);
        }
        return compareCollectionsStrictOrder(left, right, fieldStack, traversedInstanceMap);
    }

    /**
     * Checks equality of two collections taking the order of the elements into account. The order used is the order
     * defined by the iterators of the collections.
     *
     * @param left                 the left collection for the comparison, not null
     * @param right                the right collection for the comparison, not null
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference compareCollectionsStrictOrder(Collection<?> left, Collection<?> right, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        int i = 0;
        Iterator lhsIterator = left.iterator();
        Iterator rhsIterator = right.iterator();
        while (lhsIterator.hasNext() && rhsIterator.hasNext()) {
            fieldStack.push("" + i++);
            Object lhsValue = lhsIterator.next();
            Object rhsValue = rhsIterator.next();
            Difference difference = getDifferenceImpl(lhsValue, rhsValue, fieldStack, traversedInstanceMap);
            if (difference != null) {
                return difference;
            }
            fieldStack.pop();
        }
        return null;
    }


    /**
     * Checks equality of two collections but does not look at the order of the list.
     * This will look at all elements in the left collection and try to find a match in the rigth collecction.
     * The first match that is found will be considered to be the correct match. A later element in the left
     * collection can no longer be matched to this element.
     *
     * @param left       the left collection for the comparison, not null
     * @param right      the right collection for the comparison, not null
     * @param fieldStack the current field names
     * @return the difference, null if there is no difference
     */
    private Difference compareCollectionsLenientOrder(Collection<?> left, Collection<?> right, Stack<String> fieldStack) {

        // Create copy from which we can remove elements.
        ArrayList rightCopy = new ArrayList<Object>(right);

        int i = 0;
        for (Object lhsValue : left) {

            boolean found = false;
            Iterator rhsIterator = rightCopy.iterator();
            while (rhsIterator.hasNext()) {
                Object rhsValue = rhsIterator.next();

                // Compare values using reflection
                boolean equal = isEqual(lhsValue, rhsValue);
                if (equal) {
                    rhsIterator.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                return new Difference("Left value not found in right collection/array. Left value: " + lhsValue, left, right, fieldStack);
            }
        }
        return null;
    }


    /**
     * Checks equality of two maps.
     *
     * @param left                 the left map for the comparison, not null
     * @param right                the right map for the comparison, not null
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference compareMaps(Map<Object, Object> left, Map<Object, Object> right, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        if (left.size() != right.size()) {
            return new Difference("Different map sizes.", left, right, fieldStack);
        }

        for (Map.Entry<Object, Object> lhsEntry : left.entrySet()) {
            Object lhsKey = lhsEntry.getKey();

            fieldStack.push("" + lhsKey);
            Object lhsValue = lhsEntry.getValue();
            Object rhsValue = right.get(lhsKey);
            Difference difference = getDifferenceImpl(lhsValue, rhsValue, fieldStack, traversedInstanceMap);
            if (difference != null) {
                return difference;
            }
            fieldStack.pop();
        }
        return null;
    }


    /**
     * Checks equality of two objects.
     *
     * @param left                 the left instance for the comparison, not null
     * @param right                the right instance for the comparison, not null
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference compareObjects(Object left, Object right, Class clazz, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        // check different class type
        if (!clazz.equals(right.getClass())) {
            return new Difference("Different class types. Left: " + clazz + ", right: " + right.getClass(), left, right, fieldStack);
        }

        // compare java.lang.* objects with equals()
        if (clazz.getName().startsWith("java.lang")) {
            if (left.equals(right)) {
                return null;
            }
            return new Difference("Different object values.", left, right, fieldStack);
        }

        // compare all fields of the object using reflection
        return compareFields(left, right, clazz, fieldStack, traversedInstanceMap);
    }


    /**
     * Compares the values of all fields in the given objects by use of reflection.
     *
     * @param left                 the left object for the comparison, not null
     * @param right                the right object for the comparison, not null
     * @param clazz                the type of both objects
     * @param fieldStack           the current field names
     * @param traversedInstanceMap used for holding all traversed objects to avoid infinite loops with circular references
     * @return the difference, null if there is no difference
     */
    private Difference compareFields(Object left, Object right, Class clazz, Stack<String> fieldStack, Map<Object, Object> traversedInstanceMap) {

        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (Field f : fields) {
            fieldStack.push(f.getName());

            // skip transient and static fields
            if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                fieldStack.pop();
                continue;
            }
            try {
                Object leftValue = f.get(left);
                Object rightValue = f.get(right);

                // recursively check the value of the fields
                Difference difference = getDifferenceImpl(leftValue, rightValue, fieldStack, traversedInstanceMap);
                if (difference != null) {
                    return difference;
                }

            } catch (IllegalAccessException e) {
                // this can't happen. Would get a Security exception instead
                // throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }
            fieldStack.pop();
        }

        // check superclass
        Class superclazz = clazz.getSuperclass();
        while (superclazz != null && !superclazz.getName().startsWith("java.lang")) {
            Difference difference = compareFields(left, right, superclazz, fieldStack, traversedInstanceMap);
            if (difference != null) {
                return difference;
            }
            superclazz = superclazz.getSuperclass();
        }
        return null;
    }


    /**
     * Checks whether the given right value should be ignored. <br>
     * True will be returned when ignoreDefaults is true and the value is the java default value for the given type. <br>
     * Dates will be ignored if lenientDates is true.
     *
     * @param value the value to check
     * @return true if the value should be ignored
     */
    private boolean isIgnoredDefault(Object value) {

        if (!ignoreDefaults) {
            return false;
        }

        // object types
        if (value == null) {
            return true;
        }

        // primitive types
        return (value instanceof Boolean && !(Boolean) value) ||
                (value instanceof Character && (Character) value == 0) ||
                (value instanceof Byte && (Byte) value == 0) ||
                (value instanceof Short && (Short) value == 0) ||
                (value instanceof Integer && (Integer) value == 0) ||
                (value instanceof Long && (Long) value == 0) ||
                (value instanceof Float && (Float) value == 0) ||
                (value instanceof Double && (Double) value == 0);
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
        private Difference(String message, Object leftValue, Object rightValue, Stack fieldStack) {
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
}
