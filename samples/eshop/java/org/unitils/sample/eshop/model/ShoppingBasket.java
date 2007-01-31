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
package org.unitils.sample.eshop.model;

import org.unitils.sample.eshop.dao.UserDao;
import org.unitils.sample.eshop.dao.DiscountDao;
import org.unitils.sample.eshop.exception.EShopException;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
@Entity
@Table(name = "PURCHASE")
@SequenceGenerator(name = "SEQUENCE", sequenceName = "PURCHASE_ID_SEQ")
public class ShoppingBasket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany
    @JoinColumn(name = "PURCHASE_ID")
    private List<ShoppingBasketItem> items = new ArrayList<ShoppingBasketItem>();

    public ShoppingBasket() {}

    public User getUser() {
        return user;
    }

    public List<ShoppingBasketItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double totalPrice = 0d;
        for (ShoppingBasketItem item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    public void checkout(Long userId) {
        User user = getUserDao().findById(userId);
        if (!isOldEnough(user)) {
            throw new EShopException("User not old enough");
        }
        double price = getTotalPrice();
        double discount = getLoyalUserDiscount(user);
        price -= price * discount;
    }

    private double getLoyalUserDiscount(User user) {
        double totalPurchaseAmount = getDiscountDao().calculateTotalPurchaseAmount(user);
        if (totalPurchaseAmount > 1000) {
            return .05;
        } else {
            return 0;
        }
    }

    private boolean isOldEnough(User user) {
        for (ShoppingBasketItem item : items) {
            /*if (user.getAge() < item.getProduct().getMinimumAge()) {
                return false;
            }*/
        }
        return true;
    }

    private UserDao getUserDao() {
        return new UserDao();
    }

    private DiscountDao getDiscountDao() {
        return new DiscountDao();
    }
}
