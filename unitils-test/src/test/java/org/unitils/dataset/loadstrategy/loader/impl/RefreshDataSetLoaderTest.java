/*
 * Copyright Unitils.org
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
package org.unitils.dataset.loadstrategy.loader.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.database.Row;
import org.unitils.dataset.model.database.Value;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.unitils.dataset.util.DataSetTestUtils.createRow;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetLoaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private RefreshDataSetLoader refreshRowLoader = new RefreshDataSetLoader();

    protected Mock<DataSetRowSource> dataSetRowSource;
    protected Mock<DataSetRowProcessor> dataSetRowProcessor;
    protected Mock<DatabaseAccessor> databaseAccessor;

    private List<String> emptyVariables = new ArrayList<String>();

    private DataSetRow dataSetRow;
    private Row rowPrimaryKey;


    @Before
    public void initialize() throws Exception {
        refreshRowLoader.init(dataSetRowProcessor.getMock(), databaseAccessor.getMock());

        dataSetRow = createDataSetRow();
        rowPrimaryKey = createRow(new Value("value", false, new Column("column", 0, true)));

        dataSetRowSource.onceReturns(dataSetRow).getNextDataSetRow();
        dataSetRowProcessor.returns(rowPrimaryKey).process(null, null, null);
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
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false, null);
        DataSetRow dataSetRow = new DataSetRow("schema", "table", null, false, dataSetSettings);
        dataSetRow.addDataSetValue(new DataSetValue("column", "value"));
        return dataSetRow;
    }
}