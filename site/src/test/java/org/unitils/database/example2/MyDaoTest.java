package org.unitils.database.example2;

import org.junit.Before;
import org.unitils.database.DatabaseUnitils;

import javax.sql.DataSource;


// START SNIPPET: get
public class MyDaoTest {

    private DataSource dataSource;

    @Before
    public void initialize() {
        dataSource = DatabaseUnitils.getDataSource();
    }

}
// END SNIPPET: get
