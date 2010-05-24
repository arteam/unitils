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

    protected Mock<IdentifierNameProcessor> identifierNameProcessor;
    protected Mock<Database> database;

    private DataSetRow dataSetRow;
    private DataSetRow dataSetRowCaseSensitive;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() {
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        dataSetRowProcessor.init(identifierNameProcessor.getMock(), sqlTypeHandlerRepository, database.getMock());

        database.returns("\"PK\"").quoteIdentifier("PK");
        database.returns("\"pk\"").quoteIdentifier("pk");
        database.returns("PK1").toCorrectCaseIdentifier("pk1");
        database.returns("PK2").toCorrectCaseIdentifier("pk2");
        database.returns("COLUMN").toCorrectCaseIdentifier("column");

        dataSetRow = new DataSetRow("schema", "table", null, false, new DataSetSettings('=', '$', false));
        dataSetRowCaseSensitive = new DataSetRow("schema", "table", null, false, new DataSetSettings('=', '$', true));
    }


    @Test
    public void primaryKeyColumns() throws Exception {
        identifierNameProcessor.returns("schema.table").getQualifiedTableName(null);
        database.returns(asSet("pk1", "pk2")).getPrimaryKeyColumnNames("schema.table");
        dataSetRow.addDataSetColumn(new DataSetColumn("pk1", "value"));
        dataSetRow.addDataSetColumn(new DataSetColumn("pk2", "value"));
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));

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
        database.returns(asSet("pk1", "pk2", "pk3")).getPrimaryKeyColumnNames("schema.table");
        dataSetRow.addDataSetColumn(new DataSetColumn("Pk1", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        Row result = dataSetRowProcessor.process(dataSetRow, emptyVariables, unusedPrimaryKeys);

        assertReflectionEquals(asSet("pk2", "pk3"), unusedPrimaryKeys);
        Value value = result.getValues().get(0);
        assertTrue(value.getColumn().isPrimaryKey());
    }

    @Test
    public void caseSensitive() throws Exception {
        identifierNameProcessor.returns("\"schema\".\"table\"").getQualifiedTableName(null);
        database.returns(asSet("pk")).getPrimaryKeyColumnNames("\"schema\".\"table\"");
        dataSetRowCaseSensitive.addDataSetColumn(new DataSetColumn("PK", "value"));
        dataSetRowCaseSensitive.addDataSetColumn(new DataSetColumn("pk", "value"));

        Set<String> unusedPrimaryKeys = new HashSet<String>();
        Row result = dataSetRowProcessor.process(dataSetRowCaseSensitive, emptyVariables, unusedPrimaryKeys);

        Value value1 = result.getValues().get(0);
        Value value2 = result.getValues().get(1);
        assertFalse(value1.getColumn().isPrimaryKey());
        assertTrue(value2.getColumn().isPrimaryKey());
    }

}