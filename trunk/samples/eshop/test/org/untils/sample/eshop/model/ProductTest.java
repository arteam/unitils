package org.untils.sample.eshop.model;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.sample.eshop.model.Product;
import org.unitils.sample.eshop.model.ProductPrice;

import static java.util.Arrays.asList;

public class ProductTest extends UnitilsJUnit4 {

    private Product product;

    @Before
    public void initFixture() {
        ProductPrice price = new ProductPrice(asList(new ProductPrice.PriceTableItem(0, 20),
                new ProductPrice.PriceTableItem(5, 15)));
        product = new Product(0L, price, null, 0);
    }

    @Test
    public void testGetPriceFor() {
        assertEquals(20.0, product.getPriceFor(1));
        assertEquals(20.0, product.getPriceFor(4));
        assertEquals(15.0, product.getPriceFor(5));
    }
}
