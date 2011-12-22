package org.unitils.mock.example1;

import org.unitils.reflectionassert.User;

import java.util.List;

public interface UserDao {

    User findUserById(long id);

    User findUserByName(String name);

    List<User> getUsersInGroup(String groupName);

    List<User> getUsersForIds(List<Long> userIds);

    boolean isExistingUser(String firstName, String lastName, long anyAge);

    List<User> getUsersWithExpiredLicense();
}
