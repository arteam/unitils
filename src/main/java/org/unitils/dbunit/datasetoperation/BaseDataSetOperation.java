package org.unitils.dbunit.datasetoperation;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

import java.sql.SQLException;

/**
 * Base implementation of {@link DataSetOperation}. Exists only to free implementing classes from the burden of having
 * to convert checked exceptions into an unchecked {@link UnitilsException}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseDataSetOperation implements DataSetOperation {

    /**
     * Executes this DataSetOperation. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet The dbunit dataset
     */
    public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
        try {
            doExecute(dbUnitDatabaseConnection, dataSet);
        } catch (DatabaseUnitException e) {
            throw new UnitilsException("Error while executing DataSetOperation", e);
        } catch (SQLException e) {
            throw new UnitilsException("Error while executing DataSetOperation", e);
        }
    }

    /**
     * Executes this DataSetOperation. This means the given dbunit dataset is inserted in the database
     * using the given dbUnit database connection object. This method declares all exceptions that are thrown by dbunit,
     * so that they don't have to be taken care of in the underlying implementation.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet The dbunit dataset
     * @throws java.sql.SQLException Exception thown by dbunit
     * @throws org.dbunit.DatabaseUnitException Exception thown by dbunit
     */
    abstract protected void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException;
}
