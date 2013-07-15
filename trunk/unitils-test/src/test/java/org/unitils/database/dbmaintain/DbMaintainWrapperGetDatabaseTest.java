/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.database.dbmaintain;

import org.dbmaintain.MainFactory;
import org.dbmaintain.database.Database;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.Databases;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainWrapperGetDatabaseTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainWrapper dbMaintainWrapper;

    private Mock<MainFactory> mainFactoryMock;
    private Mock<Databases> databasesMock;

    @Dummy
    private Database database1;
    @Dummy
    private Database database2;


    @Before
    public void initialize() {
        dbMaintainWrapper = new DbMaintainWrapper(mainFactoryMock.getMock(), true);
        mainFactoryMock.returns(databasesMock).getDatabases();

        databasesMock.returns(database1).getDefaultDatabase();
        databasesMock.returns(database2).getDatabase("database");
        databasesMock.raises(new DatabaseException("expected")).getDatabase("xxx");
    }


    @Test
    public void defaultDatabaseWhenNull() {
        Database result = dbMaintainWrapper.getDatabase(null);
        assertSame(database1, result);
    }

    @Test
    public void namedDatabase() {
        Database result = dbMaintainWrapper.getDatabase("database");
        assertSame(database2, result);
    }

    @Test
    public void exceptionWhenDatabaseNotKnown() {
        try {
            dbMaintainWrapper.getDatabase("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get database with name: xxx\n" +
                    "Reason: DatabaseException: expected", e.getMessage());
        }
    }
}
