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
package org.unitils.dataset.loadstrategy;

import org.unitils.dataset.DataSetModuleFactory;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.dataset.rowsource.InlineDataSetRowSourceFactory;

import java.util.ArrayList;
import java.util.List;

import static org.unitils.util.CollectionUtils.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineLoadDataSetStrategyHandler {

    protected InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory;
    protected DataSetModuleFactory dataSetModuleFactory;


    public InlineLoadDataSetStrategyHandler(InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory, DataSetModuleFactory dataSetModuleFactory) {
        this.inlineDataSetRowSourceFactory = inlineDataSetRowSourceFactory;
        this.dataSetModuleFactory = dataSetModuleFactory;
    }


    public void insertDataSet(String... dataSetRows) {
        LoadDataSetStrategy insertDataSetStrategy = dataSetModuleFactory.createInsertDataSetStrategy();
        performInlineLoadDataSetStrategy(insertDataSetStrategy, asList(dataSetRows));
    }

    public void cleanInsertDataSet(String... dataSetRows) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = dataSetModuleFactory.createCleanInsertDataSetStrategy();
        performInlineLoadDataSetStrategy(cleanInsertDataSetStrategy, asList(dataSetRows));
    }

    public void refreshDataSet(String... dataSetRows) {
        LoadDataSetStrategy refreshDataSetStrategy = dataSetModuleFactory.createRefreshDataSetStrategy();
        performInlineLoadDataSetStrategy(refreshDataSetStrategy, asList(dataSetRows));
    }

    public void updateDataSet(String... dataSetRows) {
        LoadDataSetStrategy updateDataSetStrategy = dataSetModuleFactory.createUpdateDataSetStrategy();
        performInlineLoadDataSetStrategy(updateDataSetStrategy, asList(dataSetRows));
    }


    public void performInlineLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetRows) {
        DataSetRowSource dataSetRowSource = inlineDataSetRowSourceFactory.createDataSetRowSource(dataSetRows);
        loadDataSetStrategy.perform(dataSetRowSource, new ArrayList<String>());
    }

}