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
package org.unitils.sample.eshop.model;

import org.unitils.sample.eshop.exception.NotOldEnoughException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Purchase that a User makes. A Purchase consists of different PurchaseItems.
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

    protected Purchase() {
    }

    public Purchase(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public List<PurchaseItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Adds amount items of the given Product to this Purchase. If amount == 0 nothing is added. If this Purchase already
     * contains a PurchaseItem for the given Product, the amount of this PurchaseItem is increased.
     *
     * @param product The product for which items are added to this Purchase
     * @param amount  The number of items of the given Product that are added to this Purchase
     * @throws NotOldEnoughException If the user's age is not sufficient to be authorized to buy items of the given
     *                               Product
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

    /**
     * @param product A product
     * @return If this Purchase contains a PurchaseItem for the given Product, this PurchaseItem is returned. Otherwise,
     *         null is returned.
     */
    public PurchaseItem getItemForProduct(Product product) {
        for (PurchaseItem item : items) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @return The total price for this purchase
     */
    public double getTotalPrice() {
        double totalPrice = 0d;
        for (PurchaseItem item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

}
