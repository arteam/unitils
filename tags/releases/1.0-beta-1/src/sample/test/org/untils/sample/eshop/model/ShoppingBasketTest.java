package org.untils.sample.eshop.model;

import org.unitils.UnitilsJUnit3;
import org.unitils.inject.annotation.Inject;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.sample.eshop.dao.UserDao;
import org.unitils.sample.eshop.model.User;
import org.unitils.sample.eshop.model.ShoppingBasketItem;
import org.unitils.sample.eshop.model.ShoppingBasket;
import org.unitils.easymock.annotation.Mock;
import org.easymock.EasyMock;

/**
 * 
 */
public class ShoppingBasketTest extends UnitilsJUnit3 {

    @Mock private UserDao mockUserDao;

    @Mock private ShoppingBasketItem item1;

    @Mock private ShoppingBasketItem item2;

    @TestedObject
    private ShoppingBasket shoppingBasket;

    private User user;

    protected void setUp() throws Exception {
        super.setUp();

        user = new User(0L, null, 18);

        shoppingBasket = new ShoppingBasket();
        shoppingBasket.getItems().add(item1);
        shoppingBasket.getItems().add(item2);
    }

    public void testCheckout() {
        EasyMock.expect(mockUserDao.findById(null)).andReturn(user);

    }
}
