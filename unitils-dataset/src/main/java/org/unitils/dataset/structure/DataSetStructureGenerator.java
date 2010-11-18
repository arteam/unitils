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
package org.unitils.dataset.structure;

import org.unitils.dataset.database.DataSourceWrapper;

import java.io.File;

/**
 * Generator for XSDs, DTDs or other structure descriptors of database schemas.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetStructureGenerator {

    /**
     * @param dataSourceWrapper      The database meta data, not null
     * @param defaultTargetDirectory The default target directory, null if generation should be skipped
     */
    void init(DataSourceWrapper dataSourceWrapper, String defaultTargetDirectory);

    /**
     * Generates both the XSDs or DTDs and the template xml using the default target directory.
     * If the default target directory is null, generation will be skipped.
     */
    void generateDataSetStructureAndTemplate();

    /**
     * Generates both the XSDs or DTDs and the template xml.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    void generateDataSetStructureAndTemplate(File targetDirectory);

    /**
     * Generates the XSDs or DTDs.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    void generateDataSetStructure(File targetDirectory);

    /**
     * Generates a sample template xml file that can be used as a starting point for writing a data set file.
     * E.g. an xml file with the name spaces already filled in correctly.
     *
     * @param targetDirectory The target directory for the files, not null
     */
    void generateDataSetTemplateXmlFile(File targetDirectory);

}