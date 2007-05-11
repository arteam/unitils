package org.untils.sample.eshop.model;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.sample.eshop.model.Product;
import org.unitils.sample.eshop.model.ProductPrice;

import static java.util.Arrays.asList;
import java.util.Arrays;

public class ProductPriceTest {

    private ProductPrice productPrice;

    @Before
    public void initFixture() {
        productPrice = new ProductPrice(Arrays.asList(
                new ProductPrice.PriceTableItem(0, 20),
                new ProductPrice.PriceTableItem(5, 15)));
    }

    @Test
    public void testGetPriceFor() {
        assertEquals(20.0, productPrice.getPriceFor(1));
        assertEquals(20.0, productPrice.getPriceFor(4));
        assertEquals(15.0, productPrice.getPriceFor(5));
    }


}
