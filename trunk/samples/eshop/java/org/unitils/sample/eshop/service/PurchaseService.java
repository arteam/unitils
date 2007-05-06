package org.unitils.sample.eshop.service;

import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.PurchaseItem;
import org.unitils.sample.eshop.exception.InsufficientCreditException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class PurchaseService {

    private StockService stockService;

    private PaymentService paymentService;

    public boolean checkout(Purchase purchase) {
        for (PurchaseItem purchaseItem : purchase.getItems()) {
            stockService.registerPurchase(purchaseItem.getProduct(), purchaseItem.getAmount());
        }
        try {
            paymentService.credit(purchase.getUser(), purchase.getTotalPrice());
        } catch (InsufficientCreditException e) {
            return false;
        }
        return true;
    }
    
    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }


    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
