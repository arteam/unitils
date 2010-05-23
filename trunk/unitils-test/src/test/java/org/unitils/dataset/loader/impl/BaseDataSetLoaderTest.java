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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.*;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.util.AssertionUtils.assertExceptionMessageContains;

/**
 * Tests for creating using insert statements for loading data rows
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class BaseDataSetLoaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private TestBaseDataSetLoader baseDataSetLoader = new TestBaseDataSetLoader();

    protected Mock<DataSetRowSource> dataSetRowSource;
    protected Mock<DataSetRowProcessor> dataSetRowProcessor;

    private List<String> emptyVariables = new ArrayList<String>();

    private DataSetRow dataSetRow1;
    private DataSetRow dataSetRow2;
    private DatabaseRow databaseRow1;
    private DatabaseRow databaseRow2;

    private DataSetRow emptyDataSetRow;
    private DatabaseRow emptyDatabaseRow;

    @Before
    public void initialize() throws Exception {
        baseDataSetLoader.init(dataSetRowProcessor.getMock(), null);

        dataSetRow1 = createDataSetRow();
        dataSetRow2 = createDataSetRow();
        databaseRow1 = createDatabaseRow();
        databaseRow2 = createDatabaseRow();

        emptyDataSetRow = createDataSetRowWithoutColumns();
        emptyDatabaseRow = createDatabaseRowWithoutColumns();
    }


    @Test
    public void loadDatabaseRow() throws Exception {
        dataSetRowSource.onceReturns(dataSetRow1).getNextDataSetRow();
        dataSetRowSource.onceReturns(dataSetRow2).getNextDataSetRow();
        dataSetRowProcessor.returns(databaseRow1).process(dataSetRow1, null, null);
        dataSetRowProcessor.returns(databaseRow2).process(dataSetRow2, null, null);

        baseDataSetLoader.load(dataSetRowSource.getMock(), emptyVariables);
        assertReflectionEquals(asList(databaseRow1, databaseRow2), baseDataSetLoader.getLoadedDatabaseRows());
    }

    @Test
    public void emptyDatabaseRow() throws Exception {
        dataSetRowProcessor.returns(emptyDatabaseRow).process(emptyDataSetRow, null, null);
        dataSetRowSource.onceReturns(emptyDataSetRow).getNextDataSetRow();

        baseDataSetLoader.load(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(baseDataSetLoader.getLoadedDatabaseRows().isEmpty());
    }

    @Test
    public void variables() throws Exception {
        dataSetRowSource.onceReturns(dataSetRow1).getNextDataSetRow();
        dataSetRowProcessor.returns(databaseRow1).process(null, null, null);

        baseDataSetLoader.load(dataSetRowSource.getMock(), asList("1", "2"));
        dataSetRowProcessor.assertInvoked().process(dataSetRow1, asList("1", "2"), null);
    }

    @Test
    public void exceptionDuringLoadingOfRow() throws Exception {
        dataSetRowProcessor.raises(RuntimeException.class).process(null, null, null);
        dataSetRowSource.onceReturns(dataSetRow1).getNextDataSetRow();

        try {
            baseDataSetLoader.load(dataSetRowSource.getMock(), asList("1", "2"));
            fail("Exception expected");
        } catch (Exception e) {
            assertExceptionMessageContains(e, "schema.table");
            assertExceptionMessageContains(e, "column=\"value\"");
            assertExceptionMessageContains(e, "1, 2");
        }
    }


    private DataSetRow createDataSetRow() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        DataSetRow dataSetRow = new DataSetRow("schema", "table", null, false, dataSetSettings);
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));
        return dataSetRow;
    }

    private DatabaseRow createDatabaseRow() {
        DatabaseRow databaseRow = new DatabaseRow("schema.table");
        databaseRow.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column", "value", VARCHAR, false, false));
        return databaseRow;
    }

    private DataSetRow createDataSetRowWithoutColumns() {
        return new DataSetRow("schema", "table", null, false, null);
    }

    private DatabaseRow createDatabaseRowWithoutColumns() {
        return new DatabaseRow("schema.table");
    }


    private static class TestBaseDataSetLoader extends BaseDataSetLoader {

        private List<DatabaseRow> loadedDatabaseRows = new ArrayList<DatabaseRow>();

        @Override
        protected int loadDatabaseRow(DatabaseRow databaseRow) throws Exception {
            loadedDatabaseRows.add(databaseRow);
            return 1;
        }

        public List<DatabaseRow> getLoadedDatabaseRows() {
            return loadedDatabaseRows;
        }
    }
}