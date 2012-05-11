/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core.engine;

import org.unitilsnew.core.context.BootstrapContextFactory;
import org.unitilsnew.core.context.Context;
import org.unitilsnew.core.context.UnitilsContext;

/**
 * todo move to core package
 *
 * @author Tim Ducheyne
 */
public class Unitils {

    protected static ThreadLocal<UnitilsContext> unitilsContextThreadLocal = new ThreadLocal<UnitilsContext>();


    /**
     * @return The unitils test listener
     */
    public static UnitilsTestListener getUnitilsTestListener() {
        UnitilsContext unitilsContext = getUnitilsContext();
        return unitilsContext.getInstanceOfType(UnitilsTestListener.class);
    }

    public static <T> T getInstanceOfType(Class<T> type, String... classifiers) {
        UnitilsContext unitilsContext = getUnitilsContext();
        return unitilsContext.getInstanceOfType(type, classifiers);
    }


    protected static synchronized UnitilsContext getUnitilsContext() {
        UnitilsContext unitilsContext = unitilsContextThreadLocal.get();
        if (unitilsContext == null) {
            unitilsContext = createUnitilsContext();
            unitilsContextThreadLocal.set(unitilsContext);
        }
        return unitilsContext;
    }

    protected static UnitilsContext createUnitilsContext() {
        BootstrapContextFactory bootstrapContextFactory = new BootstrapContextFactory();
        Context context = bootstrapContextFactory.create();
        return context.getInstanceOfType(UnitilsContext.class);
    }
}
