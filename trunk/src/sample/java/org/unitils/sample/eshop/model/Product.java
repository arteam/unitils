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
