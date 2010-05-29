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
package org.unitils.dataset.core.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.LoadDataSetStrategy;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.loader.impl.IdentifierNameProcessor;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.util.List;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseLoadDataSetStrategy implements LoadDataSetStrategy {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseLoadDataSetStrategy.class);

    protected DatabaseAccessor databaseAccessor;
    protected IdentifierNameProcessor identifierNameProcessor;
    protected DataSetRowProcessor dataSetRowProcessor;


    public void init(Properties configuration, DatabaseMetaData database) {
        this.databaseAccessor = createDatabaseAccessor(database);
        this.identifierNameProcessor = createIdentifierNameProcessor(database);
        this.dataSetRowProcessor = createDataSetRowProcessor(identifierNameProcessor, database);
    }


    public void perform(DataSetRowSource dataSetRowSource, List<String> variables) {
        logger.info("Loading data set file: " + dataSetRowSource.getDataSetName());
        try {
            dataSetRowSource.open();

            DataSetLoader dataSetLoader = createDataSetLoader(dataSetRowProcessor, databaseAccessor);
            dataSetLoader.load(dataSetRowSource, variables);

        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set file: " + dataSetRowSource.getDataSetName(), e);
        } finally {
            dataSetRowSource.close();
        }
    }


    protected abstract DataSetLoader createDataSetLoader(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor);


    protected DatabaseAccessor createDatabaseAccessor(DatabaseMetaData database) {
        return new DatabaseAccessor(database);
    }

    protected IdentifierNameProcessor createIdentifierNameProcessor(DatabaseMetaData database) {
        // todo refactor initialization
        IdentifierNameProcessor identifierNameProcessor = new IdentifierNameProcessor();
        identifierNameProcessor.init(database);
        return identifierNameProcessor;
    }

    protected DataSetRowProcessor createDataSetRowProcessor(IdentifierNameProcessor identifierNameProcessor, DatabaseMetaData database) {
        // todo refactor initialization
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        return new DataSetRowProcessor(identifierNameProcessor, sqlTypeHandlerRepository, database);
    }


}
