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
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.factory.DataSetStrategyFactory;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategyHandler;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Test class for loading of data sets using the clean insert data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LoadDataSetStrategyHandlerReadOnlyTest extends UnitilsJUnit4 {

    @TestedObject
    private LoadDataSetStrategyHandler loadDataSetStrategyHandler;

    protected Mock<FileDataSetRowSourceFactory> fileDataSetRowSourceFactory;
    protected Mock<DataSetResolver> dataSetResolver;
    protected Mock<DataSetStrategyFactory> dataSetStrategyFactory;
    protected Mock<LoadDataSetStrategy> loadDataSetStrategy;

    protected List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() {
        loadDataSetStrategyHandler = new LoadDataSetStrategyHandler(fileDataSetRowSourceFactory.getMock(), dataSetResolver.getMock(), dataSetStrategyFactory.getMock());

        dataSetResolver.returns(new File("dataset.xml")).resolve(getClass(), "dataset.xml");
        dataSetResolver.returns(new File("other-dataset.xml")).resolve(getClass(), "other-dataset.xml");
    }

    @Test
    public void readOnly() throws Exception {
        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void onlyFirstIsReadOnly() throws Exception {
        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, false, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void resetWhenThereIsANotReadOnly() throws Exception {
        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, false, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, false, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
    }


    @Test
    public void multipleFiles() throws Exception {
        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml", "other-dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
        loadDataSetStrategy.assertInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml", "other-dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void partial() throws Exception {
        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml", "other-dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
        loadDataSetStrategy.assertInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

    @Test
    public void extraFiles() throws Exception {
        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);

        loadDataSetStrategyHandler.performLoadDataSetStrategy(loadDataSetStrategy.getMock(), asList("dataset.xml", "other-dataset.xml"), emptyVariables, true, getClass());
        loadDataSetStrategy.assertInvoked().perform(null, null);
        loadDataSetStrategy.assertNotInvoked().perform(null, null);
    }

}