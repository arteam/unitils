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
package org.unitils.dataset;

import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;

import static org.junit.Assert.*;
import static org.unitils.dataset.DataSetLoader.insertDataSetFile;

/**
 * Test class for loading of data sets using the insert data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Transactional(TransactionMode.COMMIT)
public class InsertDataSetTest extends OneDbDataSetTestBase {


    @Test
    public void insertDataSet() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-simple.xml");
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test
    public void literalValues() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-literalValues.xml");

        assertValueInTable("test", "col1", "text");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
        assertValueInTable("test", "col4", "=escaped token");
    }

    @Test
    public void caseSensitive() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-caseSensitive.xml");
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test(expected = UnitilsException.class)
    public void caseSensitiveWrongCase() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-caseSensitiveWrongCase.xml");
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test
    public void literalValuesOverriddenLiteralToken() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-literalValues-overriddenLiteralToken.xml");

        assertValueInTable("test", "col1", "text");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
        assertValueInTable("test", "col4", "%escaped token");
    }

    @Test
    public void variables() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-variables.xml", "value1", "2", "now");

        assertValueInTable("test", "col1", "test value1 2");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
    }

    @Test
    public void variablesOverriddenVariableToken() throws Exception {
        insertDataSetFile(this, "DataSetModuleDataSetTest-variables-overriddenVariableToken.xml", "value1", "2", "now");

        assertValueInTable("test", "col1", "test value1 2");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
    }

    @Test
    public void tableDoesNotExist() throws Exception {
        try {
            insertDataSetFile(this, "DataSetModuleDataSetTest-tableDoesNotExist.xml");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Table does not exist: PUBLIC.XXXX"));
        }
    }
}