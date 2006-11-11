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


import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Embeddable
public class ProductPrice {

    @OneToMany
    @JoinColumn(name = "PRODUCT_ID")
    private List<PriceTableItem> staffle = new ArrayList<PriceTableItem>();

    public ProductPrice() {}

    public ProductPrice(List<PriceTableItem> staffle) {
        this();
        this.staffle = staffle;
    }

    public double getPriceFor(int amount) {
        PriceTableItem applicablePriceTableItem = null;
        for (PriceTableItem priceTableItem : staffle) {
            if (priceTableItem.getAmount() <= amount) {
                applicablePriceTableItem = priceTableItem;
            }
        }
        return applicablePriceTableItem.getPrice();
    }

    @Entity
    @Table(name = "PRODUCT_PRICE_TABLE")
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "PRICE_TABLE_ITEM_SEQ")
    public static class PriceTableItem {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQUENCE")
        private Long id;

        @Column
        private int amount;

        @Column
        private double price;

        private PriceTableItem() {}

        public PriceTableItem(int amount, double price) {
            this();
            this.amount = amount;
            this.price = price;
        }

        public int getAmount() {
            return amount;
        }

        public double getPrice() {
            return price;
        }

    }
}
