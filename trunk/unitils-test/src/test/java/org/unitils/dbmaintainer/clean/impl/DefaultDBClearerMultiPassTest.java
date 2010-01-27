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
package org.unitils.dbmaintainer.clean.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.mock.Mock;

/**
 * Test class for the {@link DBClearer} to verify that we will keep trying to
 * drop database objects even if we get exceptions (until we make no more progress).
 *  
 * @see MultiPassErrorHandler
 * 
 * 
 * @author Mark Jeffrey
 */
public class DefaultDBClearerMultiPassTest extends UnitilsJUnit4 {

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /**
     * Allows us to replace the dbSupport created during init() with our own
     * Mock which will throw exceptions when needed.
     */
    class TestDBClearer extends DefaultDBClearer {
        public void setDbSupport(DbSupport dbSupport) {
            this.dbSupports = Arrays.asList(dbSupport);
        }
    }

    /* Tested object */
    private TestDBClearer testDbClearer;

    /* The DbSupport object */
    private Mock<DbSupport> dbSupportMock;

    private final Set<String> tableNames = new LinkedHashSet<String>(Arrays.asList("TABLE1", "TABLE2", "TABLE3"));

    /**
     * Configures the tested object.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        testDbClearer = new TestDBClearer();
        testDbClearer.init(configuration, sqlHandler);
        // this is a little strange but we now replace the dbSupport used for
        // setup with out mock...
        testDbClearer.setDbSupport(dbSupportMock.getMock());
        dbSupportMock.returns(tableNames).getTableNames();
    }

    /**
     * When we throw an exception on the first pass then it is ignored and we try another pass (which succeeds).
     */
    @Test
    public void testClearDatabase_IgnoreFirstErrorOnDropTable() throws Exception {
        dbSupportMock.onceRaises(new RuntimeException("Test Exception")).dropTable("TABLE2");
        testDbClearer.clearSchemas();
    }

    /**
     * When exceptions do not decrease then we throw an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testClearDatabase_ThrowExceptionWhenExcdeptionsDoNotDecrease() throws Exception {
        dbSupportMock.raises(new IllegalStateException("Test Exception")).dropTable("TABLE2");
        testDbClearer.clearSchemas();
    }

}
