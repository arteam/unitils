package org.untils.sample.eshop.model;

import org.easymock.EasyMock;
import org.unitils.UnitilsJUnit3;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.sample.eshop.dao.UserDao;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.PurchaseItem;
import org.unitils.sample.eshop.model.User;

/**
 * 
 */
public class ShoppingBasketTest extends UnitilsJUnit3 {

    @Mock private UserDao mockUserDao;

    @Mock private PurchaseItem item1;

    @Mock private PurchaseItem item2;

    @TestedObject
    private Purchase purchase;

    private User user;

    protected void setUp() throws Exception {
        super.setUp();

        user = new User(0L, null, 18);

        purchase = new Purchase(user);
        purchase.getItems().add(item1);
        purchase.getItems().add(item2);
    }

    public void testCheckout() {
        EasyMock.expect(mockUserDao.findById(null)).andReturn(user);

    }
}
