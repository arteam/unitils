package be.ordina.unitils;

import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

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
