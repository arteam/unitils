/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.User;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.MatchMode;

import java.util.List;

/**
 * DAO for the User class
 */
public class UserDao extends HibernateCrudDao<User> {

    public UserDao() {
        super(User.class);
    }

    /**
     * Find users by last name.
     * @param lastName the last name used to search users.
     * @return a list of users for which the last name contains the specified search string.
     */
    @SuppressWarnings("unchecked")
    public List<User> findByLastName(String lastName) {
        return getSession()
                .createCriteria(User.class)
                .add(Restrictions.like("lastName", lastName, MatchMode.ANYWHERE))
                .list();
    }

}