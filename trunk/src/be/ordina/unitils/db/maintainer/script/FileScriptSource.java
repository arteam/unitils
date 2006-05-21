/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.script;

import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of <code>ScriptSource</code> that reads script files from the filesystem. The directory, the
 * start of the file name and the file extension are configured using a Properties object by invoking the init method
 */
public class FileScriptSource implements ScriptSource {

    /**
     * Property key for the directory in which the script files are located
     */
    private static final String PROPKEY_SCRIPTFILES_DIR = "dbMaintainer.fileScriptSource.dir";

    /**
     * Property key for the start of the filenames that contain the scripts
     */
    private static final String PROPKEY_SCRIPTFILES_FILENAMESTART = "dbMaintainer.fileScriptSource.fileNameStart";

    /**
     * Property key for the extension of the script files
     */
    private static final String PROPKEY_SCRIPTFILES_FILEEXTENSION = "dbMaintainer.fileScriptSource.fileExtension";

    /**
     * The directory in which the script files are located
     */
    private String updateFilesDir;

    /**
     * The start of the filenames that contain the scripts
     */
    private String fileNameStart;

    /**
     * The extension of the script files
     */
    private String fileExtension;

    /**
     * @see ScriptSource#init(java.util.Properties)
     */
    public void init(Properties properties) {
        updateFilesDir = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_DIR);
        if (!new File(updateFilesDir).exists()) {
            throw new IllegalArgumentException("Script files directory '" + updateFilesDir + "' does not exist");
        }
        fileNameStart = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_FILENAMESTART);
        fileExtension = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_FILEEXTENSION);
        if (fileExtension.startsWith(".")) {
            throw new IllegalArgumentException("Extension should not start with a '.'");
        }
    }

    /**
     * @see ScriptSource#getScript(long)
     */
    public String getScript(long version) {
        File changeFile = new File(updateFilesDir + '/' + fileNameStart + version + "." + fileExtension);
        if (!changeFile.exists()) {
            return null;
        }
        try {
            return FileUtils.readFileToString(changeFile, System.getProperty("file.encoding"));
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to read file " + changeFile);
        }
    }

}
