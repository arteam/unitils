package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;
import org.springframework.dao.support.DataAccessUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class PurchaseDao extends HibernateCrudDao<Purchase> {

    public PurchaseDao() {
        super(Purchase.class);
    }

    public long calculateTotalPurchaseAmount(User user) {
        return (Long) getSession()
                .createQuery("select sum(item.amount) from Purchase p left join p.items item where p.user = :user")
                .setParameter("user", user)
                .uniqueResult();
    }
}
