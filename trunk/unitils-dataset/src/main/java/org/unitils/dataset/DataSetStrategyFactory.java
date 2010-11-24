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
import org.unitils.dataset.database.DataSetDatabaseHelper;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.loadstrategy.impl.TableContentDeleter;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetStrategyFactory {

    /* The unitils configuration */
    protected Properties configuration;
    protected DataSourceWrapper dataSourceWrapper;

    protected DataSetRowProcessor dataSetRowProcessor;
    protected SqlTypeHandlerRepository sqlTypeHandlerRepository;
    protected DataSetComparator dataSetComparator;
    protected TableContentRetriever tableContentRetriever;
    protected DatabaseContentLogger databaseContentLogger;
    protected TableContentDeleter tableContentDeleter;
    protected DatabaseAccessor databaseAccessor;
    protected DataSetDatabaseHelper dataSetDatabaseHelper;


    public DataSetStrategyFactory(Properties configuration, DataSourceWrapper dataSourceWrapper) {
        this.configuration = configuration;
        this.dataSourceWrapper = dataSourceWrapper;
    }


    public LoadDataSetStrategy createInsertDataSetStrategy() {
        return createLoadDataSetStrategy("insert");
    }

    public LoadDataSetStrategy createCleanInsertDataSetStrategy() {
        return createLoadDataSetStrategy("cleaninsert");
    }

    public LoadDataSetStrategy createRefreshDataSetStrategy() {
        return createLoadDataSetStrategy("refresh");
    }

    public LoadDataSetStrategy createUpdateDataSetStrategy() {
        return createLoadDataSetStrategy("update");
    }

    public AssertDataSetStrategy createAssertDataSetStrategy() {
        DataSetComparator dataSetComparator = getDataSetComparator();
        DatabaseContentLogger databaseContentLogger = getDatabaseContentLogger();

        AssertDataSetStrategy defaultAssertDataSetStrategy = getInstanceOf(AssertDataSetStrategy.class, configuration);
        defaultAssertDataSetStrategy.init(dataSetComparator, databaseContentLogger);
        return defaultAssertDataSetStrategy;
    }


    protected LoadDataSetStrategy createLoadDataSetStrategy(String type) {
        DatabaseAccessor databaseAccessor = getDatabaseAccessor();
        DataSetDatabaseHelper dataSetDatabaseHelper = getDataSetDatabaseHelper();
        DataSetRowProcessor dataSetRowProcessor = getDataSetRowProcessor();
        TableContentDeleter tableContentDeleter = getTableContentDeleter();

        LoadDataSetStrategy loadDataSetStrategy = getInstanceOf(LoadDataSetStrategy.class, configuration, type);
        loadDataSetStrategy.init(databaseAccessor, dataSetDatabaseHelper, dataSetRowProcessor, tableContentDeleter);
        return loadDataSetStrategy;
    }


    protected DataSetDatabaseHelper getDataSetDatabaseHelper() {
        if (dataSetDatabaseHelper == null) {
            dataSetDatabaseHelper = new DataSetDatabaseHelper(dataSourceWrapper);
        }
        return dataSetDatabaseHelper;
    }

    protected DataSetRowProcessor getDataSetRowProcessor() {
        if (dataSetRowProcessor == null) {
            DataSetDatabaseHelper dataSetDatabaseHelper = getDataSetDatabaseHelper();
            SqlTypeHandlerRepository sqlTypeHandlerRepository = getSqlTypeHandlerRepository();
            dataSetRowProcessor = new DataSetRowProcessor(dataSetDatabaseHelper, sqlTypeHandlerRepository, dataSourceWrapper);
        }
        return dataSetRowProcessor;
    }

    protected DataSetComparator getDataSetComparator() {
        if (dataSetComparator == null) {
            DataSetRowProcessor dataSetRowProcessor = getDataSetRowProcessor();
            TableContentRetriever tableContentRetriever = getTableContentRetriever();
            dataSetComparator = getInstanceOf(DataSetComparator.class, configuration);
            dataSetComparator.init(dataSetRowProcessor, tableContentRetriever, dataSourceWrapper);
        }
        return dataSetComparator;
    }

    protected TableContentRetriever getTableContentRetriever() {
        if (tableContentRetriever == null) {
            SqlTypeHandlerRepository sqlTypeHandlerRepository = getSqlTypeHandlerRepository();
            tableContentRetriever = new TableContentRetriever(dataSourceWrapper, sqlTypeHandlerRepository);
        }
        return tableContentRetriever;
    }

    protected DatabaseContentLogger getDatabaseContentLogger() {
        if (databaseContentLogger == null) {
            DataSetDatabaseHelper dataSetDatabaseHelper = getDataSetDatabaseHelper();
            TableContentRetriever tableContentRetriever = getTableContentRetriever();
            databaseContentLogger = getInstanceOf(DatabaseContentLogger.class, configuration);
            databaseContentLogger.init(dataSourceWrapper, tableContentRetriever, dataSetDatabaseHelper);
        }
        return databaseContentLogger;
    }

    protected TableContentDeleter getTableContentDeleter() {
        if (tableContentDeleter == null) {
            DatabaseAccessor databaseAccessor = getDatabaseAccessor();
            tableContentDeleter = new TableContentDeleter(databaseAccessor, dataSourceWrapper);
        }
        return tableContentDeleter;
    }

    protected DatabaseAccessor getDatabaseAccessor() {
        if (databaseAccessor == null) {
            databaseAccessor = new DatabaseAccessor(dataSourceWrapper);
        }
        return databaseAccessor;
    }

    protected SqlTypeHandlerRepository getSqlTypeHandlerRepository() {
        if (sqlTypeHandlerRepository == null) {
            sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
            sqlTypeHandlerRepository.init(configuration);
        }
        return sqlTypeHandlerRepository;
    }
}