package org.unitils.dbunit;

import org.unitils.core.Unitils;

/**
 * @author Filip Neven
 */
public class DatabaseAssert {

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     */
    public static void assertDBContentAsExpected() throws Exception {
        DbUnitModule dbUnitModule = Unitils.getInstance().getModulesRepositoryImpl().getModule(DbUnitModule.class);
        dbUnitModule.assertDBContentAsExpected(Unitils.getTestContext().getTestClass(), Unitils.getTestContext().getTestMethod());
    }

}
