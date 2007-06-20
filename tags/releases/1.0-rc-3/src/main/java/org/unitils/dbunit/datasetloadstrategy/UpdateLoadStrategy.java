package org.unitils.dbunit.datasetloadstrategy;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * {@link org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy} that updates the contents of the database with the contents of the dataset. This means
 * that data of existing rows is updated. Fails if the dataset contains records that are not in the database (i.e. a records having the same value for the 
 * primary key column).
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @see DatabaseOperation#UPDATE
 */
public class UpdateLoadStrategy extends BaseDataSetLoadStrategy {

    @Override
    protected void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException {
        DatabaseOperation.UPDATE.execute(dbUnitDatabaseConnection, dataSet);
    }

}
