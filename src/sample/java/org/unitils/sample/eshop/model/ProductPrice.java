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
