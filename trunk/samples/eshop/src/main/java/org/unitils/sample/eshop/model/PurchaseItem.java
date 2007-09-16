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

import javax.persistence.*;

/**
 * Represents an amount of items of a certain Product that is contained in a certain Purchase
 */
@Entity
@Table(name = "PURCHASE_ITEM")
@SequenceGenerator(name = "SEQUENCE1", sequenceName = "PURCHASE_ITEM_ID_SEQ")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE1")
    private Long id;

    @Column
    private int amount;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    protected PurchaseItem() {
    }

    public PurchaseItem(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return The total price to pay for this PurchaseItem
     */
    public double getPrice() {
        return amount * product.getPriceFor(amount);
    }

    public Product getProduct() {
        return product;
    }
}
