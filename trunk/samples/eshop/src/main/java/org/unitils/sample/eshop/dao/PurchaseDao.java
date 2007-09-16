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
package org.unitils.sample.eshop.dao;

import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;

/**
 * DAO for the Purchase class
 */
public class PurchaseDao extends HibernateCrudDao<Purchase> {

    public PurchaseDao() {
        super(Purchase.class);
    }

    /**
     * @param user The User for who the total amount of purchased items is retrieved
     * @return The total amount of items that the given user has ever purchased, null if not found
     */
    public Long calculateTotalPurchaseAmount(User user) {
        return (Long) getSession()
                .createQuery("select sum(item.amount) from Purchase p left join p.items item where p.user = :user")
                .setParameter("user", user)
                .uniqueResult();
    }
}
