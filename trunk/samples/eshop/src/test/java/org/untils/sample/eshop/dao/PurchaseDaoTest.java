/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
