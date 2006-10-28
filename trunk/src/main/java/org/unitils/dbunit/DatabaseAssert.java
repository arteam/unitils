package org.unitils.dbunit;

import org.unitils.core.TestContext;
import org.unitils.core.Unitils;
import org.unitils.hibernate.HibernateModule;

/**
 * @author Filip Neven
 */
public class DatabaseAssert {

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     */
    public static void assertDBContentAsExpected() throws Exception {

        TestContext testContext = Unitils.getInstance().getTestContext();

        HibernateModule hibernateModule = Unitils.getModulesRepository().getFirstModule(HibernateModule.class);
        if (hibernateModule != null) { // If Hibernate support is not activated in the Unitils configuration, the Hibernate module will be null
            if (hibernateModule.isHibernateTest(testContext.getTestClass())) {  //todo check null
                hibernateModule.flushDatabaseUpdates();
            }
        }
        DbUnitModule dbUnitModule = Unitils.getModulesRepository().getFirstModule(DbUnitModule.class);
        dbUnitModule.assertDBContentAsExpected(testContext.getTestObject(), testContext.getTestMethod().getName());
    }

}
