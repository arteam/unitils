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
package org.unitils.sample.eshop.model;

import org.unitils.sample.eshop.exception.NotOldEnoughException;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
@Entity
@Table(name = "PURCHASE")
@SequenceGenerator(name = "SEQUENCE", sequenceName = "PURCHASE_ID_SEQ")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany
    @JoinColumn(name = "PURCHASE_ID")
    private List<PurchaseItem> items = new ArrayList<PurchaseItem>();

    protected Purchase() {}

    public Purchase(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    /**
     * Adds amount items of the given Product to this Purchase.
     * @param product
     * @param amount
     */
    public void addItem(Product product, int amount) {
        if (product.getMinimumAge() > user.getAge()) {
            throw new NotOldEnoughException();
        }
        PurchaseItem currentItemForProduct = getItemForProduct(product);
        if (currentItemForProduct != null) {
            currentItemForProduct.setAmount(currentItemForProduct.getAmount() + amount);
            return;
        }
        if (amount > 0) {
            items.add(new PurchaseItem(product, amount));
        }
    }

    public PurchaseItem getItemForProduct(Product product) {
        for (PurchaseItem item : items) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
    }

    public double getTotalPrice() {
        double totalPrice = 0d;
        for (PurchaseItem item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }
    
}
