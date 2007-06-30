/*
 * Copyright 2006 the original author or authors.
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

import org.unitils.dbunit.util.MultiSchemaDataSet;

import java.io.InputStream;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DataSetFactory {

	
    /**
     * Initializes this DataSetFactory
     *
     * @param defaultSchemaName The name of the default schema of the test database
     */
    void init(String defaultSchemaName);

    
    /**
     * Creates a {@link MultiSchemaDataSet} using the given file. The file's name(s) are provided.
     * 
     * @param resourceLoadClass The class from which the given files should be loaded as resource from the classpath
     * @param dataSetFileNames The names of the dataset files
     *
     * @return A {@link MultiSchemaDataSet} that represents the dataset
     */
    MultiSchemaDataSet createDataSet(Class<?> resourceLoadClass, String[] dataSetFileNames);
    
    
    /**
     * Creates a {@link MultiSchemaDataSet} using the given file. The file's contents are provided.
     *
     * @param dataSetFileContents The contents of the dataset file
     * @return A {@link MultiSchemaDataSet} that represents the dataset
     */
    MultiSchemaDataSet createDataSet(InputStream dataSetFileContents);

    
    /**
     * @return The extension that files which can be interpreted by this factory must have (should not start with a '.')
     */
    String getDataSetFileExtension();
}
