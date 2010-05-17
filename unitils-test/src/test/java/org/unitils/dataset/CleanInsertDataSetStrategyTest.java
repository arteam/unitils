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
import org.unitils.dataset.core.CleanInsertDataSetStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

/**
 * Test class for loading of data sets using the clean insert data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertDataSetStrategyTest extends DataSetStrategyTestBase {

    /* Tested object */
    protected CleanInsertDataSetStrategy cleanInsertDataSetStrategy;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        cleanInsertDataSetStrategy = new CleanInsertDataSetStrategy();
        cleanInsertDataSetStrategy.init(configuration, createDatabase(configuration));
    }


    @Test
    public void cleanInsertDataSet() throws Exception {
        insertValueInTableTest("yyyy");

        cleanInsertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, getClass());
        assertValueInTable("test", "col1", "xxxx");
        assertValueNotInTable("test", "col1", "yyyy");
    }

    @Test
    public void correctOrderForDependentTables() throws Exception {
        insertValueInTableTest("yyyy");
        insertValueInTableDependent("yyyy");

        cleanInsertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-dependency.xml"), emptyVariables, getClass());
        assertValueInTable("test", "col1", "xxxx");
        assertValueInTable("dependent", "col1", "xxxx");
        assertValueNotInTable("test", "col1", "yyyy");
        assertValueNotInTable("dependent", "col1", "yyyy");
    }

    @Test(expected = UnitilsException.class)
    public void incorrectOrderForDependentTables() throws Exception {
        insertValueInTableTest("yyyy");
        insertValueInTableDependent("yyyy");

        cleanInsertDataSetStrategy.perform(asList("DataSetModuleDataSetTest-dependencyWrongOrder.xml"), emptyVariables, getClass());
    }

}