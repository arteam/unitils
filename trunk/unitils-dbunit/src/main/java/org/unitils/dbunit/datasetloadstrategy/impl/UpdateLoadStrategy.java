/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit.datasetloadstrategy.impl;

import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.UpdateOperation;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.connection.DbUnitDatabaseConnection;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

/**
 * {@link DataSetLoadStrategy} that updates the contents of the database with the contents of the data set. This means
 * that data of existing rows is updated. Fails if the data set contains records that are not in the database (i.e. a records having the same value for the
 * primary key column).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see DatabaseOperation#UPDATE
 */
public class UpdateLoadStrategy implements DataSetLoadStrategy {

    protected UpdateOperation updateOperation;


    public UpdateLoadStrategy(UpdateOperation updateOperation) {
        this.updateOperation = updateOperation;
    }


    /**
     * Loads the data set using DbUnit's update strategy
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database, not null
     * @param dataSet                  The dbunit data set, not null
     */
    public void loadDataSet(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
        try {
            updateOperation.execute(dbUnitDatabaseConnection, dataSet);

        } catch (Exception e) {
            throw new UnitilsException("Unable to update data set.", e);
        }
    }

}
