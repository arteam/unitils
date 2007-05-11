package org.untils.sample.eshop.dao;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.sample.eshop.dao.PurchaseDao;
import org.unitils.sample.eshop.model.User;

/**
 * 
 */
public class PurchaseDaoTest extends DBTestCase {

    private PurchaseDao purchaseDao = new PurchaseDao();

    public PurchaseDaoTest(String string) {
        super(string);
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
                "jdbc:hsqldb:hsql://localhost/eshop");
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
	    System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "ESHOP");
    }

    protected void setUp() throws Exception {
        super.setUp();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("eshop-config.xml");
        purchaseDao = (PurchaseDao) applicationContext.getBean("purchaseDao");
    }

    public void testCalculateTotalPurchaseAmount() {
        long totalAmount = purchaseDao.calculateTotalPurchaseAmount(new User(1L, null, 0));
        assertEquals(30, totalAmount); 
    }

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getResourceAsStream("PurchaseDaoTest.xml"));
    }
}
