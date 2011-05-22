package org.unitils.database.example1;

import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;

// START SNIPPET: get
public class MyDaoTest extends UnitilsJUnit4 {

    @TestDataSource
    private DataSource dataSource;

}
// END SNIPPET: get
