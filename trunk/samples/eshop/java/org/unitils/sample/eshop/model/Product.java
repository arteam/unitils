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


import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Embedded;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;

/**
 * Represents an eshop Product
 */
@Entity
@Table(name = "PRODUCT")
@SequenceGenerator(name = "SEQUENCE", sequenceName = "PRODUCT_ID_SEQ")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQUENCE")
    private Long id;

    @Embedded
    private ProductPrice price;

    @Column
    private String name;

    @Column
    private int minimumAge;

    /**
     * Empty constructor. Exists only to enable proxying.
     */
    protected Product() {}

    /**
     * Constructor that initializes a product with a given ProductPrice, name and minimumAge

     * @param id
     * @param price
     * @param name
     * @param minimumAge
     */
    public Product(long id, ProductPrice price, String name, int minimumAge) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.minimumAge = minimumAge;
    }

    /**
     * Convience constructor for products having a single price
     *
     * @param id
     * @param price
     * @param name
     * @param minimumAge
     */
    public Product(long id, double price, String name, int minimumAge) {
        this(id, new ProductPrice(price), name, minimumAge);
    }

    public Long getId() {
        return id;
    }

    public ProductPrice getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    /**
     * @param amount The amount of products that one wants to buy of this product
     * @return The price for one single instance of this Product, given the total amount of items the user wants to
     *         buy.
     */
    public double getPriceFor(int amount) {
        return price.getPriceFor(amount);
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!id.equals(product.id)) return false;

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
