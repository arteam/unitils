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
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.core.preparedstatement.InsertUpdatePreparedStatement;
import org.unitils.dataset.loader.DataSetLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseDataSetLoader implements DataSetLoader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseDataSetLoader.class);

    protected DataSource dataSource;
    protected DatabaseMetaDataHelper databaseMetaDataHelper;


    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void load(DataSet dataSet, List<String> variables) {
        try {
            loadDataSet(dataSet, variables);
        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set.", e);
        }
    }

    protected abstract InsertUpdatePreparedStatement createPreparedStatementWrapper(Table table, Connection connection) throws Exception;


    protected void loadDataSet(DataSet dataSet, List<String> variables) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            for (Schema schema : dataSet.getSchemas()) {
                loadSchema(schema, variables, connection);
            }
        } finally {
            connection.close();
        }
    }

    protected void loadSchema(Schema schema, List<String> variables, Connection connection) throws SQLException {
        for (Table table : schema.getTables()) {
            loadTable(table, variables, connection);
        }
    }

    protected void loadTable(Table table, List<String> variables, Connection connection) {
        for (Row row : table.getRows()) {
            if (row.getNrOfColumns() == 0) {
                continue;
            }
            loadRowHandleExceptions(row, variables, connection);
        }
    }

    protected int loadRowHandleExceptions(Row row, List<String> variables, Connection connection) {
        try {
            return loadRow(row, variables, connection);
        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set row for table: " + row.getTable() + ", row: [" + row + "], variables: " + variables, e);
        }
    }

    protected int loadRow(Row row, List<String> variables, Connection connection) throws Exception {
        InsertUpdatePreparedStatement preparedStatementWrapper = createPreparedStatementWrapper(row.getTable(), connection);
        try {
            return preparedStatementWrapper.executeUpdate(row, variables);
        } finally {
            preparedStatementWrapper.close();
        }
    }

}