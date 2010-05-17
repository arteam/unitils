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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.*;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetLoaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private RefreshDataSeLoader refreshRowLoader = new RefreshDataSeLoader();

    private Mock<DataSetRowSource> dataSetRowSource;
    private Mock<DataSetRowProcessor> dataSetRowProcessor;
    private Mock<DatabaseAccessor> databaseAccessor;

    private List<String> emptyVariables = new ArrayList<String>();

    private DataSetRow dataSetRow;
    private DatabaseRow databaseRowPrimaryKey;


    @Before
    public void initialize() throws Exception {
        refreshRowLoader.init(dataSetRowProcessor.getMock(), databaseAccessor.getMock());

        dataSetRow = createDataSetRow();
        databaseRowPrimaryKey = createDatabaseRow(true);

        dataSetRowSource.onceReturns(dataSetRow).getNextDataSetRow();
        dataSetRowProcessor.returns(databaseRowPrimaryKey).process(null, null, null);
    }


    @Test
    public void rowNotYetInDatabase() throws Exception {
        databaseAccessor.returns(0).executeUpdate(null, null);

        refreshRowLoader.load(dataSetRowSource.getMock(), emptyVariables);

        databaseAccessor.assertInvoked().executeUpdate("update schema.table set column=? where column=?", null);
        databaseAccessor.assertInvoked().executeUpdate("insert into schema.table (column) values (?)", null);
    }

    @Test
    public void rowAlreadyInDatabase() throws Exception {
        databaseAccessor.returns(1).executeUpdate(null, null);

        refreshRowLoader.load(dataSetRowSource.getMock(), emptyVariables);

        databaseAccessor.assertInvoked().executeUpdate("update schema.table set column=? where column=?", null);
        databaseAccessor.assertNotInvoked().executeUpdate("insert into schema.table (column) values (?)", null);
    }


    private DataSetRow createDataSetRow() {
        DataSetRow dataSetRow = new DataSetRow("schema", "table", null, false, null);
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));
        return dataSetRow;
    }

    private DatabaseRow createDatabaseRow(boolean primaryKey) {
        DatabaseRow databaseRow = new DatabaseRow("schema.table");
        databaseRow.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column", "value", 0, null, false, primaryKey));
        return databaseRow;
    }
}