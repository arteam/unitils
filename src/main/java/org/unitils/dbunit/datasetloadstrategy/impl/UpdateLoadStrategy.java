/*
 * Copyright 2008,  Unitils.org
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

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;
import org.unitils.dbunit.datasetloadstrategy.impl.BaseDataSetLoadStrategy;

import java.sql.SQLException;

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
    protected void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        DatabaseOperation.UPDATE.execute(dbUnitDatabaseConnection, dataSet);
    }

}
