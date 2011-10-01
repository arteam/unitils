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
package org.unitils.dataset.factory;

import org.unitils.dataset.assertstrategy.AssertDataSetStrategyHandler;
import org.unitils.dataset.assertstrategy.InlineAssertDataSetStrategyHandler;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.database.DataSourceWrapperFactory;
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

    protected Map<String, DataSetStrategyFactory> dataSetStrategyFactoriesPerDatabaseName = new HashMap<String, DataSetStrategyFactory>();

    public DataSetStrategyHandlerFactory(Properties configuration, DataSourceWrapperFactory dataSourceWrapperFactory) {
        this.configuration = configuration;
        this.dataSourceWrapperFactory = dataSourceWrapperFactory;
    }


    public LoadDataSetStrategyHandler createLoadDataSetStrategyHandler(String databaseName) {
        DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
        FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getFileDataSetRowSourceFactory();
        DataSetResolver dataSetResolver = getDataSetResolver();
        return new LoadDataSetStrategyHandler(fileDataSetRowSourceFactory, dataSetResolver, dataSetStrategyFactory);
    }

    public InlineLoadDataSetStrategyHandler createInlineLoadDataSetStrategyHandler(String databaseName) {
        DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInlineDataSetRowSourceFactory();
        return new InlineLoadDataSetStrategyHandler(inlineDataSetRowSourceFactory, dataSetStrategyFactory);
    }

    public AssertDataSetStrategyHandler createAssertDataSetStrategyHandler(String databaseName) {
        DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
        FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getFileDataSetRowSourceFactory();
        DataSetResolver dataSetResolver = getDataSetResolver();
        return new AssertDataSetStrategyHandler(fileDataSetRowSourceFactory, dataSetResolver, dataSetStrategyFactory);
    }

    public InlineAssertDataSetStrategyHandler createInlineAssertDataSetStrategyHandler(String databaseName) {
        DataSetStrategyFactory dataSetStrategyFactory = getDataSetStrategyFactory(databaseName);
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInlineDataSetRowSourceFactory();
        return new InlineAssertDataSetStrategyHandler(inlineDataSetRowSourceFactory, dataSetStrategyFactory);
    }


    protected FileDataSetRowSourceFactory getFileDataSetRowSourceFactory() {
        if (fileDataSetRowSourceFactory == null) {
            fileDataSetRowSourceFactory = getInstanceOf(FileDataSetRowSourceFactory.class, configuration);
            fileDataSetRowSourceFactory.init(configuration);
        }
        return fileDataSetRowSourceFactory;
    }

    protected InlineDataSetRowSourceFactory getInlineDataSetRowSourceFactory() {
        if (inlineDataSetRowSourceFactory == null) {
            inlineDataSetRowSourceFactory = getInstanceOf(InlineDataSetRowSourceFactory.class, configuration);
            inlineDataSetRowSourceFactory.init(configuration);
        }
        return inlineDataSetRowSourceFactory;
    }

    protected DataSetResolver getDataSetResolver() {
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