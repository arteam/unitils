package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.Product;

/**
 *
 */
public class ProductDao extends HibernateCrudDao<Product> {

    public ProductDao() {
        super(Product.class);
    }
}
