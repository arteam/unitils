package org.unitils.mock.example2;

import javax.sql.DataSource;

public interface MyDao {

    String getSomething();

    void storeSomething(String value);

    void storeSomething(String value, DataSource dataSource);
}
