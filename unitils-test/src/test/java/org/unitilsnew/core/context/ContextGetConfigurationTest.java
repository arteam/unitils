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

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.config.Configuration;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ContextGetConfigurationTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;
    @Dummy
    private Configuration configuration;


    @Before
    public void initialize() {
        context = new Context(configuration);
    }


    @Test
    public void getConfiguration() {
        Configuration result = context.getConfiguration();
        assertSame(configuration, result);
    }
}
