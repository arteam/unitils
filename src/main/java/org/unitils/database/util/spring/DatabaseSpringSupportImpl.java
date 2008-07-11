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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Unitils;
import org.unitils.spring.SpringModule;

/**
 * Implementation of {@link DatabaseSpringSupport}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseSpringSupportImpl implements DatabaseSpringSupport {


    @SuppressWarnings("unchecked")
    public Set<PlatformTransactionManager> getPlatformTransactionManagers(Object testObject) {
    	if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return Collections.<PlatformTransactionManager>emptySet();
        }
        ApplicationContext context = getSpringModule().getApplicationContext(testObject);
        Map<String, PlatformTransactionManager> platformTransactionManagers = context.getBeansOfType(PlatformTransactionManager.class);
        return new HashSet<PlatformTransactionManager>(platformTransactionManagers.values());
    }


    /**
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }
}
