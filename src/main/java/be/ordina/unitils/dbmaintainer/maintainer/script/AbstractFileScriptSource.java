package be.ordina.unitils.dbmaintainer.maintainer.script;

import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

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
        List<File> filesSorted = sortFilesByIndex(scriptFiles);
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
                String indexNrStr = StringUtils.substringBefore(name, "_");
                if (!StringUtils.isNumeric(indexNrStr)) {
                    return false;
                }
                return true;
            }
        }));
        return scriptFiles;
    }

    /**
     * Sorts the given list of script files according to their index number
     *
     * @param files
     * @return The sorted list of script files
     */
    protected List<File> sortFilesByIndex(List<File> files) {
        Comparator<File> indexComparator = new Comparator<File>() {
            public int compare(File file1, File file2) {
                Long file1IndexNr = getIndex(file1);
                Long file2IndexNr = getIndex(file2);
                return file1IndexNr.compareTo(file2IndexNr);
            }
        };
        Collections.sort(files, indexComparator);
        return files;
    }

    /**
     * Returns the version of the given script file
     *
     * @param scriptFile The file containing a script
     * @return The version of the script file
     */
    protected Long getIndex(File scriptFile) {
        return new Long(StringUtils.substringBefore(scriptFile.getName(), "_"));
    }

}
