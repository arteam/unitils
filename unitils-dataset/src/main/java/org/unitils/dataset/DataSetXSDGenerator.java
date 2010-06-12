/*
 * Copyright Unitils.org
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
package org.unitils.dataset;

import org.unitils.core.Unitils;

import java.io.File;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetXSDGenerator {


    public static void generateDataSetXSDs() {
        getDataSetModule().generateDataSetXSDs();
    }

    public static void generateDataSetXSDs(File targetDirectory) {
        getDataSetModule().generateDataSetXSDs(targetDirectory);
    }


    /**
     * Gets the instance DataSetModule that is registered in the modules repository.
     * This instance implements the actual behavior of the static methods in this class.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     */
    private static DataSetModule getDataSetModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
    }
}