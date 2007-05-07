package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;
import org.springframework.dao.support.DataAccessUtils;

/**
 * DAO for the Purchase class
 */
public class PurchaseDao extends HibernateCrudDao<Purchase> {

    public PurchaseDao() {
        super(Purchase.class);
    }

    /**
     * @param user The User for who the total amount of purchased items is retrieved
     * @return The total amount of items that the given user has ever purchased
     */
    public long calculateTotalPurchaseAmount(User user) {
        return (Long) getSession()
                .createQuery("select sum(item.amount) from Purchase p left join p.items item where p.user = :user")
                .setParameter("user", user)
                .uniqueResult();
    }
}
