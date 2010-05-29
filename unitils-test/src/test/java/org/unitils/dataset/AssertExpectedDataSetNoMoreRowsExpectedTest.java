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

import static org.junit.Assert.fail;
import static org.unitils.dataset.DataSetAssert.assertExpectedDataSet;
import static org.unitils.dataset.DataSetLoader.cleanInsertDataSetFile;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertExpectedDataSetNoMoreRowsExpectedTest extends DataSetTestBase {


    @Test
    public void noMoreRowsFound() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        assertExpectedDataSet(this, "DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-noMoreRows.xml");
    }

    @Test
    public void moreRowsFound() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-3rows.xml");
            assertExpectedDataSet(this, "DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-moreRowsFound.xml");

        } catch (AssertionError e) {
            assertMessageContains("Expected no more database records in table PUBLIC.TEST but found more records.", e);
            assertMessageContains("Actual database content", e);
            assertMessageContains("   value1  1", e);
            assertMessageContains("-> value2  2", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void rowWithColumnsAfterEmptyRow() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-3rows.xml");
            assertExpectedDataSet(this, "DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-rowWithColumnsAfterEmptyRow.xml");

        } catch (AssertionError e) {
            assertMessageContains("Expected no more database records in table PUBLIC.TEST but found more records.", e);
            assertMessageContains("Actual database content", e);
            assertMessageContains("-> value1  1", e);
            assertMessageContains("-> value2  2", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void twoEmptyRows() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        assertExpectedDataSet(this, "DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-twoEmptyRows.xml");
    }

}