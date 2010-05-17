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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.InsertDataSetStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;

/**
 * Test class for loading of data sets using the insert data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertDataSetStrategyTest extends DataSetStrategyTestBase {

    /* Tested object */
    protected InsertDataSetStrategy insertDataSetStrategy = new InsertDataSetStrategy();

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        insertDataSetStrategy.init(configuration, createDatabase(configuration));
    }


    @Test
    public void insertDataSet() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, getClass());
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test
    public void literalValues() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-literalValues.xml"), emptyVariables, getClass());

        assertValueInTable("test", "col1", "text");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
        assertValueInTable("test", "col4", "=escaped token");
    }

    @Test
    public void caseSensitive() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-caseSensitive.xml"), emptyVariables, getClass());
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test(expected = UnitilsException.class)
    public void caseSensitiveWrongCase() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-caseSensitiveWrongCase.xml"), emptyVariables, getClass());
        assertValueInTable("test", "col1", "xxxx");
    }

    @Test
    public void literalValuesOverriddenLiteralToken() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-literalValues-overriddenLiteralToken.xml"), emptyVariables, getClass());

        assertValueInTable("test", "col1", "text");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
        assertValueInTable("test", "col4", "%escaped token");
    }

    @Test
    public void variables() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-variables.xml"), asList("value1", "2", "now"), getClass());

        assertValueInTable("test", "col1", "test value1 2");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
    }

    @Test
    public void variablesOverriddenVariableToken() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-variables-overriddenVariableToken.xml"), asList("value1", "2", "now"), getClass());

        assertValueInTable("test", "col1", "test value1 2");
        assertValueInTable("test", "col2", "2");
        assertFalse("No value found for col3", getValues("col3", "test").isEmpty());
    }
}