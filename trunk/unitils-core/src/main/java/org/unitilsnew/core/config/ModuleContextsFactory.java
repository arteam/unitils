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

package org.unitilsnew.core.config;

import org.unitilsnew.core.Context;
import org.unitilsnew.core.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class ModuleContextsFactory implements Factory<List<Context>> {

    private List<Configuration> moduleConfigurations;


    public ModuleContextsFactory(List<Configuration> moduleConfigurations) {
        this.moduleConfigurations = moduleConfigurations;
    }


    public List<Context> create() {
        List<Context> contexts = new ArrayList<Context>();
        for (Configuration moduleConfiguration : moduleConfigurations) {
            contexts.add(new Context(moduleConfiguration));
        }
        return contexts;
    }
}
