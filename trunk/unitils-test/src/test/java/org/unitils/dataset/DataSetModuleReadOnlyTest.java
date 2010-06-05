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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.Unitils;
import org.unitils.dataset.core.LoadDataSetStrategy;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

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
public class DataSetModuleReadOnlyTest extends DataSetTestBase {

    @TestedObject
    private DataSetModule dataSetModule = new DataSetModule();

    protected Mock<LoadDataSetStrategy> loadDataSetStrategy;

    protected List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() {
        Properties configuration = Unitils.getInstance().getConfiguration();
        dataSetModule.init(configuration);
        dataSetModule.afterInit();
    }

    @Test
    public void readOnly() throws Exception {
        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void onlyFirstIsReadOnly() throws Exception {
        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, false, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void resetWhenThereIsANotReadOnly() throws Exception {
        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, false, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, false, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
    }


    @Test
    public void multipleFiles() throws Exception {
        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml", "DataSetModuleDataSetTest-2rows.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
        loadDataSetStrategy.assertInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml", "DataSetModuleDataSetTest-2rows.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void partial() throws Exception {
        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml", "DataSetModuleDataSetTest-2rows.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
        loadDataSetStrategy.assertInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void extraFiles() throws Exception {
        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        dataSetModule.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("DataSetModuleDataSetTest-simple.xml", "DataSetModuleDataSetTest-2rows.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

}