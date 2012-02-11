/*
 * Copyright 2010,  Unitils.org
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

package org.unitilsnew.core;

import org.unitilsnew.core.config.BootstrapContextFactory;

import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class Unitils {

    /* The singleton instance */
    private static Unitils unitils;

    /**
     * Returns the singleton instance
     *
     * @return the singleton instance, not null
     */
    public static synchronized Unitils getInstance() {
        if (unitils == null) {
            unitils = bootstrapUnitils();
        }
        return unitils;
    }

    protected static Unitils bootstrapUnitils() {
        BootstrapContextFactory bootstrapContextFactory = new BootstrapContextFactory();
        Context bootstrapContext = bootstrapContextFactory.create();
        return bootstrapContext.getInstanceOfType(Unitils.class);
    }


    private List<Context> moduleContexts;

    public Unitils(List<Context> moduleContexts) {
        this.moduleContexts = moduleContexts;
    }
}
