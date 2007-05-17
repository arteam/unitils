package org.unitils.dbunit.dataSetOperation;

import org.unitils.dbunit.util.DbUnitDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;

import java.sql.SQLException;

/**
 * {@link DataSetOperation} that inserts the contents of the dataset into the database.

 * @see org.dbunit.operation.DatabaseOperation#INSERT
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InsertOperation extends BaseDataSetOperation {

    /**
     * Executes this DataSetOperation. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet The dbunit dataset
     */
    public void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        DatabaseOperation.INSERT.execute(dbUnitDatabaseConnection, dataSet);
    }
}
