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

import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetloadstrategy.impl.InsertLoadStrategy;
import org.unitils.hibernate.HibernateUnitils;
import org.unitils.sample.eshop.dao.JPAPurchaseDao;
import org.unitils.sample.eshop.dao.PurchaseDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

/**
 * todo javadoc
 */ 
@DataSet(loadStrategy = InsertLoadStrategy.class)
@SpringApplicationContext({"eshop-config.xml", "test-config.xml"})
@Transactional(TransactionMode.ROLLBACK)
public class PurchaseDaoTest extends UnitilsJUnit4 {

    /* Object under test */
    //@SpringBean("purchaseDao")
    private JPAPurchaseDao purchaseDao;

    /* Test user */
    private User testUser;


    @Before
    public void initializeFixture() {
    	/*purchaseDao = new JPAPurchaseDao();
    	EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("test");
    	purchaseDao.setEntityManagerFactory(emFactory);*/
    	
        testUser = new User(1L, null, 0);
    }

    @Test
    public void testCalculateTotalPurchaseAmount() {
        Long totalAmount = purchaseDao.calculateTotalPurchaseAmount(testUser);
        HibernateUnitils.flushDatabaseUpdates();
        assertLenEquals(30, totalAmount);
    }
    
    @Test
    public void testCalculateTotalPurchaseAmount1() {
        Long totalAmount = purchaseDao.calculateTotalPurchaseAmount(testUser);
        assertLenEquals(30, totalAmount);
    }
    
}
