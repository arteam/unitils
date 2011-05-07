package org.unitils.mock.example3;

import org.unitils.mock.example2.MyDao;

import javax.sql.DataSource;

// START SNIPPET: service
public class MyService {

    public void doService() {
        DataSource dataSource = createDataSource();
        MyDao myDao = getMyDao();

        myDao.storeSomething("something", dataSource);
    }

    protected DataSource createDataSource() {
        // ... create a data source ...
        // END SNIPPET: service
        return null;
        // START SNIPPET: service
    }

    protected MyDao getMyDao() {
        return ServiceLocator.getMyDao();
    }
    // END SNIPPET: service

    private static class ServiceLocator {

        public static MyDao getMyDao() {
            return null;
        }
    }
    // START SNIPPET: service
}
// END SNIPPET: service
