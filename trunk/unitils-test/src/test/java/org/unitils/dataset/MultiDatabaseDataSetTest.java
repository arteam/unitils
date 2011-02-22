/*
 * Copyright DbMaintain.org
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

import org.junit.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.unitils.util.CollectionUtils.asList;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiDatabaseDataSetTest extends MultiDbDataSetTestBase {

    DataSetLoader ds1DataSetLoader = new DataSetLoader("unitils1", false);
    DataSetLoader ds2DataSetLoader = new DataSetLoader("unitils2", false);

    @Test
    public void testMultiDatabaseDataSet() {
        ds1DataSetLoader.doInsertDataSetFile(this, asList("DataSetModuleDataSetTest-simple.xml"));
        ds2DataSetLoader.doInsertDataSetFile(this, asList("DataSetModuleDataSetTest-simple.xml"));

        assertValueInTable("test", "col1", "xxxx", dataSource1);
        assertValueInTable("test", "col1", "xxxx", dataSource2);
    }

}
