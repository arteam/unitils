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
package org.unitils.dataset.loadstrategy.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.database.DataSetDatabaseHelper;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.loadstrategy.loader.DataSetLoader;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.List;

import static org.unitils.util.ExceptionUtils.getAllMessages;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseLoadDataSetStrategy implements LoadDataSetStrategy {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseLoadDataSetStrategy.class);

    protected DatabaseAccessor databaseAccessor;
    protected DataSetDatabaseHelper dataSetDatabaseHelper;
    protected DataSetRowProcessor dataSetRowProcessor;
    protected TableContentDeleter tableContentDeleter;


    public void init(DatabaseAccessor databaseAccessor, DataSetDatabaseHelper dataSetDatabaseHelper, DataSetRowProcessor dataSetRowProcessor, TableContentDeleter tableContentDeleter) {
        this.databaseAccessor = databaseAccessor;
        this.dataSetDatabaseHelper = dataSetDatabaseHelper;
        this.dataSetRowProcessor = dataSetRowProcessor;
        this.tableContentDeleter = tableContentDeleter;
    }


    public void perform(DataSetRowSource dataSetRowSource, List<String> variables) {
        logger.info("Loading data set file: " + dataSetRowSource.getDataSetName());
        try {
            dataSetRowSource.open();

            DataSetLoader dataSetLoader = createDataSetLoader(dataSetRowProcessor, databaseAccessor);
            dataSetLoader.load(dataSetRowSource, variables);

        } catch (Exception e) {
            String message = getAllMessages(e);
            throw new UnitilsException("Unable to load data set file: " + dataSetRowSource.getDataSetName() + "\n" + message, e);
        } finally {
            dataSetRowSource.close();
        }
    }


    protected abstract DataSetLoader createDataSetLoader(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor);


}
