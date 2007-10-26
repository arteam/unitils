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
package org.untils.sample.eshop.service;

import static org.easymock.EasyMock.anyDouble;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.sample.eshop.model.Product;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;
import org.unitils.sample.eshop.service.PaymentService;
import org.unitils.sample.eshop.service.PurchaseService;
import org.unitils.sample.eshop.service.StockService;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.untils.sample.eshop.BaseHibernateTest;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@SpringApplicationContext("classpath:/eshop-config.xml")
@Transactional(TransactionMode.DISABLED)
public class PurchaseServiceTest extends BaseHibernateTest {

    private PaymentService mockPaymentService;

    private StockService mockStockService;

    private PurchaseService purchaseService;

    private Purchase purchase;

    private Product product1, product2;

    @Before
    public void setUp() {
        mockPaymentService = createMock(PaymentService.class);
        mockStockService = createMock(StockService.class);
        purchaseService = new PurchaseService();
        purchaseService.setPaymentService(mockPaymentService);
        purchaseService.setStockService(mockStockService);

        product1 = new Product(1L, 5.0, null, 0);
        product2 = new Product(2L, 5.0, null, 0);
        purchase = new Purchase(new User(0L, null, 0));
        purchase.addItem(product1, 1);
        purchase.addItem(product2, 2);
    }

    @Test
    public void testCheckout() {
        mockPaymentService.credit((User)anyObject(), anyDouble());
        mockStockService.registerPurchasedItems(product1, 1);
        mockStockService.registerPurchasedItems(product2, 2);
        replay(mockPaymentService);
        replay(mockStockService);

        purchaseService.checkout(purchase);
        verify(mockPaymentService);
        verify(mockStockService);
    }
}
