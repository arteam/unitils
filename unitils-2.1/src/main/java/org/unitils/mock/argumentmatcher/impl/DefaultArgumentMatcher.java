package org.unitils.mock.argumentmatcher.impl;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.reflectionassert.ReflectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import org.unitils.core.util.CloneUtil;
import static org.unitils.core.util.CloneUtil.createDeepClone;

/**
 * A matcher for checking whether an argument equals a given value. This matchers uses reference comparison if the
 * expected and actual arguments refer to the same object. Otherwise, lenient reflection comparison is used (This means
 * the actual order of collections will be ignored and only fields that have a non default value will be compared)
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 15-dec-2008
 */
public class DefaultArgumentMatcher implements ArgumentMatcher {

    /* The original value passed to the argument matcher */
    private final Object value;

    /* Copy of the original value */
    private final Object copyOfValue;

    /**
     * Creates a matcher for the given value. The original value is stored and a copy of the value is taken so that it
     * can be compared even when the value itself was modified later-on.
     *
     * @param value The expected value
     */
    public DefaultArgumentMatcher(Object value) {
        this.value = value;
        this.copyOfValue = createDeepClone(value);
    }

    /**
     * Returns true if the given object matches the expected argument, false otherwise. If the given argument refers to
     * the same object as the original value, true is returned. If the given argument is another object than the original
     * value, lenient reflection comparison is used to compare the values. This means that the actual order of collections
     * will be ignored and only fields that have a non default value will be compared.
     *
     * @param argument                 The argument that was used by reference, not null
     * @param argumentAtInvocationTime Copy of the argument, taken at the time that the invocation was performed, not null
     * @return true if the given object matches the expected argument, false otherwise
     */
    public boolean matches(Object argument, Object argumentAtInvocationTime) {
        if (value == argument) {
            return true;
        } else {
            ReflectionComparator reflectionComparator;
            if (copyOfValue instanceof Character || copyOfValue instanceof Number || copyOfValue instanceof Boolean) {
                reflectionComparator = createRefectionComparator();
            } else {
                reflectionComparator = createRefectionComparator(LENIENT_ORDER, IGNORE_DEFAULTS);
            }
            return reflectionComparator.isEqual(this.copyOfValue, argumentAtInvocationTime);
        }
    }
}
