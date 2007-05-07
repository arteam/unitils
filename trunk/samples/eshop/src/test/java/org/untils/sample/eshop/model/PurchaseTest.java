package org.untils.sample.eshop.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.unitils.sample.eshop.model.Product;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.PurchaseItem;
import org.unitils.sample.eshop.model.User;
import org.unitils.sample.eshop.exception.NotOldEnoughException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class PurchaseTest {

    private Purchase purchase;

    private Product productInPurchase1, productInPurchase2;

    private Product productNotInPurchase;

    private Product cigarettes;

    private User user;

    @Before
    public void initFixture() {
        user = new User(1L, null, 17);
        purchase = new Purchase(user);
        productInPurchase1 = new Product(1L, 10d, null, 0);
        productInPurchase2 = new Product(2L, 3d, null, 0);
        purchase.addItem(productInPurchase1, 5);
        purchase.addItem(productInPurchase2, 2);
        productNotInPurchase = new Product(3L, 0d, null, 0);
        cigarettes = new Product(4L, 5, null, 18);
    }

    @Test
    public void testGetTotalPrice() {
        assertEquals(10.0 * 5 + 3d * 2, purchase.getTotalPrice());
    }

    @Test
    public void testAddItem() {
        assertNull(purchase.getItemForProduct(productNotInPurchase));
        purchase.addItem(productNotInPurchase, 5);
        assertNotNull(purchase.getItemForProduct(productNotInPurchase));
    }

    @Test
    public void testAddItem_zeroAmount() {
        purchase.addItem(productNotInPurchase, 0);
        assertNull(purchase.getItemForProduct(productNotInPurchase));
    }

    @Test
    public void testAddItem_productAlreadyInPurchase() {
        purchase.addItem(productInPurchase1, 5);
        assertEquals(10, purchase.getItemForProduct(productInPurchase1).getAmount());
    }

    @Test(expected = NotOldEnoughException.class)
    public void testAddItem_userNotOldEnough() {
        purchase.addItem(cigarettes, 10);
    }
}
