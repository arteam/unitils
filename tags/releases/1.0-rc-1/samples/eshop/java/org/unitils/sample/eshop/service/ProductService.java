package org.unitils.sample.eshop.service;

import org.unitils.sample.eshop.dao.ProductDao;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProductService {

    public ProductService() {
        System.out.println("ProductService");
    }

    private ProductDao productDao;

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

}
