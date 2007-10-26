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

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Base class for DAO's that use hibernate. Offers basic CRUD operations.
 */
public class HibernateCrudDao<T> extends HibernateDaoSupport {

    private Class<T> mappedClass;

    public HibernateCrudDao(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    public T findById(Long id) {
        return (T) getSession().get(mappedClass, id);
    }

    public void create (T object) {
        getSession().persist(object);
    }

    public void update(T object) {
        getSession().update(object);
    }

    public void delete(T object) {
        getSession().delete(object);
    }
}
