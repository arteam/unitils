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
package org.unitils.database.transaction.impl;

import java.util.Set;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * Defines the contract for suppliers of a spring <code>PlatformTransactionManager</code> of a specific subtype, suitable
 * for a given test object. E.g. if some ORM persistence unit was configured on the test, a <code>PlatformTransactionManager</code>
 * that can offer transactional behavior for such a persistence unit is supplied.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface UnitilsTransactionManagementConfiguration {

	
	/**
	 * @param testObject The test object, not null
	 * @return True if this implementation is able to supply a suitable <code>PlatformTransactionManager</code> for the
	 * given test object
	 */
	boolean isApplicableFor(Object testObject);

	
	/**
	 * Returns a <code>PlatformTransactionManager</code> that can provide transactional behavior for the given test object.
	 * May only be invoked if {@link #isApplicableFor(Object)} returns true for this test object.
	 * 
	 * @param testObject The test object, not null
	 * @return A <code>PlatformTransactionManager</code> that can provide transactional behavior for the given test object.
	 */
	Set<PlatformTransactionManager> getSpringPlatformTransactionManagers(Object testObject);

}
