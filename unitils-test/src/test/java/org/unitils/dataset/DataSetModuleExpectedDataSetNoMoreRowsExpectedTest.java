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
package org.unitils.dataset;

import org.junit.Test;
import org.unitils.dataset.loader.impl.InsertDataSetLoader;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleExpectedDataSetNoMoreRowsExpectedTest extends DataSetModuleDataSetTestBase {


    @Test
    public void noMoreRowsFound() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-noMoreRows.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test
    public void moreRowsFound() throws Exception {
        try {
            dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-moreRowsFound.xml"), new ArrayList<String>(), getClass(), true);
        } catch (AssertionError e) {
            assertMessageContains("Expected no more database records in table PUBLIC.TEST but found more records.", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void rowWithColumnsAfterEmptyRow() throws Exception {
        try {
            dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-rowWithColumnsAfterEmptyRow.xml"), new ArrayList<String>(), getClass(), true);
        } catch (AssertionError e) {
            assertMessageContains("Found differences for table PUBLIC.TEST", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void twoEmptyRows() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetNoMoreRowsExpectedTest-twoEmptyRows.xml"), new ArrayList<String>(), getClass(), true);
    }

}