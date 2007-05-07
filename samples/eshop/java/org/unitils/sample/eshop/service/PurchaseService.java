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

    /**
     * Registers the given purchase. The amount due is credited from the user's payment acount. If the user can't pay,
     * nothing is ordered. 
     *
     * @param purchase
     * @return True if the purchase succeeded successfully, false otherwise
     */
    public boolean checkout(Purchase purchase) {
        try {
            // Credits the total amount to pay, using the PaymentService. 
            paymentService.credit(purchase.getUser(), purchase.getTotalPrice());
        } catch (InsufficientCreditException e) {
            return false;
        }
        for (PurchaseItem purchaseItem : purchase.getItems()) {
            // Calls the StockService to register the ordering of the given amount of items of the given product.
            stockService.registerPurchasedItems(purchaseItem.getProduct(), purchaseItem.getAmount());
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
