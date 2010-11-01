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
package org.unitils.dataset.model;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.loadstrategy.impl.IdentifierNameProcessor;
import org.unitils.dataset.model.database.Row;
import org.unitils.dataset.model.database.Value;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.mock.Mock;

import java.sql.SQLException;
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
    private DataSetRowProcessor dataSetRowProcessor;

    protected Mock<IdentifierNameProcessor> identifierNameProcessor;
    protected Mock<DatabaseMetaData> databaseMetaData;

    private DataSetRow dataSetRow;
    private DataSetRow dataSetRowCaseSensitive;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws SQLException {
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        dataSetRowProcessor = new DataSetRowProcessor(identifierNameProcessor.getMock(), sqlTypeHandlerRepository, databaseMetaData.getMock());

        identifierNameProcessor.returns("\"PK\"").getCorrectCaseColumnName("PK", null);
        identifierNameProcessor.returns("\"pk\"").getCorrectCaseColumnName("pk", null);
        identifierNameProcessor.returns("PK1").getCorrectCaseColumnName("pk1", null);
        identifierNameProcessor.returns("PK2").getCorrectCaseColumnName("pk2", null);
        identifierNameProcessor.returns("COLUMN").getCorrectCaseColumnName("column", null);
        databaseMetaData.returns("PK").removeIdentifierQuotes("\"PK\"");
        databaseMetaData.returns("pk").removeIdentifierQuotes("\"pk\"");
        databaseMetaData.returns("PK1").removeIdentifierQuotes("PK1");
        databaseMetaData.returns("PK2").removeIdentifierQuotes("PK2");
        databaseMetaData.returns("COLUMN").removeIdentifierQuotes("COLUMN");
        databaseMetaData.returns("schema").getSchemaName("\"schema\".\"table\"");
        databaseMetaData.returns("schema").getSchemaName("schema.table");
        databaseMetaData.returns("table").getTableName("schema.table");
        databaseMetaData.returns("table").getTableName("\"schema\".\"table\"");

        dataSetRow = new DataSetRow("schema", "table", null, false, new DataSetSettings('=', '$', false));
        dataSetRowCaseSensitive = new DataSetRow("schema", "table", null, false, new DataSetSettings('=', '$', true));
    }


    @Test
    public void primaryKeyColumns() throws Exception {
        identifierNameProcessor.returns("schema.table").getQualifiedTableName(null);
        databaseMetaData.returns(asSet("PK1", "PK2")).getPrimaryKeyColumnNames("schema.table");
        dataSetRow.addDataSetValue(new DataSetValue("pk1", "value"));
        dataSetRow.addDataSetValue(new DataSetValue("pk2", "value"));
        dataSetRow.addDataSetValue(new DataSetValue("column", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        Row result = dataSetRowProcessor.process(dataSetRow, emptyVariables, unusedPrimaryKeys);

        assertTrue(unusedPrimaryKeys.isEmpty());
        Value value1 = result.getValues().get(0);
        Value value2 = result.getValues().get(1);
        Value value3 = result.getValues().get(2);
        assertTrue(value1.getColumn().isPrimaryKey());
        assertTrue(value2.getColumn().isPrimaryKey());
        assertFalse(value3.getColumn().isPrimaryKey());
    }

    @Test
    public void unusedPrimaryKeyColumns() throws Exception {
        identifierNameProcessor.returns("schema.table").getQualifiedTableName(null);
        databaseMetaData.returns(asSet("PK1", "PK2", "PK3")).getPrimaryKeyColumnNames("schema.table");
        dataSetRow.addDataSetValue(new DataSetValue("pk1", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        Row result = dataSetRowProcessor.process(dataSetRow, emptyVariables, unusedPrimaryKeys);

        assertReflectionEquals(asSet("PK2", "PK3"), unusedPrimaryKeys);
        Value value = result.getValues().get(0);
        assertTrue(value.getColumn().isPrimaryKey());
    }

    @Test
    public void caseSensitive() throws Exception {
        identifierNameProcessor.returns("\"schema\".\"table\"").getQualifiedTableName(null);
        databaseMetaData.returns(asSet("pk")).getPrimaryKeyColumnNames("\"schema\".\"table\"");
        dataSetRowCaseSensitive.addDataSetValue(new DataSetValue("PK", "value"));
        dataSetRowCaseSensitive.addDataSetValue(new DataSetValue("pk", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        Row result = dataSetRowProcessor.process(dataSetRowCaseSensitive, emptyVariables, unusedPrimaryKeys);

        Value value1 = result.getValues().get(0);
        Value value2 = result.getValues().get(1);
        assertFalse(value1.getColumn().isPrimaryKey());
        assertTrue(value2.getColumn().isPrimaryKey());
    }

}