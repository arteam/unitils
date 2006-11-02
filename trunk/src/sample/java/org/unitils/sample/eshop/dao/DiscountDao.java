package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.User;

/**
 *
 */
public class DiscountDao extends HibernateDao {

    public double calculateTotalPurchaseAmount(User user) {
        return (Double) getSession().createQuery("select sum(purchaseItem.price) from PurchaseItem purchaseItem " +
                "where purchaseItem.purchase.user = :user").setParameter("user", user).uniqueResult();
    }

}
