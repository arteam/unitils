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

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.spring.SpringModule;

import java.util.Map;

/**
 * Implementation of {@link DatabaseSpringSupport}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseSpringSupportImpl implements DatabaseSpringSupport {


    public boolean isTransactionManagerConfiguredInSpring(Object testObject) {
        if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return false;
        }
        ApplicationContext context = getSpringModule().getApplicationContext(testObject);
        return context.getBeansOfType(PlatformTransactionManager.class).size() != 0;
    }


    @SuppressWarnings("unchecked")
    public PlatformTransactionManager getPlatformTransactionManager(Object testObject) {
        ApplicationContext context = getSpringModule().getApplicationContext(testObject);
        Map<String, PlatformTransactionManager> platformTransactionManagers = context.getBeansOfType(PlatformTransactionManager.class);
        if (platformTransactionManagers.size() == 0) {
            throw new UnitilsException("Could not find a bean of type " + PlatformTransactionManager.class.getSimpleName()
                    + " in the spring ApplicationContext for this class");
        }
        if (platformTransactionManagers.size() > 1) {
            throw new UnitilsException("Found more than one bean of type " + PlatformTransactionManager.class.getSimpleName()
                    + " in the spring ApplicationContext for this class");
        }
        return platformTransactionManagers.values().iterator().next();
    }


    /**
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }
}
