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
import org.unitils.dataset.assertstrategy.DataSetComparator;
import org.unitils.dataset.assertstrategy.DatabaseContentLogger;
import org.unitils.dataset.assertstrategy.impl.TableContentRetriever;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;
import org.unitils.dataset.rowsource.InlineDataSetRowSourceFactory;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;

import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * Helper class for constructing parts of the data set module.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleFactoryHelper {

    /* The unitils configuration */
    protected Properties configuration;

    protected DatabaseMetaData databaseMetaData;


    /**
     * @param configuration    The unitils configuration, not null
     * @param databaseMetaData The data base meta data, not null
     */
    public DataSetModuleFactoryHelper(Properties configuration, DatabaseMetaData databaseMetaData) {
        this.configuration = configuration;
        this.databaseMetaData = databaseMetaData;
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


    public FileDataSetRowSourceFactory createFileDataSetRowSourceFactory() {
        FileDataSetRowSourceFactory xmlDataSetRowSourceFactory = getInstanceOf(FileDataSetRowSourceFactory.class, configuration);
        xmlDataSetRowSourceFactory.init(configuration, databaseMetaData.getSchemaName());
        return xmlDataSetRowSourceFactory;
    }

    public InlineDataSetRowSourceFactory createInlineDataSetRowSourceFactory() {
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getInstanceOf(InlineDataSetRowSourceFactory.class, configuration);
        inlineDataSetRowSourceFactory.init(configuration, databaseMetaData.getSchemaName());
        return inlineDataSetRowSourceFactory;
    }

    public SqlTypeHandlerRepository createSqlTypeHandlerRepository() {
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        sqlTypeHandlerRepository.init(configuration);
        return sqlTypeHandlerRepository;
    }


    public DataSetComparator createDataSetComparator(DataSetRowProcessor dataSetRowProcessor, TableContentRetriever tableContentRetriever) {
        DataSetComparator dataSetComparator = getInstanceOf(DataSetComparator.class, configuration);
        dataSetComparator.init(dataSetRowProcessor, tableContentRetriever, databaseMetaData);
        return dataSetComparator;
    }

    public DatabaseContentLogger createDatabaseContentLogger(TableContentRetriever tableContentRetriever) {
        DatabaseContentLogger databaseContentLogger = getInstanceOf(DatabaseContentLogger.class, configuration);
        databaseContentLogger.init(databaseMetaData, tableContentRetriever);
        return databaseContentLogger;
    }

    public DataSetStructureGenerator createDataSetStructureGenerator() {
        DataSetStructureGenerator dataSetStructureGenerator = getInstanceOf(DataSetStructureGenerator.class, configuration);
        dataSetStructureGenerator.init(configuration, databaseMetaData.getSQLHandler());
        return dataSetStructureGenerator;
    }
}