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
package org.unitils.dataset.assertstrategy;

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
public class InlineAssertDataSetStrategyHandler {

    protected InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory;
    protected DataSetModuleFactory dataSetModuleFactory;


    public InlineAssertDataSetStrategyHandler(InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory, DataSetModuleFactory dataSetModuleFactory) {
        this.inlineDataSetRowSourceFactory = inlineDataSetRowSourceFactory;
        this.dataSetModuleFactory = dataSetModuleFactory;
    }


    public void assertExpectedDataSet(boolean logDatabaseContentOnAssertionError, String... dataSetRows) {
        AssertDataSetStrategy defaultAssertDataSetStrategy = dataSetModuleFactory.createAssertDataSetStrategy();
        performInlineAssertDataSetStrategy(defaultAssertDataSetStrategy, asList(dataSetRows), logDatabaseContentOnAssertionError);
    }


    public void performInlineAssertDataSetStrategy(AssertDataSetStrategy assertDataSetStrategy, List<String> dataSetRows, boolean logDatabaseContentOnAssertionError) {
        DataSetRowSource dataSetRowSource = inlineDataSetRowSourceFactory.createDataSetRowSource(dataSetRows);
        assertDataSetStrategy.perform(dataSetRowSource, new ArrayList<String>(), logDatabaseContentOnAssertionError);
    }

}