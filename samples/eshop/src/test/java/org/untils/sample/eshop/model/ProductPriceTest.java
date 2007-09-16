/*
 * Copyright 2006-2007,  Unitils.org
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
