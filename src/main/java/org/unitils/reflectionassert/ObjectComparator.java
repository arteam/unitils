package org.unitils.reflectionassert;

import java.util.Stack;
import java.util.Set;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectComparator extends ReflectionComparator {


    public ObjectComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    public boolean canHandle(Object left, Object right) {
        return left != null && right != null;
    }

    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        // check different class type
        Class clazz = left.getClass();
        if (!clazz.equals(right.getClass())) {
            return new Difference("Different class types. Left: " + clazz + ", right: " + right.getClass(), left, right, fieldStack);
        }
        // compare all fields of the object using reflection
        return compareFields(left, right, clazz, fieldStack, traversedInstancePairs);
    }

    /**
     * Compares the values of all fields in the given objects by use of reflection.
     *
     * @param left                 the left object for the comparison, not null
     * @param right                the right object for the comparison, not null
     * @param clazz                the type of both objects
     * @param fieldStack           the current field names
     * @param traversedInstancePairs
     * @return the difference, null if there is no difference
     */
    protected Difference compareFields(Object left, Object right, Class clazz, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
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

                // recursively check the value of the fields
                Difference difference = rootComparator.getDifference(f.get(left), f.get(right), fieldStack, traversedInstancePairs);
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

        // compare fields declared in superclass
        Class superclazz = clazz.getSuperclass();
        while (superclazz != null && !superclazz.getName().startsWith("java.lang")) {
            Difference difference = compareFields(left, right, superclazz, fieldStack, traversedInstancePairs);
            if (difference != null) {
                return difference;
            }
            superclazz = superclazz.getSuperclass();
        }
        return null;
    }
}
