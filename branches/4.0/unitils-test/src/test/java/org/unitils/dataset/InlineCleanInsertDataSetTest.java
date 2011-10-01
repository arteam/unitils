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

import static org.unitils.dataset.DataSetLoader.cleanInsertDataSet;

/**
 * Test class for loading of data sets using the clean insert data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineCleanInsertDataSetTest extends OneDbDataSetTestBase {


    @Test
    public void inlineCleanInsertDataSet() throws Exception {
        insertValueInTableTest("yyyy");

        cleanInsertDataSet("TEST col1=xxxx");
        assertValueInTable("test", "col1", "xxxx");
        assertValueNotInTable("test", "col1", "yyyy");
    }

    @Test
    public void correctOrderForDependentTables() throws Exception {
        insertValueInTableTest("yyyy");
        insertValueInTableDependent("yyyy");

        cleanInsertDataSet("TEST col1=xxxx", "DEPENDENT col1=xxxx");
        assertValueInTable("test", "col1", "xxxx");
        assertValueInTable("dependent", "col1", "xxxx");
        assertValueNotInTable("test", "col1", "yyyy");
        assertValueNotInTable("dependent", "col1", "yyyy");
    }

    @Test(expected = UnitilsException.class)
    public void incorrectOrderForDependentTables() throws Exception {
        insertValueInTableTest("yyyy");
        insertValueInTableDependent("yyyy");

        cleanInsertDataSet("DEPENDENT col1=xxxx", "TEST col1=xxxx");
    }

    @Test
    public void literalValues() throws Exception {
        cleanInsertDataSet("TEST col1==1, col2='=2', col3==sysdate");

        assertValueInTable("test", "col1", "1");
        assertValueInTable("test", "col2", "2");
    }

}