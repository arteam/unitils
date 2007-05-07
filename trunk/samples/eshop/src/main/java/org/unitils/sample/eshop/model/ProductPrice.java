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


import static java.util.Arrays.asList;
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
import java.util.Arrays;

/**
 * Composite object that represents the price of a Product. Can be a simple price, or can consist of a price table
 * where lower prices are used for higher amounts of items ordered.
 */
@Embeddable
public class ProductPrice {

    @OneToMany
    @JoinColumn(name = "PRODUCT_ID")
    private List<PriceTableItem> priceTable = new ArrayList<PriceTableItem>();

    protected ProductPrice() {}

    /**
     * Constructs a simple ProductPrice object, in which a single price is used whatever amount of products is bought
     *
     * @param price The price that is used for the product.
     */
    public ProductPrice(double price) {
        this.priceTable = asList(new PriceTableItem(0, price));
    }

    /**
     * Constructs a ProductPrice object, in which the given priceTable is used to defer the price. Typically, lower
     * prices are used when ordering a larger amount of products
     * 
     * @param priceTable The priceTable that will be used for defering the price of a Product.
     */
    public ProductPrice(List<PriceTableItem> priceTable) {
        this.priceTable = priceTable;
    }

    /**
     * @param amount The amount of items for which the concrete price is retrieved
     * @return The concrete price, given the number of items of the product to which this price applies.
     */
    public double getPriceFor(int amount) {
        PriceTableItem applicablePriceTableItem = null;
        for (PriceTableItem priceTableItem : priceTable) {
            if (priceTableItem.getMinimalAmount() <= amount) {
                applicablePriceTableItem = priceTableItem;
            }
        }
        return applicablePriceTableItem.getPrice();
    }

    /**
     * Represents a single line in a price table.
     */
    @Entity
    @Table(name = "PRODUCT_PRICE_TABLE")
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "PRICE_TABLE_ITEM_SEQ")
    public static class PriceTableItem {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQUENCE")
        private Long id;

        @Column(name = "AMOUNT")
        private int minimalAmount;

        @Column
        private double price;

        protected PriceTableItem() {}

        public PriceTableItem(int amount, double price) {
            this();
            this.minimalAmount = amount;
            this.price = price;
        }

        public int getMinimalAmount() {
            return minimalAmount;
        }

        public double getPrice() {
            return price;
        }

    }
}
