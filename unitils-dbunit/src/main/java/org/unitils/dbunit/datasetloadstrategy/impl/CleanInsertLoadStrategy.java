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
import org.dbunit.operation.DeleteAllOperation;
import org.dbunit.operation.InsertOperation;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.connection.DbUnitDatabaseConnection;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

/**
 * {@link DataSetLoadStrategy} that first removes all data present in the tables specified
 * in the data set and then inserts the new data set, .
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertLoadStrategy implements DataSetLoadStrategy {

    protected DeleteAllOperation deleteAllOperation;
    protected InsertOperation insertOperation;


    public CleanInsertLoadStrategy(DeleteAllOperation deleteAllOperation, InsertOperation insertOperation) {
        this.deleteAllOperation = deleteAllOperation;
        this.insertOperation = insertOperation;
    }


    /**
     * Loads the data set using DbUnit's clean insert strategy
     *
     * @param dbUnitDatabaseConnection DbUnit class providing access to the database, not null
     * @param dataSet                  The dbunit data set, not null
     */
    public void loadDataSet(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
        try {
            deleteAllOperation.execute(dbUnitDatabaseConnection, dataSet);
            insertOperation.execute(dbUnitDatabaseConnection, dataSet);

        } catch (Exception e) {
            throw new UnitilsException("Unable to clean insert data set.", e);
        }
    }
}
