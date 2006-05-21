/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * A class for testing equality of 2 objects by using reflection. <br>
 * The {@link Object#equals} method is often used for business logic equality checking. The {@link Object#equals} method
 * can for example return true when the id fields of 2 instances have equal values, no matter what the values of the
 * other fields are. This class offers another way to check equality of objects.
 * <p/>
 * The {@link #checkEquals} methods will use reflection to get and compare the values of all fields in the objects. If
 * a field contains another object, the same reflection comparison will be done recursively on these inner objects. All
 * fields in superclasses will also be compared using reflection. Static and transient fields will be ignored.
 * <p/>
 * As an exception, the {@link Object#equals} method will be called instead of using reflection on all
 * java.lang.* type field values. Eg a field of type java.lang.Integer will be compared using its equals method. No
 * superclass comparison is done on java.lang.* type classes. Eg the java.lang.Ojbect class fields will not be compared.
 * <p/>
 * By default, a strict comparison is performed, but if needed, some leniency can be configured by use of the
 * ignoreDefaults and lenientDates attributes:
 * <ul>
 * <li>If ignoreDefaults is set, all fields that have a default java value for the right object will be ignored. Eg if
 * the right object contains an int field with value 0 it will not be compared to the value of the left object.</li>
 * <li>If lenientDates is set, the check will only see whether both objects contain a value or not, the value itself
 * is not compared. Eg. if the left object contained a date with value 1-1-2006 and the right object contained a date
 * with value 2-2-2006 they would still be considered equal.</li>
 * </ul><p/>
 * If the check indicates that both objects are not equal, the first (and only the first!) found difference can be
 * retrieved by the differenceFieldStack, differenceLeftValue and differenceRightValue properties.
 */
public class ReflectionEquals {


    /**
     * Convenience method. Same as {@link #checkEquals(Object, Object, boolean, boolean)} for a strict
     * comparison (ignoreDefaults and lenientDates set to false).
     *
     * @param left  the left instance for the comparison
     * @param right the right instance for the comparison
     * @return the result
     */
    public static ReflectionEquals checkEquals(Object left, Object right) {
        return checkEquals(left, right, false, false);
    }

    /**
     * Tests equality of 2 objects by using reflection.
     * See class javadoc for more information.
     *
     * @param left           the left instance for the comparison
     * @param right          the right instance for the comparison
     * @param ignoreDefaults true if fields with right default values should not be compared
     * @param lenientDates   true if Date fields are equals when their values are both null or both not null
     * @return the result
     */
    public static ReflectionEquals checkEquals(Object left, Object right, boolean ignoreDefaults, boolean lenientDates) {
        ReflectionEquals reflectionEquals = new ReflectionEquals(ignoreDefaults, lenientDates);
        reflectionEquals.checkEqualsImpl(left, right);
        return reflectionEquals;
    }


    /* True if the tested objects are equal, false otherwise. */
    private boolean isEquals;

    /* When isEquals is false this will contain the stack of the fieldnames where the difference was found. <br>
     * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA". */
    private Stack differenceFieldStack;

    /* When isEquals is false this will contain the left value of the field where the difference was found. */
    private Object differenceLeftValue;

    /* When isEquals is false, this will contain the right value of the field where the difference was found. */
    private Object differenceRightValue;

    /* True if fields should not be compared if the right value is a java default (0, null)
       LenientDates overrides ignoreDefaults for fields of type Date */
    private boolean ignoreDefaults;

    /* True if Date fields should not be checked on value. The values will be considered equal when they are
       both null or both not null */
    private boolean lenientDates;

    /* A utility instance used for non reflection comparing. */
    private EqualsBuilder equalsBuilder;

    /* Used for holding all traversed objects to avoid infinite loops with circular references */
    private Map traversedInstanceMap;


    /**
     * Private constructor. Everything is set to indicate equality.
     *
     * @param ignoreDefaults true if fields with right default values should not be compared
     * @param lenientDates   true if Date fields are equals when their values are both null or both not null
     */
    private ReflectionEquals(boolean ignoreDefaults, boolean lenientDates) {
        this.ignoreDefaults = ignoreDefaults;
        this.lenientDates = lenientDates;

        isEquals = true;
        differenceFieldStack = new Stack();
        differenceLeftValue = null;
        differenceRightValue = null;

        traversedInstanceMap = new HashMap();
        equalsBuilder = new EqualsBuilder();
    }


    /**
     * Returns true if the tested objects are equal, false otherwise.
     *
     * @return boolean true if equal
     */
    public boolean isEquals() {
        return isEquals;
    }


    /**
     * When isEquals is false this will return the stack of the fieldnames where the difference was found.
     * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA".
     * When isEquals is true, null will be returned.
     *
     * @return the stack of field names or null
     */
    public Stack getDifferenceFieldStack() {
        if (isEquals) {
            return null;
        }
        return differenceFieldStack;
    }


    /**
     * When isEquals is false this return a string representation of the field stack.
     * Eg primitiveFieldInB.fieldBinA.fieldA
     * When isEquals is true, null will be returned.
     *
     * @return the field names as sting or null
     */
    public String getDifferenceFieldStackAsString() {
        if (isEquals) {
            return null;
        }

        String result = "";
        Iterator iterator = differenceFieldStack.iterator();
        while (iterator.hasNext()) {
            result += iterator.next();
            if (iterator.hasNext()) {
                result += ".";
            }
        }
        return result;
    }


    /**
     * When isEquals is false this will contain the left value of the field where the difference was found.
     * When isEquals is true, null will be returned.
     *
     * @return the value or null
     */
    public Object getDifferenceLeftValue() {
        if (isEquals) {
            return null;
        }
        return differenceLeftValue;
    }


    /**
     * When isEquals is false, this will contain the right value of the field where the difference was found.
     * When isEquals is true, null will be returned.
     *
     * @return the value or null
     */
    public Object getDifferenceRightValue() {
        if (isEquals) {
            return null;
        }
        return differenceRightValue;
    }


    /**
     * Implements {@link #checkEquals(Object, Object, boolean, boolean)}.
     *
     * @param left  the left instance for the comparison
     * @param right the right instance for the comparison
     */
    private void checkEqualsImpl(Object left, Object right) {
        differenceLeftValue = left;
        differenceRightValue = right;

        //check same instances and both null
        if (left == right) {
            return;
        }

        //check (lenient) dates
        if (left instanceof Date) {
            checkEqualsDate((Date) left, (Date) right);
            return;
        }

        //check 1 is null, ignore defaults is handled compareFields
        if (left == null || right == null) {
            isEquals = false;
            return;
        }

        //check other type
        Class clazz = left.getClass();
        if (!clazz.equals(right.getClass())) {
            isEquals = false;
            return;
        }

        //check for infinite loops
        if (traversedInstanceMap.get(left) != null) {
            return;
        }
        traversedInstanceMap.put(left, left);

        //check collections, maps, primitive arrays, object arrays and objects
        if (left instanceof Collection) {
            checkEqualsCollection((Collection) left, (Collection) right);

        } else if (left instanceof Map) {
            checkEqualsMap((Map) left, (Map) right);

        } else if (clazz.isArray()) {
            if (clazz.getComponentType().isPrimitive()) {
                isEquals = equalsBuilder.append(left, right).isEquals();

            } else {
                checkEqualsArray((Object[]) left, (Object[]) right);
            }
        } else {
            if (clazz.getName().startsWith("java.lang")) {   //compare java.lang.* objects with equals()
                isEquals = equalsBuilder.append(left, right).isEquals();

            } else {
                compareFields(left, right, clazz);           //compare with reflection
            }
        }
    }


    /**
     * Checks equality of two dates.
     * If lenientDates is set both dates will be considered equals if they are both null or both not null.
     *
     * @param left  the left array for the comparison
     * @param right the right array for the comparison
     */
    private void checkEqualsDate(Date left, Date right) {
        if (!lenientDates) {
            isEquals = equalsBuilder.append(left, right).isEquals();
            return;
        }

        if ((right == null && left != null) || (right != null && left == null)) {
            isEquals = false;
        }
    }


    /**
     * Checks equality of two non-primitive arrays.
     *
     * @param left  the left array for the comparison, not null
     * @param right the right array for the comparison, not null
     */
    private void checkEqualsArray(Object[] left, Object[] right) {

        if (left.length != right.length) {
            isEquals = false;
            return;
        }

        for (int i = 0; i < left.length && isEquals; ++i) {
            differenceFieldStack.push("" + i);
            checkEqualsImpl(left[i], right[i]);
            differenceFieldStack.pop();
        }
    }


    /**
     * Checks equality of two collections.
     *
     * @param left  the left collection for the comparison, not null
     * @param right the right collection for the comparison, not null
     */
    private void checkEqualsCollection(Collection left, Collection right) {

        if (left.size() != right.size()) {
            isEquals = false;
            return;
        }

        int i = 0;
        Iterator lhsIterator = left.iterator();
        Iterator rhsIterator = right.iterator();
        while (lhsIterator.hasNext() && rhsIterator.hasNext() && isEquals) {
            differenceFieldStack.push("" + i++);
            Object lhsValue = lhsIterator.next();
            Object rhsValue = rhsIterator.next();
            checkEqualsImpl(lhsValue, rhsValue);
            differenceFieldStack.pop();
        }
    }


    /**
     * Checks equality of two maps.
     *
     * @param left  the left map for the comparison, not null
     * @param right the right map for the comparison, not null
     */
    private void checkEqualsMap(Map left, Map right) {

        if (left.size() != right.size()) {
            isEquals = false;
            return;
        }

        Iterator lhsIterator = left.entrySet().iterator();
        while (lhsIterator.hasNext()) {
            Map.Entry lhsEntry = (Map.Entry) lhsIterator.next();
            Object lhsKey = lhsEntry.getKey();

            differenceFieldStack.push("" + lhsKey);
            Object lhsValue = lhsEntry.getValue();
            Object rhsValue = right.get(lhsKey);
            checkEqualsImpl(lhsValue, rhsValue);
            if (!isEquals) {
                return;
            }
            differenceFieldStack.pop();
        }
    }


    /**
     * Compares the values of all fields in the given objects by use of reflection.
     *
     * @param left  the left object for the comparison, not null
     * @param right the right object for the comparison, not null
     * @param clazz the type of both objects
     */
    private void compareFields(Object left, Object right, Class clazz) {

        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (int i = 0; i < fields.length && isEquals; i++) {
            Field f = fields[i];
            differenceFieldStack.push(f.getName());

            //skip transient and static fields
            if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                differenceFieldStack.pop();
                continue;
            }
            try {
                Class fieldClazz = f.getType();
                Object leftValue = f.get(left);
                Object rightValue = f.get(right);

                //ignore default values if needed
                if (isIgnoredDefault(rightValue, fieldClazz)) {
                    continue;
                }

                //recursively check the value of the fields
                checkEqualsImpl(leftValue, rightValue);
                if (!isEquals) {
                    return;
                }

            } catch (IllegalAccessException e) {
                //this can't happen. Would get a Security exception instead
                //throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }
            differenceFieldStack.pop();
        }

        //check superclass
        if (isEquals) {
            Class superclazz = clazz.getSuperclass();
            while (superclazz != null && superclazz.getName().startsWith("java.lang")) {
                compareFields(left, right, superclazz);
                superclazz = superclazz.getSuperclass();
            }
        }
    }


    /**
     * Checks whether the given right value should be ignored. <br>
     * True will be returned when ignoreDefaults is true and the value is the java default value for the given type. <br>
     * Dates will be ignored if lenientDates is true.
     *
     * @param rightValue the value to check
     * @param clazz      the type of the value
     * @return true if the value should be ignored
     */
    private boolean isIgnoredDefault(Object rightValue, Class clazz) {
        if (!ignoreDefaults) {
            return false;
        }

        //lenientDates overrides ignore defaults
        if (lenientDates && clazz == Date.class) {
            return false;
        }

        //object types
        if (rightValue == null) {
            return true;
        }

        //primitive types
        if (clazz.isPrimitive() && (
                (rightValue instanceof Boolean && !((Boolean) rightValue).booleanValue()) ||
                (rightValue instanceof Character && ((Character) rightValue).charValue() == 0) ||
                (rightValue instanceof Byte && ((Byte) rightValue).byteValue() == 0) ||
                (rightValue instanceof Short && ((Short) rightValue).shortValue() == 0) ||
                (rightValue instanceof Integer && ((Integer) rightValue).intValue() == 0) ||
                (rightValue instanceof Long && ((Long) rightValue).longValue() == 0) ||
                (rightValue instanceof Float && ((Float) rightValue).floatValue() == 0) ||
                (rightValue instanceof Double && ((Double) rightValue).doubleValue() == 0))) {
            return true;
        }

        return false;
    }


}
