package org.untils.sample.eshop.dao;

import org.unitils.sample.eshop.dao.DiscountDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByType;
import org.untils.sample.eshop.BaseHibernateTest;

/**
 * 
 */
@DataSet
public class DiscountDaoTest extends BaseHibernateTest {

    @SpringBeanByType
    private DiscountDao discountDao = new DiscountDao();

    public void testCalculateTotalPurchaseAmount() {
        long totalAmount = discountDao.calculateTotalPurchaseAmount(new User(1L, null, 0));
        assertEquals(30, totalAmount);
    }
}
