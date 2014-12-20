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
import org.dbunit.operation.RefreshOperation;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.connection.DbUnitConnection;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

/**
 * {@link org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy} that 'refreshes' the contents of the database with the contents of the data set. This means
 * that data of existing rows is updated and non-existing rows are inserted. Any rows that are in the database but not
 * in the data set stay unaffected.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see DatabaseOperation#REFRESH
 */
public class RefreshLoadStrategy implements DataSetLoadStrategy {

    protected RefreshOperation refreshOperation;


    public RefreshLoadStrategy(RefreshOperation refreshOperation) {
        this.refreshOperation = refreshOperation;
    }


    /**
     * Loads the data set using DbUnit's refresh strategy
     *
     * @param dbUnitConnection DbUnit class providing access to the database, not null
     * @param dataSet          The dbunit data set, not null
     */
    public void loadDataSet(DbUnitConnection dbUnitConnection, IDataSet dataSet) {
        try {
            refreshOperation.execute(dbUnitConnection, dataSet);

        } catch (Exception e) {
            throw new UnitilsException("Unable to refresh data set.", e);
        }
    }
}
