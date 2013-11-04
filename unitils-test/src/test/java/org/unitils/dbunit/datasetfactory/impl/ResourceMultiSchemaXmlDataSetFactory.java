/*
 * Copyright (c) Smals
 */
package org.unitils.dbunit.datasetfactory.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.MultiSchemaXmlDataSetReaderExtend;
import org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;


/**
 * ResourceMultiSchemaXmlDataSetFactory - A factory to create datasets for {@link org.unitils.dbunit.annotation.DataSet} and {@link org.unitils.dbunit.annotation.ExpectedDataSet} .
 * 
 * @author wiw
 * 
 * @since 1.2.8
 */
public class ResourceMultiSchemaXmlDataSetFactory extends MultiSchemaXmlDataSetFactory {

    private static final Logger LOGGER = Logger.getLogger(ResourceMultiSchemaXmlDataSetFactory.class);
    /**
     * Creates a {@link MultiSchemaDataSet}
     * @param inputStreams
     * @return {@link MultiSchemaDataSet}
     */
    public MultiSchemaDataSet createDataSet(InputStream... inputStreams) {
        try {
            MultiSchemaXmlDataSetReaderExtend multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReaderExtend(defaultSchemaName);
            return multiSchemaXmlDataSetReader.readDataSetXml(Arrays.asList(inputStreams));
        } catch (Exception e) {
            throw new UnitilsException((new StringBuilder()).append("Unable to create DbUnit dataset for data set resourcefiles: xxx").toString(), e);
        }
    }
    /**
     * The dataSetFiles are converted into inputstreams and than the {@link MultiSchemaDataSet} is created.
     * @see org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetFactory#createDataSet(java.io.File[])
     */
    @Override
    public MultiSchemaDataSet createDataSet(File... dataSetFiles) {
        InputStream[] tempStream = new InputStream[dataSetFiles.length];
        
        for (int i = 0; i < dataSetFiles.length; i++) {
            File file = dataSetFiles[i];
            try {
                tempStream[i] = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        
        return createDataSet(tempStream);
    }
}
