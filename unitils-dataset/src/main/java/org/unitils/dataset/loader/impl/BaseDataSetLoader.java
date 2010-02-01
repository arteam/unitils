/*
 * Copyright 2009,  Unitils.org
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
package org.unitils.dataset.loader.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.*;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.loader.RowLoader;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseDataSetLoader implements DataSetLoader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseDataSetLoader.class);

    protected Database database;
    protected Database databaseMetaDataHelper;
    protected NameProcessor nameProcessor;


    public void init(Database database) {
        this.database = database;
        this.nameProcessor = new NameProcessor(database.getIdentifierQuoteString());
    }

    public void load(DataSet dataSet, List<String> variables) {
        try {
            RowLoader rowLoader = createRowLoader(dataSet);
            loadDataSet(dataSet, variables, rowLoader);

        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set.", e);
        }
    }

    protected RowLoader createRowLoader(DataSet dataSet) throws Exception {
        ColumnProcessor columnProcessor = new ColumnProcessor(dataSet.getLiteralToken(), dataSet.getVariableToken(), nameProcessor);

        RowLoader rowLoader = createRowLoader();
        rowLoader.init(columnProcessor, nameProcessor, database);
        return rowLoader;
    }

    protected abstract RowLoader createRowLoader();


    protected void loadDataSet(DataSet dataSet, List<String> variables, RowLoader rowLoader) throws SQLException {
        for (Schema schema : dataSet.getSchemas()) {
            loadSchema(schema, variables, rowLoader);
        }
    }

    protected void loadSchema(Schema schema, List<String> variables, RowLoader rowLoader) throws SQLException {
        for (Table table : schema.getTables()) {
            loadTable(table, variables, rowLoader);
        }
    }

    protected void loadTable(Table table, List<String> variables, RowLoader rowLoader) {
        for (Row row : table.getRows()) {
            if (row.getNrOfColumns() == 0) {
                continue;
            }
            rowLoader.loadRow(row, variables);
        }
    }

}