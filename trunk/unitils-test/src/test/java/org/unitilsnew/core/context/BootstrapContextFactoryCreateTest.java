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

package org.unitilsnew.core.context;

import org.junit.Test;
import org.unitilsnew.core.config.UserPropertiesFactory;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class BootstrapContextFactoryCreateTest {

    /* Tested object */
    private BootstrapContextFactory bootstrapContextFactory = new BootstrapContextFactory();


    @Test
    public void systemPropertiesLoaded() {
        Context result = bootstrapContextFactory.create();
        assertSame(System.getProperties(), result.getConfiguration().getProperties());
    }

    @Test
    public void defaultImplementationTypesInstalled() {
        Context result = bootstrapContextFactory.create();

        assertEquals(UnitilsContextFactory.class, result.getDefaultImplementationType(UnitilsContext.class));
        assertEquals(UserPropertiesFactory.class, result.getDefaultImplementationType(Properties.class));
    }

}
