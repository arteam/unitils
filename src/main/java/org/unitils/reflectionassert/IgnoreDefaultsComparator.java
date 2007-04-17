package org.unitils.reflectionassert;

import java.util.Stack;
import java.util.Set;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class IgnoreDefaultsComparator extends ReflectionComparator {

    /**
     * todo javadoc
     *
     * @param chainedComparator
     */
    public IgnoreDefaultsComparator(ReflectionComparator chainedComparator) {
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
        // object types
        if (left == null) {
            return true;
        }

        // primitive types
        return (left instanceof Boolean && !(Boolean) left) ||
                (left instanceof Character && (Character) left == 0) ||
                (left instanceof Number && ((Number) left).doubleValue() == 0);
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
        return null;
    }
}
