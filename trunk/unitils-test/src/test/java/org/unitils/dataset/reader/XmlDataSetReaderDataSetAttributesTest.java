/*
 * Copyright 2009,  Unitils.org
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
package org.unitils.dataset.reader;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.factory.impl.XmlDataSetReader;

import java.io.File;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Tests for reading an xml data set
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetReaderDataSetAttributesTest extends UnitilsJUnit4 {

    /* Tested object */
    private XmlDataSetReader xmlDataSetReader;


    @Before
    public void setUp() throws Exception {
        xmlDataSetReader = new XmlDataSetReader("SCHEMA_A", false, '=', '$');
    }


    @Test
    public void defaultDataSetAttributes() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("LessColumnsFirstDataSet.xml"));

        Schema schema = result.getSchema("SCHEMA_A");
        Table table = schema.getTable("TABLE_A");
        Column column = table.getRow(0).getColumns().get(0);
        assertFalse(schema.isCaseSensitive());
        assertFalse(table.isCaseSensitive());
        assertFalse(column.isCaseSensitive());
        assertEquals('=', column.getLiteralToken());
        assertEquals('$', column.getVariableToken());
        assertTrue(schema.getDeleteTableOrder().isEmpty());
    }

    @Test
    public void overridingDataSetAttributes() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("OverridingAttributesDataSet.xml"));

        Schema schema = result.getSchema("SCHEMA_A");
        Table table = schema.getTable("TABLE_A");
        Column column = table.getRow(0).getColumns().get(0);
        assertTrue(schema.isCaseSensitive());
        assertTrue(table.isCaseSensitive());
        assertTrue(column.isCaseSensitive());
        assertEquals('%', column.getLiteralToken());
        assertEquals(':', column.getVariableToken());
        assertLenientEquals(asList("table1", "table2"), schema.getDeleteTableOrder());
    }

    @Test(expected = UnitilsException.class)
    public void invalidCaseSensitiveValue() throws Exception {
        xmlDataSetReader.readDataSetXml(getDataSetFile("InvalidCaseSensitiveValueDataSet.xml"));
    }

    @Test(expected = UnitilsException.class)
    public void invalidLiteralTokenValue() throws Exception {
        xmlDataSetReader.readDataSetXml(getDataSetFile("InvalidLiteralTokenValueDataSet.xml"));
    }

    @Test(expected = UnitilsException.class)
    public void invalidVariableTokenValue() throws Exception {
        xmlDataSetReader.readDataSetXml(getDataSetFile("InvalidVariableTokenValueDataSet.xml"));
    }

    @Test
    public void emptyDeleteTableOrderValue() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("EmptyDeleteTableOrderValueDataSet.xml"));
        assertTrue(result.getSchema("SCHEMA_A").getDeleteTableOrder().isEmpty());
    }


    private File getDataSetFile(String dataSetFileNames) {
        return toFile(getClass().getResource(dataSetFileNames));
    }

}