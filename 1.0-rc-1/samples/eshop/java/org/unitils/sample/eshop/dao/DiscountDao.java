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
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.dao.support.DataAccessUtils;
import org.hibernate.Session;

/**
 *
 */
public class DiscountDao extends HibernateDaoSupport {

    public long calculateTotalPurchaseAmount(User user) {

        Long result = (Long) DataAccessUtils.requiredUniqueResult(getHibernateTemplate().findByNamedParam("select sum(item.amount) " +
                "from ShoppingBasket basket left join basket.items item where basket.user = :user", "user", user));
        return result;
    }

}
