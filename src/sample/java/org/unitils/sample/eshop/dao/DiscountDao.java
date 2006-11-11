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

/**
 *
 */
public class DiscountDao extends HibernateDao {

    public double calculateTotalPurchaseAmount(User user) {
        return (Double) getSession().createQuery("select sum(purchaseItem.price) from PurchaseItem purchaseItem " +
                "where purchaseItem.purchase.user = :user").setParameter("user", user).uniqueResult();
    }

}
