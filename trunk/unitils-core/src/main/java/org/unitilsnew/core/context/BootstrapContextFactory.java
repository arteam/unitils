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

package org.unitilsnew.core.context;

import org.unitilsnew.core.Factory;
import org.unitilsnew.core.config.Configuration;
import org.unitilsnew.core.config.UserPropertiesFactory;
import org.unitilsnew.core.engine.UnitilsTestListener;
import org.unitilsnew.core.engine.UnitilsTestListenerFactory;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class BootstrapContextFactory implements Factory<Context> {


    public Context create() {
        Configuration systemConfiguration = createSystemConfiguration();

        Context context = new Context(systemConfiguration);
        setDefaultImplementationTypes(context);
        return context;
    }


    protected void setDefaultImplementationTypes(Context context) {
        context.setDefaultImplementationType(UnitilsTestListener.class, UnitilsTestListenerFactory.class);
        context.setDefaultImplementationType(UnitilsContext.class, UnitilsContextFactory.class);
        context.setDefaultImplementationType(Properties.class, UserPropertiesFactory.class);
    }

    protected Configuration createSystemConfiguration() {
        Properties systemProperties = System.getProperties();
        return new Configuration(systemProperties);
    }
}
