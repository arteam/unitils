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
package org.unitils.database.util.spring;

import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;

/**
 * Defines the contract for implementations that retrieve an <code>PlatformTransactionManager</code> from the spring 
 * <code>ApplicationContext</code> that is configured for this class (if any)
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DatabaseSpringSupport {

	
	/**
	 * @param testObject The test object, not null
	 * @return True if an <code>ApplicationContext</code> is associated with the given test object, 
	 * and if a <code>PlatformTransactionManager</code> is available
	 */
	boolean isTransactionManagerConfiguredInSpring(Object testObject);
	
	
	/**
	 * @param testObject The test object, not null
	 * @return The instance of <code>PlatformTransactionManager</code> that is configured in the 
	 * <code>ApplicationContext</code> associated with the given test object
	 */
	PlatformTransactionManager getPlatformTransactionManager(Object testObject);
}
