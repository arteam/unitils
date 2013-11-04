/*
 * Copyright (c) Smals
 */
package org.unitils.dbunit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;


/**
 * A class to create/delete tempFiles .
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
public class FileHandler {
    private static final Log LOGGER = LogFactory.getLog(FileHandler.class);
    /**
     * Create temporary files to store the XML.
     * @param dataSetResourceName
     * @return
     */
    public File createTempFile(String dataSetResourceName) {
        LOGGER.debug("Creating temp file.");
        String tempDataSetResourceName = new String(dataSetResourceName);
        if (tempDataSetResourceName.endsWith("/")) {
            tempDataSetResourceName = tempDataSetResourceName.substring(0, tempDataSetResourceName.length() - 1);
        }
        String nameFile = "";
        if (tempDataSetResourceName.contains("/")) {
            nameFile = tempDataSetResourceName.substring(tempDataSetResourceName.lastIndexOf("/"), tempDataSetResourceName.lastIndexOf("."));
        } else {
            nameFile = tempDataSetResourceName.substring(0, tempDataSetResourceName.lastIndexOf("."));
        }
        
        try {
            return File.createTempFile(nameFile + "-", ".xml");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Write {@link InputStream} to {@link File}
     * @param file
     */
    public void writeToFile(File file, InputStream in) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            LOGGER.debug("Writing preprocessed dataset to temp file: " + file.getAbsolutePath());

            if (in.markSupported()) {
                in.mark(Integer.MAX_VALUE);
                IOUtils.copy(in, fos);
                in.reset();
            } else {
                IOUtils.copy(in, fos);
            }
            LOGGER.trace("inputstream visualised: \n" + IOUtils.toString(in));
            fos.close();
            LOGGER.debug("End writing preprocessed dataset to file: " + file.getAbsolutePath());
        } catch(IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * Delete all the files
     * @param files
     */
    public void deleteFiles(List<File> files) {
        for (File file : files) {
            LOGGER.debug("Deleting temp file: " + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            } else {
                LOGGER.warn("File " + file.getName() + " not found.");
            }
        }
    }
}
