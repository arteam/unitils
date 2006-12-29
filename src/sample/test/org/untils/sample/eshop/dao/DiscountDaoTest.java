package org.untils.sample.eshop.dao;

import org.untils.sample.eshop.BaseHibernateDaoTest;
import org.unitils.sample.eshop.dao.DiscountDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.inject.annotation.TestedObject;

/**
 * 
 */
public class DiscountDaoTest extends BaseHibernateDaoTest {

    @TestedObject
    private DiscountDao discountDao = new DiscountDao();

    public void testCalculateTotalPurchaseAmount() {
        long totalAmount = discountDao.calculateTotalPurchaseAmount(new User(1L, null, 0));
        assertEquals(30, totalAmount);
    }
}
