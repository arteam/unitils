package org.unitils.database.example3;

import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;


// START SNIPPET: get
public class MyDaoTest {

    @TestDataSource("database1")
    private DataSource hsqldbDataSource;

    @TestDataSource("database2")
    private DataSource oracleDataSource;

}
// END SNIPPET: get
