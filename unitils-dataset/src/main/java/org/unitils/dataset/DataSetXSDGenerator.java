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
import org.unitils.dataset.structure.DataSetStructureGenerator;
import org.unitils.dataset.structure.DataSetStructureGeneratorFactory;

import java.io.File;

import static org.unitils.database.DatabaseUnitils.getDatabaseNames;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetXSDGenerator {


    public static void generateDataSetXSDs() {
        for (String databaseName : getDatabaseNames()) {
            generateDataSetXSDs(databaseName);
        }
    }

    public static void generateDataSetXSDs(String databaseName) {
        getDataSetStructureGenerator().generateDataSetStructureAndTemplate(databaseName);
    }


    public static void generateDataSetXSDs(File targetDirectory) {
        for (String databaseName : getDatabaseNames()) {
            generateDataSetXSDs(databaseName, targetDirectory);
        }
    }

    public static void generateDataSetXSDs(String databaseName, File targetDirectory) {
        getDataSetStructureGenerator().generateDataSetStructureAndTemplate(databaseName, targetDirectory);
    }


    private static DataSetStructureGenerator getDataSetStructureGenerator() {
        DataSetModule dataSetModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
        DataSetStructureGeneratorFactory dataSetStructureGeneratorFactory = dataSetModule.getDataSetStructureGeneratorFactory();
        return dataSetStructureGeneratorFactory.getDataSetStructureGenerator();
    }

}