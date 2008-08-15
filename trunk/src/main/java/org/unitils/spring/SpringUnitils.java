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
package org.unitils.spring;

import org.unitils.core.Unitils;

/**
 * Utility facade for handling Spring things such as invalidating a cached application context.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringUnitils {


    /**
     * Forces the reloading of the application context the next time that it is requested. If classes are given
     * only contexts that are linked to those classes will be reset. If no classes are given, all cached
     * contexts will be reset.
     *
     * @param classes The classes for which to reset the contexts
     */
    public static void invalidateApplicationContext(Class<?>... classes) {
        SpringModule springModule = Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
        springModule.invalidateApplicationContext(classes);
    }

}
