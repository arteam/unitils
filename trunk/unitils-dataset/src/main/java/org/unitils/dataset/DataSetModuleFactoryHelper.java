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

import org.unitils.dataset.assertstrategy.AssertDataSetStrategy;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;
import org.unitils.dataset.rowsource.InlineDataSetRowSourceFactory;

import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * todo javdoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleFactoryHelper {

    protected DatabaseMetaData databaseMetaData;
    /* The unitils configuration */
    protected Properties configuration;


    /**
     * @param configuration The unitils configuration, not null
     */
    public DataSetModuleFactoryHelper(DatabaseMetaData databaseMetaData, Properties configuration) {
        this.databaseMetaData = databaseMetaData;
        this.configuration = configuration;
    }


    public LoadDataSetStrategy createInsertDataSetStrategy() {
        LoadDataSetStrategy insertDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "insert");
        insertDataSetStrategy.init(configuration, databaseMetaData);
        return insertDataSetStrategy;
    }

    public LoadDataSetStrategy createCleanInsertDataSetStrategy() {
        LoadDataSetStrategy cleanInsertDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "cleaninsert");
        cleanInsertDataSetStrategy.init(configuration, databaseMetaData);
        return cleanInsertDataSetStrategy;
    }

    public LoadDataSetStrategy createRefreshDataSetStrategy() {
        LoadDataSetStrategy refreshDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "refresh");
        refreshDataSetStrategy.init(configuration, databaseMetaData);
        return refreshDataSetStrategy;
    }


    public AssertDataSetStrategy createAssertDataSetStrategy() {
        AssertDataSetStrategy defaultAssertDataSetStrategy = getInstanceOf(AssertDataSetStrategy.class, configuration);
        defaultAssertDataSetStrategy.init(configuration, databaseMetaData);
        return defaultAssertDataSetStrategy;
    }


    public DataSetResolver createDataSetResolver() {
        DataSetResolver dataSetResolver = getInstanceOf(DataSetResolver.class, configuration);
        dataSetResolver.init(configuration);
        return dataSetResolver;
    }


    protected FileDataSetRowSourceFactory createFileDataSetRowSourceFactory() {
        FileDataSetRowSourceFactory xmlDataSetRowSourceFactory = getInstanceOf(FileDataSetRowSourceFactory.class, configuration);
        xmlDataSetRowSourceFactory.init(configuration, databaseMetaData.getSchemaName());
        return xmlDataSetRowSourceFactory;
    }

    protected InlineDataSetRowSourceFactory createInlineDataSetRowSourceFactory() {
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInstanceOf(InlineDataSetRowSourceFactory.class, configuration);
        inlineDataSetRowSourceFactory.init(configuration, databaseMetaData.getSchemaName());
        return inlineDataSetRowSourceFactory;
    }
}