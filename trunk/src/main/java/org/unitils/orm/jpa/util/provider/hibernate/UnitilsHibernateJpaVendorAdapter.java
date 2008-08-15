/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.orm.jpa.util.provider.hibernate;

import javax.persistence.spi.PersistenceProvider;

import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Custom implementation of spring's <code>HibernateJpaVendorAdapter</code> that supplies a custom
 * subclass of <code>HibernatePeristence</code> as <code>PersistenceProvider</code> for hibernate JPA.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsHibernateJpaVendorAdapter extends HibernateJpaVendorAdapter {

	@Override
	public PersistenceProvider getPersistenceProvider() {
		return new UnitilsHibernatePersistenceProvider();
	}

	
}
