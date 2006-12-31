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

/**
 * todo Write test for price calculation
 * Boundary conditions: amount 0, negative amount
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

    public Product() {}

    public Product(ProductPrice price, String name, int minimumAge) {
        this();
        this.price = price;
        this.name = name;
        this.minimumAge = minimumAge;
    }

    public Long getId() {
        return id;
    }

    public ProductPrice getPriceStaffle() {
        return price;
    }

    public String getName() {
        return name;
    }

    public double getPriceFor(int amount) {
        return price.getPriceFor(amount);
    }

    public int getMinimumAge() {
        return minimumAge;
    }

}
