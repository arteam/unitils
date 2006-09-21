package be.ordina.unitils.testing;

import junit.framework.TestCase;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Filip Neven
 */
public class UnitilsJUnit3 extends TestCase {

    private static Unitils unitils;

    private static boolean firstTime = true;

    private static Set<Class> testClassesAlreadyRun = new HashSet<Class>();

    protected void setUp() throws Exception {
        if (firstTime) {
            firstTime = false;
            unitils = new Unitils();
            unitils.beforeSuite();
        }
        if (isFirstMethodFromClass()) {
            unitils.beforeClass(this);
        }
        unitils.beforeMethod(this, this.getName());
    }

    private boolean isFirstMethodFromClass() {
        Class testClass = this.getClass();
        if (testClassesAlreadyRun.contains(testClass)) {
            return false;
        } else {
            testClassesAlreadyRun.add(testClass);
            return true;
        }
    }

}
