/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.script;

import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.Properties;
import java.util.List;

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
     * Property key for the extension of the script files
     */
    private static final String PROPKEY_SCRIPTFILES_FILEEXTENSION = "dbMaintainer.fileScriptSource.fileExtension";

    /**
     * Property key for the length of the version nr
     */
    private static final String PROPKEY_SCRIPTFILES_VERSIONNRLENGTH = "dbMaintainer.fileScriptSource.versionNrLength";

    /**
     * The directory in which the script files are located
     */
    private String updateFilesDir;

    /**
     * The length of the version nr in the filenames of the scripts
     */
    private int versionNrLength;

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
        fileExtension = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_FILEEXTENSION);
        if (fileExtension.startsWith(".")) {
            throw new IllegalArgumentException("Extension should not start with a '.'");
        }
        try {
            versionNrLength = Integer.parseInt(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_VERSIONNRLENGTH));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property " + PROPKEY_SCRIPTFILES_VERSIONNRLENGTH + " does not contain valid number", e);
        }
    }

    /**
     * @see ScriptSource#getScript(long)
     */
    public String getScript(long version) {
        final String fileNameStart = StringUtils.leftPad("" + version, versionNrLength, '0') + '_';
        File[] filesHavingVersion = new File(updateFilesDir).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(fileNameStart) && name.endsWith(fileExtension);
            }
        });
        if (filesHavingVersion.length == 0) {
            return null;
        }
        if (filesHavingVersion.length > 1) {
            throw new RuntimeException("Multiple files found starting with " + fileNameStart);
        }
        File changeFile = filesHavingVersion[0];
        try {
            return FileUtils.readFileToString(changeFile, System.getProperty("file.encoding"));
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to read file " + changeFile);
        }
    }

}
