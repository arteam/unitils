package org.untils.sample.eshop.model;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.unitils.sample.eshop.model.ProductPrice;

import static java.util.Arrays.asList;

public class ProductPriceTest {

    /* Object under test */
    private ProductPrice productPrice;


    /**
     * Sets up a table of product prices.
     */
    @Before
    public void initFixture() {
        productPrice = new ProductPrice(asList(new ProductPrice.PriceTableItem(0, 20), new ProductPrice.PriceTableItem(5, 15)));
    }


    @Test
    public void testGetPriceFor() {
        double result = productPrice.getPriceFor(5);
        assertEquals(15.0, result);
    }


}
