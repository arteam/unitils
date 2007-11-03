/*
 * Copyright 2006-2007,  Unitils.org
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
package org.untils.sample.eshop.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.sample.eshop.exception.NotOldEnoughException;
import org.unitils.sample.eshop.model.Product;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;

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
        user = new User(1L);
        user.setAge(12);
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
