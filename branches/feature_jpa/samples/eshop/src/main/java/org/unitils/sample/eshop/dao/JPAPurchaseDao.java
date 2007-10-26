package org.unitils.sample.eshop.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;

public class JPAPurchaseDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Purchase findById(Long id) {
		return entityManager.find(Purchase.class, id);
	}

	/**
     * @param user The User for who the total amount of purchased items is retrieved
     * @return The total amount of items that the given user has ever purchased, null if not found
     */
    public Long calculateTotalPurchaseAmount(User user) {
        return (Long) entityManager
                .createQuery("select sum(item.amount) from org.unitils.sample.eshop.model.Purchase as p join p.items item where p.user = :user")
                .setParameter("user", user)
                .getSingleResult();
    }
}
