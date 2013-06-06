/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit.datasetfactory;

import java.io.File;
import java.util.List;

/**
 * Factory for creating DbUnit data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetFactory {

    /**
     * Creates a {@link MultiSchemaDataSet} using the given file.
     *
     * @param dataSetFiles The data set files, not null
     * @return A {@link MultiSchemaDataSet} containing the data sets per schema, not null
     */
    MultiSchemaDataSet createDataSet(List<File> dataSetFiles);

    /**
     * @return The extension that files which can be interpreted by this factory must have (should not start with a '.')
     */
    String getDataSetFileExtension();
}
