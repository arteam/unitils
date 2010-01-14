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

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.core.preparedstatement.InsertUpdatePreparedStatement;
import org.unitils.dataset.core.preparedstatement.UpdatePreparedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateDataSetLoader extends BaseDataSetLoader {


    protected InsertUpdatePreparedStatement createPreparedStatementWrapper(Table table, Connection connection) throws SQLException {
        return new UpdatePreparedStatement(table.getSchema().getName(), table.getName(), connection);
    }

    @Override
    protected int loadRow(Row row, List<String> variables, Connection connection) throws Exception {
        int nrUpdates = super.loadRow(row, variables, connection);
        if (nrUpdates == 0) {
            throw new UnitilsException("Unable to update record for data set. No record found in database with matching primary key values.");
        }
        return nrUpdates;
    }

}