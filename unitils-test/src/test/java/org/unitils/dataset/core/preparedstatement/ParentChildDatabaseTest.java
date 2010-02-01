/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dataset.core.preparedstatement;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.mock.Mock;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ParentChildDatabaseTest {

    /* Tested object */
    private Database database;

    private Mock<DbSupport> dbSupport;


    @Before
    public void initializeForeignKeyColumns() throws SQLException {
        database = new Database(dbSupport.getMock());

        Map<String, String> parentChildColumnNames = new LinkedHashMap<String, String>();
        parentChildColumnNames.put("pk1", "fk1");
        parentChildColumnNames.put("pk2", "fk2");
        //database.returns(parentChildColumnNames).getChildForeignKeyColumns(null, null);
    }


    // todo implement
    @Test
    @Ignore
    public void executeUpdate() throws Exception {
        throw new NullPointerException("todo implement");
        /* Row parentRow = new Row();
        parentRow.addColumn(createColumn("pk1", "1"));
        parentRow.addColumn(createColumn("pk2", "2"));

        Row childRow = new Row(parentRow, false);
        childRow.addColumn(createColumn("column_1", "1"));

        insertPreparedStatement.executeUpdate(childRow, emptyVariables);

        database.assertInvoked().createPreparedStatement("insert into my_schema.table_a (column_1, 'fk1', 'fk2') values (?, ?, ?)");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "2", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
        */
    }


}