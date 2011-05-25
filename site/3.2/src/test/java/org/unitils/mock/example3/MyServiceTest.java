package org.unitils.mock.example3;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.example2.MyDao;

import javax.sql.DataSource;

// START SNIPPET: serviceTest
public class MyServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private PartialMock<MyService> myService;

    private Mock<DataSource> dataSourceMock;
    private Mock<MyDao> myDaoMock;

    @Before
    public void initialize() {
        myService.returns(dataSourceMock).createDataSource();
        myService.returns(myDaoMock).getMyDao();
    }

    @Test
    public void testMethod() {
        myService.getMock().doService();

        myDaoMock.assertInvoked().storeSomething("something", null);
    }
}
// END SNIPPET: serviceTest