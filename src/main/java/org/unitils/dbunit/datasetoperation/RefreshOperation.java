package org.unitils.dbunit.datasetoperation;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

import java.sql.SQLException;

/**
 * {@link org.unitils.dbunit.datasetoperation.DataSetOperation} that 'refreshes' the contents of the database with the contents of the dataset. This means
 * that data of existing rows are updated and non-existing rows are inserted. Any rows that are in the database but not
 * in the dataset stay unaffected.

 * @see org.dbunit.operation.DatabaseOperation#REFRESH
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class RefreshOperation extends BaseDataSetOperation {

    /**
     * Executes this DataSetOperation. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet The dbunit dataset
     */
    public void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        DatabaseOperation.REFRESH.execute(dbUnitDatabaseConnection, dataSet);
    }
}
