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
import org.unitils.dataset.sqltypehandler.impl.TextSqlTypeHandler;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Tests the variable and literal value processing
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetRowProcessorVariableLiteralTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetRowProcessor dataSetRowProcessor = new DataSetRowProcessor();

    protected Mock<IdentifierNameProcessor> identifierNameProcessor;
    protected Mock<SqlTypeHandlerRepository> sqlTypeHandlerRepository;
    protected Mock<Database> database;

    private DataSetRow dataSetRow;

    private List<String> emptyVariables = new ArrayList<String>();
    private Set<String> emptyPrimaryKeys = new HashSet<String>();


    @Before
    public void initialize() {
        sqlTypeHandlerRepository.returns(new TextSqlTypeHandler()).getSqlTypeHandler(0);

        dataSetRowProcessor.init(identifierNameProcessor.getMock(), sqlTypeHandlerRepository.getMock(), database.getMock());

        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        dataSetRow = new DataSetRow("schema", "table", null, false, dataSetSettings);
    }


    @Test
    public void regularValue() throws Exception {
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, emptyVariables, emptyPrimaryKeys);

        Value databaseColumn = result.getDatabaseColumnsWithValue().get(0);
        assertEquals("value", databaseColumn.getValue());
        assertFalse(databaseColumn.isLiteralValue());
    }

    @Test
    public void literalValue() throws Exception {
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "=value"));
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, emptyVariables, emptyPrimaryKeys);

        Value databaseColumn = result.getDatabaseColumnsWithValue().get(0);
        assertEquals("value", databaseColumn.getValue());
        assertTrue(databaseColumn.isLiteralValue());
    }

    @Test
    public void escapedLiteralValue() throws Exception {
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "==value"));
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, emptyVariables, emptyPrimaryKeys);

        Value databaseColumn = result.getDatabaseColumnsWithValue().get(0);
        assertEquals("=value", databaseColumn.getValue());
        assertFalse(databaseColumn.isLiteralValue());
    }

    @Test
    public void variables() throws Exception {
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "$0value $0 $1"));
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, asList("1", "2"), emptyPrimaryKeys);

        Value databaseColumn = result.getDatabaseColumnsWithValue().get(0);
        assertEquals("1value 1 2", databaseColumn.getValue());
        assertFalse(databaseColumn.isLiteralValue());
    }

    @Test
    public void literalValueThroughVariable() throws Exception {
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "$0"));
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, asList("=value"), emptyPrimaryKeys);

        Value databaseColumn = result.getDatabaseColumnsWithValue().get(0);
        assertEquals("value", databaseColumn.getValue());
        assertTrue(databaseColumn.isLiteralValue());
    }

    @Test
    public void escapedLiteralValueThroughVariable() throws Exception {
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "$0"));
        DatabaseRow result = dataSetRowProcessor.process(dataSetRow, asList("==value"), emptyPrimaryKeys);

        Value databaseColumn = result.getDatabaseColumnsWithValue().get(0);
        assertEquals("=value", databaseColumn.getValue());
        assertFalse(databaseColumn.isLiteralValue());
    }

}
