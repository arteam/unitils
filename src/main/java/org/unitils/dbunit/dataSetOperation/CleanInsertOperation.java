package org.unitils.dbunit.dataSetOperation;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.DatabaseUnitException;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

import java.sql.SQLException;

/**
 * {@link DataSetOperation} that inserts a dataset, after removes all present data from the tables specified in the dataset.

 * @see org.dbunit.operation.DatabaseOperation#CLEAN_INSERT
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class CleanInsertOperation extends BaseDataSetOperation {

    /**
     * Executes this DataSetOperation. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet The dbunit dataset
     */
    public void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        DatabaseOperation.CLEAN_INSERT.execute(dbUnitDatabaseConnection, dataSet);
    }
}
