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
package org.unitils.dataset.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.IdentifierNameProcessor;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * Tests the primary key handling of the data set row processor
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetRowProcessorPrimaryKeyTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetRowProcessor dataSetRowProcessor = new DataSetRowProcessor();

    private Mock<IdentifierNameProcessor> identifierNameProcessor;
    private Mock<SqlTypeHandlerRepository> sqlTypeHandlerRepository;
    private Mock<Database> database;

    private DataSetRow dataSetRow;
    private DataSetRow dataSetRowCaseSensitive;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() {
        dataSetRowProcessor.init(identifierNameProcessor.getMock(), sqlTypeHandlerRepository.getMock(), database.getMock());

        dataSetRow = new DataSetRow("schema", "table", null, false, new DataSetSettings('=', '$', false));
        dataSetRowCaseSensitive = new DataSetRow("schema", "table", null, false, new DataSetSettings('=', '$', true));
    }


    @Test
    public void primaryKeyColumn() throws Exception {
        database.returns(asSet("pk")).getPrimaryKeyColumnNames("schema", "table", false);
        dataSetRow.addDataSetColumn(new DataSetColumn("PK", "value"));
        dataSetRow.addDataSetColumn(new DataSetColumn("pk", "value"));
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, emptyVariables, unusedPrimaryKeys);

        assertTrue(unusedPrimaryKeys.isEmpty());
        DatabaseColumnWithValue databaseColumn1 = result.getDatabaseColumnsWithValue().get(0);
        DatabaseColumnWithValue databaseColumn2 = result.getDatabaseColumnsWithValue().get(1);
        DatabaseColumnWithValue databaseColumn3 = result.getDatabaseColumnsWithValue().get(2);
        assertTrue(databaseColumn1.isPrimaryKey());
        assertTrue(databaseColumn2.isPrimaryKey());
        assertFalse(databaseColumn3.isPrimaryKey());
    }

    @Test
    public void unusedPrimaryKeyColumns() throws Exception {
        database.returns(asSet("pk1", "pk2", "pk3")).getPrimaryKeyColumnNames("schema", "table", false);
        dataSetRow.addDataSetColumn(new DataSetColumn("Pk1", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, emptyVariables, unusedPrimaryKeys);

        assertReflectionEquals(asSet("pk2", "pk3"), unusedPrimaryKeys);
        DatabaseColumnWithValue databaseColumn1 = result.getDatabaseColumnsWithValue().get(0);
        assertTrue(databaseColumn1.isPrimaryKey());
    }

    @Test
    public void caseSensitive() throws Exception {
        database.returns(asSet("pk")).getPrimaryKeyColumnNames("schema", "table", true);
        dataSetRowCaseSensitive.addDataSetColumn(new DataSetColumn("PK", "value"));
        dataSetRowCaseSensitive.addDataSetColumn(new DataSetColumn("pk", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        DatabaseRow result = dataSetRowProcessor.process(dataSetRowCaseSensitive, emptyVariables, unusedPrimaryKeys);

        DatabaseColumnWithValue databaseColumn1 = result.getDatabaseColumnsWithValue().get(0);
        DatabaseColumnWithValue databaseColumn2 = result.getDatabaseColumnsWithValue().get(1);
        assertFalse(databaseColumn1.isPrimaryKey());
        assertTrue(databaseColumn2.isPrimaryKey());
    }

}