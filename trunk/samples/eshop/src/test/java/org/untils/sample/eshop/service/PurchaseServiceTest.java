package org.untils.sample.eshop.service;

import org.junit.Test;
import org.junit.Before;
import static org.easymock.classextension.EasyMock.*;
import org.unitils.sample.eshop.service.PaymentService;
import org.unitils.sample.eshop.service.StockService;
import org.unitils.sample.eshop.service.PurchaseService;
import org.unitils.sample.eshop.model.User;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.Product;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class PurchaseServiceTest {

    private PaymentService mockPaymentService;

    private StockService mockStockService;

    private PurchaseService purchaseService;

    private Purchase purchase;

    private Product product1, product2;

    @Before
    public void initFixture() {
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
