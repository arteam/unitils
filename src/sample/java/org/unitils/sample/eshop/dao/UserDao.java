package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.User;

/**
 *
 */
public class UserDao extends HibernateCrudDao<User> {

    public UserDao() {
        super(User.class);
    }

}
