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

import org.unitils.dataset.assertstrategy.AssertDataSetStrategyHandler;
import org.unitils.dataset.assertstrategy.InlineAssertDataSetStrategyHandler;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.loadstrategy.InlineLoadDataSetStrategyHandler;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategyHandler;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;
import org.unitils.dataset.rowsource.InlineDataSetRowSourceFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetStrategyHandlerFactory {

    /* The unitils configuration */
    protected Properties configuration;
    protected DataSourceWrapperFactory dataSourceWrapperFactory;

    protected DataSetResolver dataSetResolver;
    protected InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory;
    protected FileDataSetRowSourceFactory fileDataSetRowSourceFactory;

    protected LoadDataSetStrategyHandler loadDataSetStrategyHandler;
    protected InlineLoadDataSetStrategyHandler inlineLoadDataSetStrategyHandler;
    protected AssertDataSetStrategyHandler assertDataSetStrategyHandler;
    protected InlineAssertDataSetStrategyHandler inlineAssertDataSetStrategyHandler;

    protected Map<String, DataSetStrategyFactory> dataSetStrategyFactoriesPerDatabaseName = new HashMap<String, DataSetStrategyFactory>();

    public DataSetStrategyHandlerFactory(Properties configuration, DataSourceWrapperFactory dataSourceWrapperFactory) {
        this.configuration = configuration;
        this.dataSourceWrapperFactory = dataSourceWrapperFactory;
    }


    public LoadDataSetStrategyHandler getLoadDataSetStrategyHandler(String databaseName) {
        if (loadDataSetStrategyHandler == null) {
            DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
            FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getFileDataSetRowSourceFactory();
            DataSetResolver dataSetResolver = getDataSetResolver();
            loadDataSetStrategyHandler = new LoadDataSetStrategyHandler(fileDataSetRowSourceFactory, dataSetResolver, dataSetStrategyFactory);
        }
        return loadDataSetStrategyHandler;
    }

    public InlineLoadDataSetStrategyHandler getInlineLoadDataSetStrategyHandler(String databaseName) {
        if (inlineLoadDataSetStrategyHandler == null) {
            DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
            InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInlineDataSetRowSourceFactory();
            inlineLoadDataSetStrategyHandler = new InlineLoadDataSetStrategyHandler(inlineDataSetRowSourceFactory, dataSetStrategyFactory);
        }
        return inlineLoadDataSetStrategyHandler;
    }

    public AssertDataSetStrategyHandler getAssertDataSetStrategyHandler(String databaseName) {
        if (assertDataSetStrategyHandler == null) {
            DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
            FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getFileDataSetRowSourceFactory();
            DataSetResolver dataSetResolver = getDataSetResolver();
            assertDataSetStrategyHandler = new AssertDataSetStrategyHandler(fileDataSetRowSourceFactory, dataSetResolver, dataSetStrategyFactory);
        }
        return assertDataSetStrategyHandler;
    }

    public InlineAssertDataSetStrategyHandler getInlineAssertDataSetStrategyHandler(String databaseName) {
        if (inlineAssertDataSetStrategyHandler == null) {
            DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
            InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInlineDataSetRowSourceFactory();
            inlineAssertDataSetStrategyHandler = new InlineAssertDataSetStrategyHandler(inlineDataSetRowSourceFactory, dataSetStrategyFactory);
        }
        return inlineAssertDataSetStrategyHandler;
    }


    public FileDataSetRowSourceFactory getFileDataSetRowSourceFactory() {
        if (fileDataSetRowSourceFactory == null) {
            fileDataSetRowSourceFactory = getInstanceOf(FileDataSetRowSourceFactory.class, configuration);
            fileDataSetRowSourceFactory.init(configuration);
        }
        return fileDataSetRowSourceFactory;
    }

    public InlineDataSetRowSourceFactory getInlineDataSetRowSourceFactory() {
        if (inlineDataSetRowSourceFactory == null) {
            inlineDataSetRowSourceFactory = getInstanceOf(InlineDataSetRowSourceFactory.class, configuration);
            inlineDataSetRowSourceFactory.init(configuration);
        }
        return inlineDataSetRowSourceFactory;
    }

    public DataSetResolver getDataSetResolver() {
        if (dataSetResolver == null) {
            dataSetResolver = getInstanceOf(DataSetResolver.class, configuration);
            dataSetResolver.init(configuration);
        }
        return dataSetResolver;
    }

    protected DataSetStrategyFactory getDataSetStrategyFactory(String databaseName) {
        if (isBlank(databaseName)) {
            databaseName = null;
        }
        DataSetStrategyFactory dataSetStrategyFactory = dataSetStrategyFactoriesPerDatabaseName.get(databaseName);
        if (dataSetStrategyFactory == null) {
            DataSourceWrapper dataSourceWrapper = dataSourceWrapperFactory.getDataSourceWrapper(databaseName);
            dataSetStrategyFactory = new DataSetStrategyFactory(configuration, dataSourceWrapper);
            dataSetStrategyFactoriesPerDatabaseName.put(databaseName, dataSetStrategyFactory);
        }
        return dataSetStrategyFactory;
    }
}