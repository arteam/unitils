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

/**
 * Resolves the location for a data set with a certain name.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Tuomas Jormola
 */
public interface DataSetResolver {

    /**
     * Resolves the location for a data set with a certain name.
     * An exception is raised if the file could not be found.
     *
     * @param testClass   The test class, not null
     * @param dataSetName The name of the data set, not null
     * @return The data set file, not null
     */
    File resolve(Class<?> testClass, String dataSetName);

}
