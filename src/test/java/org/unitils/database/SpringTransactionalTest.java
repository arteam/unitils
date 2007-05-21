/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.database;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.dbmaintainer.util.BaseDataSourceDecorator;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.database.util.BaseConnectionDecorator;
import org.easymock.classextension.EasyMock;
import static org.easymock.EasyMock.expect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringTransactionalTest extends AbstractTransactionalSpringContextTests {

    private static Connection mockConnection1 = EasyMock.createStrictMock(Connection.class), mockConnection2 = EasyMock.createStrictMock(Connection.class);

    private static BaseConnectionDecorator con1 = new BaseConnectionDecorator(mockConnection1), con2 = new BaseConnectionDecorator(mockConnection2);

    protected String[] getConfigLocations() {
        return new String[] {"org/unitils/database/ac.xml"};
    }

    protected void onSetUpBeforeTransaction() throws Exception {
        setDefaultRollback(false);

        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.commit();
        mockConnection1.close();
        EasyMock.replay(mockConnection1, mockConnection2);
    }

    public void testSpring() throws Exception {

        DataSource dataSource = (DataSource) getApplicationContext().getBean("dataSource");
        Connection conn1 = DataSourceUtils.getConnection(dataSource);
        DataSourceUtils.releaseConnection(conn1, dataSource);
        Connection conn2 = DataSourceUtils.getConnection(dataSource);
        DataSourceUtils.releaseConnection(conn1, dataSource);
        assertSame(conn1, conn2);
    }

    protected void onTearDownAfterTransaction() throws Exception {
        EasyMock.verify(mockConnection1, mockConnection2);
    }

    public static class MockDataSource extends BaseDataSourceDecorator {

        boolean firstTime = true;

        /**
         * Creates a new instance that wraps the given <code>DataSource</code>
         */
        public MockDataSource() {
            super(null);
        }


        public Connection getConnection() throws SQLException {
            if (firstTime) {
                firstTime = false;
                return con1;
            } else {
                return con2;
            }
        }
    }
}
