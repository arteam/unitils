package org.unitils.dbunit.datasetoperation;

import org.dbunit.dataset.IDataSet;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * Defines the contract for implementations that specify an operation that needs to be executed on the database, given
 * a DbUnit dataset. Implementations typically call an implementation of DbUnit's <code>DatabaseOperation</code> class.
 * Implementations must have an empty constructor so that an instance can be created using reflection.
 *
 * The concrete implementation class that is used can be configured using the annotation attribute
 * {@link org.unitils.dbunit.annotation.DataSet#operation()}. A default can be specified using the property
 * <code>DbUnitModule.DataSet.operation.default</code>. 
 *
 * This wrapper mechanism makes it very easy to use custom DbUnit <code>DatabaseOperation</code> composite object
 * structures, without sacrificing the powerfulness of Unitils' configuration system.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DataSetOperation {

    /**
     * Executes this DataSetOperation. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet The dbunit dataset
     */
    public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet);
}
