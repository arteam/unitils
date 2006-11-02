package org.unitils.sample.eshop.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 *
 */
@Entity
@Table(name = "PURCHASE_ITEM")
@SequenceGenerator(name = "SEQUENCE1", sequenceName = "PURCHASE_ITEM_ID_SEQ")
public class ShoppingBasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE1")
    private Long id;


    @Column
    private int amount;

    public ShoppingBasketItem() {}



    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        //return amount * product.getPriceFor(amount);
        return 0d;
    }

}
