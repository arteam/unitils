package be.ordina.unitils.db.maintainer.script;

import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.db.maintainer.VersionScriptPair;

import java.util.Properties;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;

/**
 * @author Filip Neven
 */
public abstract class AbstractFileScriptSource implements ScriptSource {
    /**
     * Property key for the directory in which the script files are located
     */
    private static final String PROPKEY_SCRIPTFILES_DIR = "dbMaintainer.fileScriptSource.dir";
    /**
     * Property key for the extension of the script files
     */
    private static final String PROPKEY_SCRIPTFILES_FILEEXTENSION = "dbMaintainer.fileScriptSource.fileExtension";
    /**
     * The directory in which the script files are located
     */
    private String scriptFilesDir;
    /**
     * The extension of the script files
     */
    private String fileExtension;

    /**
     * @see ScriptSource#init(java.util.Properties)
     */
    public void init(Properties properties) {
        scriptFilesDir = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_DIR);
        if (!new File(scriptFilesDir).exists()) {
            throw new IllegalArgumentException("Script files directory '" + scriptFilesDir + "' does not exist");
        }
        fileExtension = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTFILES_FILEEXTENSION);
        if (fileExtension.startsWith(".")) {
            throw new IllegalArgumentException("Extension should not start with a '.'");
        }
    }

    /**
     * @return All available script files, sorted according to their version number
     */
    protected List<File> getScriptFilesSorted() {
        List<File> scriptFiles = getScriptFiles();
        List<File> filesSorted = sortFilesByVersion(scriptFiles);
        return filesSorted;
    }

    /**
     * @return All available script files
     */
    private List<File> getScriptFiles() {
        List<File> scriptFiles = Arrays.asList(new File(scriptFilesDir).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (!name.endsWith(fileExtension)) {
                    return false;
                }
                if (!StringUtils.contains(name, '_')) {
                    return false;
                }
                String versionNrStr = StringUtils.substringBefore(name, "_");
                if (!StringUtils.isNumeric(versionNrStr)) {
                    return false;
                }
                return true;
            }
        }));
        return scriptFiles;
    }

    /**
     * Sorts the given list of script files according to their version number
     * @param filesHavingVersion
     * @return The sorted list of script files
     */
    protected List<File> sortFilesByVersion(List<File> filesHavingVersion) {
        Comparator<File> versionComparator = new Comparator<File>() {
            public int compare(File file1, File file2) {
                Long file1VersionNr = getVersion(file1);
                Long file2VersionNr = getVersion(file2);
                return file1VersionNr.compareTo(file2VersionNr);
            }
        };
        Collections.sort(filesHavingVersion, versionComparator);
        return filesHavingVersion;
    }

    /**
     * Returns the version of the given script file
     * @param scriptFile The file containing a script
     * @return The version of the script file
     */
    protected Long getVersion(File scriptFile) {
        return new Long(StringUtils.substringBefore(scriptFile.getName(), "_"));
    }

    /**
     * Returns the scripts from the given list of script files as a list of <code>VersionScriptPair</code> objects
     * @param filesSorted The script files
     * @return The scripts as a list of <code>VersionScriptPair</code> objects
     */
    protected List<VersionScriptPair> getStatementsFromFiles(List<File> filesSorted) {
        List<VersionScriptPair> scripts = new ArrayList<VersionScriptPair>();
        for (File file : filesSorted) {
            try {
                scripts.add(new VersionScriptPair(getVersion(file),
                        FileUtils.readFileToString(file, System.getProperty("file.encoding"))));
            } catch (IOException e) {
                throw new RuntimeException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }
}
