package org.unitils.database.example7;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.unitils.UnitilsTestExecutionListener;
import org.unitils.database.MyDao;
import org.unitils.dataset.annotation.DataSet;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

// START SNIPPET: test
@ContextConfiguration
@TestExecutionListeners(UnitilsTestExecutionListener.class)
public class MyDaoTest extends AbstractJUnit4SpringContextTests {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private MyDao myDao;

    @DataSet
    public void testMethod(){
        myDao.deleteAllUsers();

        int count = jdbcTemplate.queryForInt("select count(*) from users");
        assertEquals(0, count);
    }

}
// END SNIPPET: test
