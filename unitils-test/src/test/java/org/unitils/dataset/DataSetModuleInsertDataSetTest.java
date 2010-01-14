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
import static org.junit.Assert.assertFalse;

/**
 * Test class for loading of data sets using the {@link DataSetModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleInsertDataSetTest extends DataSetModuleDataSetTestBase {

    @Test
    public void insertDataSet() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleDataSetTest-simple.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test
    public void literalValues() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleDataSetTest-literalValues.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        assertValueInTable("test", "col1", "text");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
        assertValueInTable("test", "col4", "=escaped token");
    }

    @Test
    public void literalValuesOverriddenLiteralToken() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleDataSetTest-literalValues-overriddenLiteralToken.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader.class);
        assertValueInTable("test", "col1", "text");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
        assertValueInTable("test", "col4", "%escaped token");
    }

    @Test
    public void variables() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleDataSetTest-variables.xml"), asList("value1", "2", "now"), getClass(), InsertDataSetLoader.class);
        assertValueInTable("test", "col1", "test value1 2");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
    }

    @Test
    public void variablesOverriddenVariableToken() throws Exception {
        dataSetModule.loadDataSet(asList("DataSetModuleDataSetTest-variables-overriddenVariableToken.xml"), asList("value1", "2", "now"), getClass(), InsertDataSetLoader.class);
        assertValueInTable("test", "col1", "test value1 2");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
    }
}