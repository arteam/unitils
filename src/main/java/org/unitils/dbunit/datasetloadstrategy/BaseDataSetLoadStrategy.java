/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbunit.datasetloadstrategy;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

import java.sql.SQLException;

/**
 * Base implementation of {@link DataSetLoadStrategy}. Exists only to free implementing classes from the burden of having
 * to convert checked exceptions into an unchecked {@link UnitilsException}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseDataSetLoadStrategy implements DataSetLoadStrategy {

    /**
     * Executes this DataSetLoadStrategy. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet                  The dbunit dataset
     */
    public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
        try {
            doExecute(dbUnitDatabaseConnection, dataSet);
        } catch (DatabaseUnitException e) {
            throw new UnitilsException("Error while executing DataSetLoadStrategy", e);
        } catch (SQLException e) {
            throw new UnitilsException("Error while executing DataSetLoadStrategy", e);
        }
    }

    /**
     * Executes this DataSetLoadStrategy. This means the given dbunit dataset is inserted in the database
     * using the given dbUnit database connection object. This method declares all exceptions that are thrown by dbunit,
     * so that they don't have to be taken care of in the underlying implementation.
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database
     * @param dataSet                  The dbunit dataset
     * @throws java.sql.SQLException Exception thown by dbunit
     * @throws org.dbunit.DatabaseUnitException
     *                               Exception thown by dbunit
     */
    abstract protected void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException;
}
