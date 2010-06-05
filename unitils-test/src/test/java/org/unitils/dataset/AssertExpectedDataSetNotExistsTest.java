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
import static org.unitils.dataset.DataSetLoader.cleanInsertDataSetFile;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertExpectedDataSetNotExistsTest extends DataSetTestBase {


    @Test
    public void rowNotFound() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataNotExistsTest-rowNotFound.xml");
    }

    @Test
    public void rowFound() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
            DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataNotExistsTest-rowFound.xml");
        } catch (AssertionError e) {
            e.printStackTrace();
            assertMessageContains("Expected not to find a match for data set row: PUBLIC.TEST [COL1=value1, COL2=1]", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void emptyRowInNotExists() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataNotExistsTest-emptyRowInNotExists.xml");
    }

}