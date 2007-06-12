package org.untils.sample.eshop.dao;

import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyRefEquals;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.unitils.sample.eshop.dao.UserDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.util.TransactionMode;
import org.unitils.UnitilsJUnit4;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.List;
import java.util.Arrays;

/**
 * User: FrBe
 * Date: 5-jun-2007
 * Time: 12:32:56
 */
@DataSet
@SpringApplicationContext({"eshop-config.xml", "test-config.xml"})
public class UserDaoTest extends UnitilsJUnit4 {

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