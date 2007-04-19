package org.unitils.reflectionassert;

import java.util.Set;
import java.util.Stack;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientNumberComparator extends ReflectionComparator {

    /**
     * todo javadoc
     *
     * @param chainedComparator
     */
    public LenientNumberComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    /**
     * todo javadoc
     *
     * @param left
     * @param right
     * @return
     */
    public boolean canHandle(Object left, Object right) {
        return (left != null && right != null) &&
                (left instanceof Character || left instanceof Number) && (right instanceof Character || right instanceof Number);
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
    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        // check if right and left have same number value (including NaN and Infinity)
        Double leftDouble = getDoubleValue(left);
        Double rightDouble = getDoubleValue(right);
        if (leftDouble.equals(rightDouble)) {
            return null;
        }
        return new Difference("Different primitive values.", left, right, fieldStack);
    }

    /**
     * Gets the double value for the given left Character or Number instance.
     *
     * @param object the Character or Number, not null
     * @return the value as a Double (this way NaN and infinity can be compared)
     */
    private Double getDoubleValue(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return (double) ((Character) object).charValue();
    }
}
