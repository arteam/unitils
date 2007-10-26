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
package org.unitils.sample.eshop.service;

import org.unitils.sample.eshop.exception.InsufficientCreditException;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.PurchaseItem;

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
     * @param purchase The purchase, not null
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
