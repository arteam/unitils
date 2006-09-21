package be.ordina.unitils.testing;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Filip Neven
 */
public class UnitilsJUnit4 {

    private static Unitils unitils;

    private static boolean firstTime = true;

    private static Set<Class> testClassesAlreadyRun = new HashSet<Class>();

    @Before
    public void unitilsBeforeMethod() throws Exception {
        if (firstTime) {
            firstTime = false;
            unitils = new Unitils();
            unitils.beforeSuite();
        }
        if (isFirstMethodFromClass()) {
            unitils.beforeClass(this);
        }
        unitils.beforeMethod(this, null);
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
