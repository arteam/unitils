package org.unitils.dbunit.datasetfactory;

import org.unitils.dbunit.util.MultiSchemaDataSet;

import java.io.InputStream;

/**
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
     * Create a {@link MultiSchemaDataSet} using the given file. The file's name and contents are provided.
     * 
     * @param dataSetFileName The name of the dataset file
     * @param dataSetFileContents The contents of the dataset file
     * @return A {@link MultiSchemaDataSet} that represents the dataset
     */
    MultiSchemaDataSet createDataSet(String dataSetFileName, InputStream dataSetFileContents);

    /**
     * @return The extension that files which can be interpreted by this factory must have (should not start with a '.')
     */
    String getDataSetFileExtension();
}
