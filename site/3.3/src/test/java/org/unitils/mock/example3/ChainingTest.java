package org.unitils.mock.example3;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.sql.Connection;

public class ChainingTest extends UnitilsJUnit4 {

    private Mock<MyService> myServiceMock;

    @Test
    public void testMethod() throws Exception {
        Connection connection = null;
        // START SNIPPET: behavior
        myServiceMock.returns(connection).createDataSource().getConnection();
        // END SNIPPET: behavior
        // START SNIPPET: assert
        myServiceMock.assertInvoked().createDataSource().getConnection();
        // END SNIPPET: assert
    }
}
