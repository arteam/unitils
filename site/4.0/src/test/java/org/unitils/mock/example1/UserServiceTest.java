/*
 *
 *  Copyright 2010,  Unitils.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.unitils.mock.example1;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.reflectionassert.User;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.mock.ArgumentMatchers.*;

public class UserServiceTest extends UnitilsJUnit4 {

    private Mock<UserDao> userDaoMock;

    @Test
    public void behavior() {
        User user1 = new User(1, "first name", "last name");
        User user2 = new User(2, "first name", "last name");
        // START SNIPPET: arguments
        userDaoMock.returns(user1).findUserById(1);
        userDaoMock.returns(user2).findUserById(2);
        // END SNIPPET: arguments

        // START SNIPPET: ignoreArguments
        userDaoMock.returns(asList(user1, user2)).getUsersInGroup(null);
        // END SNIPPET: ignoreArguments

        // START SNIPPET: defaultArgumentMatcher
        userDaoMock.returns(asList(user1, user2)).getUsersForIds(asList(1L, 2L));
        // END SNIPPET: defaultArgumentMatcher

        // START SNIPPET: argumentMatchers
        userDaoMock.returns(asList(user1, user2)).getUsersForIds(any(List.class));
        userDaoMock.returns(user1).findUserByName(notNull(String.class));
        userDaoMock.returns(user1).findUserByName(isNull(String.class));
        // END SNIPPET: argumentMatchers

        // START SNIPPET: someArgumentMatchers
        userDaoMock.returns(true).isExistingUser("first", "last", anyInt());
        // END SNIPPET: someArgumentMatchers
    }
}

