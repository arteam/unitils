package be.ordina.unitils;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * @author Filip Neven
 */
public class UnitilsTestNG implements IHookable {

    private Unitils unitils;

    @BeforeSuite
    protected void unitilsBeforeSuite() throws Exception {
        unitils = new Unitils();
        unitils.beforeAll();
    }

    @BeforeClass
    protected void unitilsBeforeClass() throws Exception {
        unitils.beforeTestClass(this);
    }

    /**
     * This method is invoked automatically by the TestNG framework. We use this
     *
     * @param callBack
     * @param testResult
     */
    public void run(IHookCallBack callBack, ITestResult testResult) {

        unitils.beforeTestMethod(this, testResult.getName());
        callBack.runTestMethod(testResult);
    }
}
