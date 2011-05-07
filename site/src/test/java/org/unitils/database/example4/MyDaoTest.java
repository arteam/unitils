package org.unitils.database.example4;

import org.junit.Before;
import org.unitils.database.DatabaseUnitils;

import javax.sql.DataSource;


// START SNIPPET: get
public class MyDaoTest {

    private DataSource hsqldbDataSource;
    private DataSource oracleDataSource;

    @Before
    public void initialize() {
        hsqldbDataSource = DatabaseUnitils.getDataSource("database1");
        oracleDataSource = DatabaseUnitils.getDataSource("database2");
    }

}
// END SNIPPET: get
