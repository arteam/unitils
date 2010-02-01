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
public class DataSetModuleExpectedDataNotExistsTest extends DataSetModuleDataSetTestBase {


    @Test
    public void rowNotFound() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataNotExistsTest-rowNotFound.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test
    public void rowFound() throws Exception {
        try {
            dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataNotExistsTest-rowFound.xml"), new ArrayList<String>(), getClass(), true);
        } catch (AssertionError e) {
            assertMessageContains("Expected not to find a match for data set row: col1=\"value1\", col2=\"1\"", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void emptyRowInNotExists() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataNotExistsTest-emptyRowInNotExists.xml"), new ArrayList<String>(), getClass(), true);
    }

}