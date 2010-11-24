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

import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.structure.DataSetStructureGenerator;

import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * Helper class for constructing parts of the data set module.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetStructureGeneratorFactory {

    /**
     * Property key for the xsd target directory
     */
    public static final String PROPKEY_XSD_TARGETDIRNAME = "dataset.xsd.targetDirName";

    /* The unitils configuration */
    protected Properties configuration;
    protected DataSourceWrapper dataSourceWrapper;
    protected DataSetStructureGenerator dataSetStructureGenerator;


    public DataSetStructureGeneratorFactory(Properties configuration, DataSourceWrapper dataSourceWrapper) {
        this.configuration = configuration;
        this.dataSourceWrapper = dataSourceWrapper;
    }


    public DataSetStructureGenerator getDataSetStructureGenerator() {
        if (dataSetStructureGenerator == null) {
            String defaultTargetDirectory = configuration.getProperty(PROPKEY_XSD_TARGETDIRNAME);
            dataSetStructureGenerator = getInstanceOf(DataSetStructureGenerator.class, configuration);
            dataSetStructureGenerator.init(dataSourceWrapper, defaultTargetDirectory);
        }
        return dataSetStructureGenerator;
    }
}