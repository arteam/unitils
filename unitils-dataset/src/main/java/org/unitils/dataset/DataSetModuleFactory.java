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

import org.dbmaintain.database.IdentifierProcessor;
import org.unitils.dataset.assertstrategy.*;
import org.unitils.dataset.assertstrategy.impl.TableContentRetriever;
import org.unitils.dataset.database.DataSetDatabaseHelper;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.loadstrategy.InlineLoadDataSetStrategyHandler;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategyHandler;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;
import org.unitils.dataset.rowsource.InlineDataSetRowSourceFactory;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.dataset.structure.DataSetStructureGenerator;

import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * Helper class for constructing parts of the data set module.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleFactory {

    /**
     * Property key for the xsd target directory
     */
    public static final String PROPKEY_XSD_TARGETDIRNAME = "dataset.xsd.targetDirName";

    /* The unitils configuration */
    protected Properties configuration;

    protected DataSourceWrapper dataSourceWrapper;
    protected IdentifierProcessor identifierProcessor;

    protected InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory;
    protected FileDataSetRowSourceFactory fileDataSetRowSourceFactory;

    protected DataSetResolver dataSetResolver;

    protected LoadDataSetStrategyHandler loadDataSetStrategyHandler;
    protected InlineLoadDataSetStrategyHandler inlineLoadDataSetStrategyHandler;
    protected AssertDataSetStrategyHandler assertDataSetStrategyHandler;
    protected InlineAssertDataSetStrategyHandler inlineAssertDataSetStrategyHandler;
    protected DataSetStructureGenerator dataSetStructureGenerator;


    public DataSetModuleFactory(Properties configuration, DataSourceWrapper dataSourceWrapper, IdentifierProcessor identifierProcessor) {
        this.configuration = configuration;
        this.dataSourceWrapper = dataSourceWrapper;
        this.identifierProcessor = identifierProcessor;

    }


    public LoadDataSetStrategyHandler getLoadDataSetStrategyHandler() {
        if (loadDataSetStrategyHandler == null) {
            FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getFileDataSetRowSourceFactory();
            DataSetResolver dataSetResolver = getDataSetResolver();
            loadDataSetStrategyHandler = new LoadDataSetStrategyHandler(fileDataSetRowSourceFactory, dataSetResolver, this);
        }
        return loadDataSetStrategyHandler;
    }

    public InlineLoadDataSetStrategyHandler getInlineLoadDataSetStrategyHandler() {
        if (inlineLoadDataSetStrategyHandler == null) {
            InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInlineDataSetRowSourceFactory();
            inlineLoadDataSetStrategyHandler = new InlineLoadDataSetStrategyHandler(inlineDataSetRowSourceFactory, this);
        }
        return inlineLoadDataSetStrategyHandler;
    }

    public AssertDataSetStrategyHandler getAssertDataSetStrategyHandler() {
        if (assertDataSetStrategyHandler == null) {
            FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getFileDataSetRowSourceFactory();
            DataSetResolver dataSetResolver = getDataSetResolver();
            assertDataSetStrategyHandler = new AssertDataSetStrategyHandler(fileDataSetRowSourceFactory, dataSetResolver, this);
        }
        return assertDataSetStrategyHandler;
    }

    public InlineAssertDataSetStrategyHandler getInlineAssertDataSetStrategyHandler() {
        if (inlineAssertDataSetStrategyHandler == null) {
            InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInlineDataSetRowSourceFactory();
            inlineAssertDataSetStrategyHandler = new InlineAssertDataSetStrategyHandler(inlineDataSetRowSourceFactory, this);
        }
        return inlineAssertDataSetStrategyHandler;
    }


    public LoadDataSetStrategy createInsertDataSetStrategy() {
        LoadDataSetStrategy insertDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "insert");
        insertDataSetStrategy.init(configuration, dataSourceWrapper, identifierProcessor);
        return insertDataSetStrategy;
    }

    public LoadDataSetStrategy createCleanInsertDataSetStrategy() {
        LoadDataSetStrategy cleanInsertDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "cleaninsert");
        cleanInsertDataSetStrategy.init(configuration, dataSourceWrapper, identifierProcessor);
        return cleanInsertDataSetStrategy;
    }

    public LoadDataSetStrategy createRefreshDataSetStrategy() {
        LoadDataSetStrategy refreshDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "refresh");
        refreshDataSetStrategy.init(configuration, dataSourceWrapper, identifierProcessor);
        return refreshDataSetStrategy;
    }

    public LoadDataSetStrategy createUpdateDataSetStrategy() {
        LoadDataSetStrategy updateDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, "update");
        updateDataSetStrategy.init(configuration, dataSourceWrapper, identifierProcessor);
        return updateDataSetStrategy;
    }


    public AssertDataSetStrategy createAssertDataSetStrategy() {
        AssertDataSetStrategy defaultAssertDataSetStrategy = getInstanceOf(AssertDataSetStrategy.class, configuration);
        defaultAssertDataSetStrategy.init(configuration, dataSourceWrapper, identifierProcessor);
        return defaultAssertDataSetStrategy;
    }


    public DataSetResolver getDataSetResolver() {
        if (dataSetResolver == null) {
            dataSetResolver = getInstanceOf(DataSetResolver.class, configuration);
            dataSetResolver.init(configuration);
        }
        return dataSetResolver;
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

    public SqlTypeHandlerRepository createSqlTypeHandlerRepository() {
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        sqlTypeHandlerRepository.init(configuration);
        return sqlTypeHandlerRepository;
    }


    public DataSetComparator createDataSetComparator(DataSetRowProcessor dataSetRowProcessor, TableContentRetriever tableContentRetriever) {
        DataSetComparator dataSetComparator = getInstanceOf(DataSetComparator.class, configuration);
        dataSetComparator.init(dataSetRowProcessor, tableContentRetriever, dataSourceWrapper);
        return dataSetComparator;
    }

    public DatabaseContentLogger createDatabaseContentLogger(TableContentRetriever tableContentRetriever) {
        DatabaseContentLogger databaseContentLogger = getInstanceOf(DatabaseContentLogger.class, configuration);
        // todo move out
        DataSetDatabaseHelper dataSetDatabaseHelper = new DataSetDatabaseHelper(dataSourceWrapper, identifierProcessor);
        databaseContentLogger.init(dataSourceWrapper, tableContentRetriever, dataSetDatabaseHelper);
        return databaseContentLogger;
    }


    public DataSetStructureGenerator getDataSetStructureGenerator() {
        if (dataSetStructureGenerator == null) {
            String defaultTargetDirectory = configuration.getProperty(PROPKEY_XSD_TARGETDIRNAME);
            dataSetStructureGenerator = getInstanceOf(DataSetStructureGenerator.class, configuration);
            dataSetStructureGenerator.init(dataSourceWrapper, defaultTargetDirectory);
        }
        return dataSetStructureGenerator;
    }
}