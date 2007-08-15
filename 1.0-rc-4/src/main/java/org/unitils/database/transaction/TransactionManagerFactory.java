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
package org.unitils.database.transaction;

import java.util.Properties;

/**
 * Interface for factories of TransactionManagers.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface TransactionManagerFactory {

    /**
     * Initializes itself using the properties in the given configuration.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration);


    /**
     * Creates a new {@link TransactionManager}
     *
     * @return The TransactionManager, not null
     */
    public TransactionManager createTransactionManager();

}
