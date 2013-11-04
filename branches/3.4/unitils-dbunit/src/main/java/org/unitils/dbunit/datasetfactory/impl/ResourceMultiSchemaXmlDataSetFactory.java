/*
 * Copyright (c) Smals
 */
package org.unitils.dbunit.datasetfactory.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReaderExtend;


/**
 * ResourceMultiSchemaXmlDataSetFactory - A factory to create datasets for {@link ResourceDataSet} and {@link ExpectedResourceDataSet} .
 * 
 * @author wiw
 * 
 * @since 1.2.8
 * 
 * @see <a href="http://unitils.org/summary.html">Unitils</a>
 */
public class ResourceMultiSchemaXmlDataSetFactory extends MultiSchemaXmlDataSetFactory {

    private static final Log LOGGER = LogFactory.getLog(ResourceMultiSchemaXmlDataSetFactory.class);
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
