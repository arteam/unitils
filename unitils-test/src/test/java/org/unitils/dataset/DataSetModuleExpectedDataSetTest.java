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
import org.unitils.core.UnitilsException;
import org.unitils.dataset.loader.impl.InsertDataSetLoader;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleExpectedDataSetTest extends DataSetModuleDataSetTestBase {


    @Test
    public void matchingDataSet() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test
    public void differentDataSet() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        try {
            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-different.xml"), new ArrayList<String>(), getClass(), true);
        } catch (AssertionError e) {
            assertMessageContains("Found differences for table PUBLIC.TEST", e);
            assertMessageContains("Different database record found for data set row:  col1=\"xxxx\", col2=\"9999\"", e);
            assertMessageContains("value1", e);
            assertMessageContains("xxxx", e);
            assertMessageContains("1", e);
            assertMessageContains("9999", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void literalValues() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-literalValues.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-literalValues.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test
    public void caseSensitive() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-caseSensitive.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test(expected = UnitilsException.class)
    public void caseSensitiveWrongCase() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-caseSensitiveWrongCase.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test
    public void equalVariables() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-variables.xml"), asList("test", "1"), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-variables.xml"), asList("test", "1"), getClass(), true);
    }

    @Test
    public void differentVariables() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-variables.xml"), asList("test", "1"), getClass(), InsertDataSetLoader.class);
        try {
            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-variables.xml"), asList("xxxx", "9999"), getClass(), true);
        } catch (AssertionError e) {
            assertMessageContains("test", e);
            assertMessageContains("xxxx", e);
            assertMessageContains("1", e);
            assertMessageContains("9999", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void noDatabaseContentLogging() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        try {
            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-different.xml"), new ArrayList<String>(), getClass(), false);
        } catch (AssertionError e) {
            assertMessageNotContains("Actual Database Content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void emptyDataSet() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetTest-emptyDataSet.xml"), new ArrayList<String>(), getClass(), true);
    }

}