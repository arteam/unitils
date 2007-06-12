package org.untils.sample.eshop.dao;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyRefEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitils.sample.eshop.dao.UserDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * User: FrBe
 * Date: 5-jun-2007
 * Time: 12:32:56
 */
@DataSet
@SpringApplicationContext({"eshop-config.xml", "test-config.xml"})
public class UserDAOTest extends UnitilsJUnit4 {

    @SpringBean("userDao")
    private UserDao userDao;

    @TestDataSource
    private DataSource dataSource;

    @Test
    public void testFindById() {
        User user = userDao.findById(1L);
        assertRefEquals(new User("johnDoe", "John", "Doe"), user, ReflectionComparatorMode.IGNORE_DEFAULTS);
    }

    @Test
    public void testFindByLastName() {
        List<User> users = userDao.findByLastName("Doe");
        assertPropertyRefEquals("userName", Arrays.asList("johnDoe", "janeDoe"), users, ReflectionComparatorMode.LENIENT_ORDER);
    }
}