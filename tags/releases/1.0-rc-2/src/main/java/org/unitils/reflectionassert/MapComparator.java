package org.unitils.reflectionassert;

import java.util.Stack;
import java.util.Set;
import java.util.Map;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MapComparator extends ReflectionComparator {

    /**
     * todo javadoc
     *
     * @param chainedComparator
     */
    public MapComparator(ReflectionComparator chainedComparator) {
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
        return (left != null && right != null) && (left instanceof Map && right instanceof Map);
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
        Map<?, ?> leftMap = (Map<?, ?>) left;
        Map<?, ?> rightMap = (Map<?, ?>) right;

        if (leftMap.size() != rightMap.size()) {
            return new Difference("Different map sizes.", left, right, fieldStack);
        }

        for (Map.Entry<?, ?> lhsEntry : leftMap.entrySet()) {
            Object lhsKey = lhsEntry.getKey();

            fieldStack.push("" + lhsKey);
            Object lhsValue = lhsEntry.getValue();
            Object rhsValue = rightMap.get(lhsKey);
            Difference difference = rootComparator.getDifference(lhsValue, rhsValue, fieldStack, traversedInstancePairs);
            if (difference != null) {
                return difference;
            }
            fieldStack.pop();
        }
        return null;
    }
}
