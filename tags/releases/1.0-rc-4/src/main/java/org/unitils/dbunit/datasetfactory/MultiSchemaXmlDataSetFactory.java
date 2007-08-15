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

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReader;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.InputStream;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiSchemaXmlDataSetFactory implements DataSetFactory {

    private String defaultSchemaName;

    public void init(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }
    
    
    /**
     * Creates a {@link MultiSchemaDataSet} using the given file. The file's name(s) are provided.
     * 
     * @param resourceLoadClass The class from which the given files should be loaded as resource from the classpath
     * @param dataSetFileNames The names of the dataset files
     *
     * @return A {@link MultiSchemaDataSet} that represents the dataset
     */
	public MultiSchemaDataSet createDataSet(Class<?> resourceLoadClass, String[] dataSetFileNames) {
		MultiSchemaDataSet result = new MultiSchemaDataSet();
    	for (String dataSetFileName : dataSetFileNames) {
    		InputStream dataSetAsStream = resourceLoadClass.getResourceAsStream(dataSetFileName);
    		if (dataSetAsStream == null) {
    			throw new UnitilsException("DataSet file with name " + dataSetFileName + " cannot be found");
    		}
    		addDataSets(result, dataSetAsStream);
    	}
    	return result;
	}

	
	/**
     * Creates a {@link MultiSchemaDataSet} using the given file. The file's contents are provided.
     *
     * @param dataSetFileContents The contents of the dataset file
     * @return A {@link MultiSchemaDataSet} that represents the dataset
     */
    public MultiSchemaDataSet createDataSet(InputStream dataSetFileContents) {
    	MultiSchemaDataSet result = new MultiSchemaDataSet();
    	addDataSets(result, dataSetFileContents);
    	return result;
    }

    
	/**
	 * Adds the content of the given {@link InputStream} to the given {@link MultiSchemaDataSet}
	 * 
	 * @param multiSchemaDataSet  The aggregate that collects all individual DbUnit datasets
	 * @param dataSetFileContents The content of the file that is added to the {@link MultiSchemaDataSet}
	 */
	private void addDataSets(MultiSchemaDataSet multiSchemaDataSet, InputStream dataSetFileContents) {
		try {
            MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader(defaultSchemaName);
            multiSchemaXmlDataSetReader.readDataSetXml(multiSchemaDataSet, dataSetFileContents);
        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for input stream.", e);
        } finally {
            closeQuietly(dataSetFileContents);
        }
	}

    
	/**
     * @return The extension that files which can be interpreted by this factory must have
     */
	public String getDataSetFileExtension() {
        return "xml";
    }
	
}
