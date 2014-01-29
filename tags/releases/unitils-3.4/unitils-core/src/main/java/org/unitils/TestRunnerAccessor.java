
package org.unitils;

import java.lang.reflect.Method;

/**
 * Interface to expose the capability of executing test methods to the outside
 * world.
 * 
 * @author jef
 */
public interface TestRunnerAccessor {

    /**
     * Execute a test method on a given tested object.
     * @param testedObject
     * @param testMethod 
     */
    void executeTestMethod(Object testedObject, Method testMethod);
}
