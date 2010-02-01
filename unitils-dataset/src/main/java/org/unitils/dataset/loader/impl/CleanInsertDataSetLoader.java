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

import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.loader.impl.TableContentDeleter;
import org.unitils.dataset.loader.RowLoader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * First deletes all data from the tables in the data set and then
 * loads the data using insert statements.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertDataSetLoader extends InsertDataSetLoader {


    @Override
    public void loadSchema(Schema schema, List<String> variables, RowLoader rowLoader) throws SQLException {
        TableContentDeleter deleteTableContentPreparedStatement = createTableContentDeleter();
        deleteDataFromTablesInReverseOrder(schema, deleteTableContentPreparedStatement);
        super.loadSchema(schema, variables, rowLoader);
    }


    protected void deleteDataFromTablesInReverseOrder(Schema schema, TableContentDeleter deleteTableContentPreparedStatement) throws SQLException {
        String schemaName = schema.getName();

        List<Table> tables = new ArrayList<Table>(schema.getTables());
        Collections.reverse(tables);
        for (Table table : tables) {
            deleteTableContentPreparedStatement.deleteTableContent(table);
        }
    }

    protected TableContentDeleter createTableContentDeleter() throws SQLException {
        return new TableContentDeleter(nameProcessor, database);
    }
}