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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.dataset.DataSetLoader.refreshDataSetFile;

/**
 * Test class for loading of data sets using the refresh data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetTest extends DataSetTestBase {

    // todo add more tests

    @Test
    public void tableDoesNotExist() throws Exception {
        try {
            refreshDataSetFile(this, "DataSetModuleDataSetTest-tableDoesNotExist.xml");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Table does not exist: PUBLIC.XXXX"));
        }
    }
}