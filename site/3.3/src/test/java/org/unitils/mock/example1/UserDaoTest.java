package org.unitils.mock.example1;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.reflectionassert.User;

public class UserDaoTest extends UnitilsJUnit4 {

    @TestedObject
    private Mock<UserDao> userDaoMock;

    private User defaultUser;
    private User john;

    // START SNIPPET: defaultBehavior
    @Before
    public void initialize() {
        userDaoMock.returns(defaultUser).findUserByName(null);
    }

    @Test
    public void testMethod() {
        userDaoMock.returns(john).findUserByName("john");
    }
    // END SNIPPET: defaultBehavior
}
