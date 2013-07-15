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
package org.unitils.dbunit.connection;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class DbUnitConnectionGetConnectionTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbUnitConnection dbUnitConnection;

    private Mock<DataSource> dataSourceMock;
    private Mock<Connection> connectionMock1;
    private Mock<Connection> connectionMock2;
    @Dummy
    private Connection nativeConnection1;
    @Dummy
    private Connection nativeConnection2;


    @Before
    public void initialize() throws Exception {
        dbUnitConnection = new DbUnitConnection(dataSourceMock.getMock(), "schema");

        dataSourceMock.onceReturns(connectionMock1).getConnection();
        dataSourceMock.onceReturns(connectionMock2).getConnection();
        connectionMock1.returns(nativeConnection1).getMetaData().getConnection();
        connectionMock2.returns(nativeConnection2).getMetaData().getConnection();
    }


    @Test
    public void nativeConnectionIsReturned() throws Exception {
        Connection connection = dbUnitConnection.getConnection();
        assertSame(nativeConnection1, connection);
    }

    @Test
    public void connectionIsCached() throws Exception {
        Connection connection1 = dbUnitConnection.getConnection();
        Connection connection2 = dbUnitConnection.getConnection();
        assertSame(connection1, connection2);
    }

    @Test
    public void newConnectionIsCreatedAfterClose() throws Exception {
        Connection connection1 = dbUnitConnection.getConnection();
        dbUnitConnection.closeJdbcConnection();
        Connection connection2 = dbUnitConnection.getConnection();
        assertSame(nativeConnection1, connection1);
        assertSame(nativeConnection2, connection2);
    }
}
