/**
 * Copyright 2007 La Poste, S.A. All rights reserved
 */
package org.unitils.sample.eshop.dao;

import org.hibernate.SessionFactory;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.sample.eshop.model.User;

/**
 * TODO: Document this type.
 * @author Your_Full_Name (Administrator)
 * @since 15-jul-07 12:46:40
 *
 * $Revision$
 * $LastChangedBy$
 * $LastChangedDate$ 
 */
public class HibernatePurchaseDao {

	private SessionFactory sessionFactory;

	public Long calculateTotalPurchaseAmount(User user) {
        return (Long) sessionFactory.getCurrentSession()
                .createQuery("select sum(item.amount) from Purchase p left join p.items item where p.user = :user")
                .setParameter("user", user)
                .uniqueResult();
    }
	
	/**
	 * Setter for sessionFactory.
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
}
