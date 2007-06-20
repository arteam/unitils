package org.unitils.reflectionassert;

import java.util.Stack;
import java.util.Date;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientDatesComparator extends ReflectionComparator {


    public LenientDatesComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    public boolean canHandle(Object left, Object right) {
        return (left == null || left instanceof Date) && (right == null || right instanceof Date);
    }

    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        if ((right == null && left != null) || (right != null && left == null)) {
            return new Difference("Lenient dates, but not both instantiated or both null.", left, right, fieldStack);
        }
        return null;
    }
}
