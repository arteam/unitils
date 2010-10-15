package org.unitils.dataset.database;

import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DatabaseTestUtils.createDatabaseMetaData;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;

/**
 * Simple test to see if the tableExists works as intended.
 *
 * @author Jeroen Horemans
 */

public class DatabaseMetaDataTableExistsTest extends UnitilsJUnit4{

    private DatabaseMetaData databaseMetaData;

    @TestDataSource
    protected DataSource dataSource;
    
    @Before
    public void initialize(){
        databaseMetaData = createDatabaseMetaData();
        dropTestTables();
        createTestTables();
    }
    
    @Test
    public void noTableExistsTest() throws SQLException{
       Assert.assertFalse(databaseMetaData.tableExists("PUBLIC.NON_EXISTING_TABLE"));
    }
    
    @Test
    public void tableExistsTest() throws SQLException{
       Assert.assertTrue(databaseMetaData.tableExists("PUBLIC.TEST"));
    }
    
    @Test
    public void tableExistsCachedTest() throws SQLException{
       Assert.assertTrue(databaseMetaData.tableExists("PUBLIC.TEST"));
       dropTestTables();
       Assert.assertTrue(databaseMetaData.tableExists("PUBLIC.TEST"));
    }
    
    
    
    
    @After
    public void cleanup() throws Exception {
        dropTestTables();
    }

    private void createTestTables() {
        executeUpdate("create table test (col1 varchar(100) not null, col2 integer not null, col3 timestamp, primary key (col1, col2))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table test", dataSource);
    }

}
