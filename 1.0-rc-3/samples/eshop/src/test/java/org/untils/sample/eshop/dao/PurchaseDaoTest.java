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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetloadstrategy.CleanInsertLoadStrategy;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import org.unitils.sample.eshop.dao.PurchaseDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import javax.sql.DataSource;

/**
 * todo javadoc
 */
@DataSet(loadStrategy = CleanInsertLoadStrategy.class)
@Transactional(TransactionMode.DISABLED)
@SpringApplicationContext({"eshop-config.xml", "test-config.xml"})
public class PurchaseDaoTest extends UnitilsJUnit4 {

    /* Object under test */
    @SpringBean("purchaseDao")
    private PurchaseDao purchaseDao;

    @TestDataSource
    private DataSource dataSource;

    /* Test user */
    private User testUser;


    @Before
    public void initializeFixture() {
        testUser = new User(1L);
    }

    @Test
    public void testCalculateTotalPurchaseAmount() {
        Long totalAmount = purchaseDao.calculateTotalPurchaseAmount(testUser);
        assertLenEquals(30, totalAmount);
    }

}
