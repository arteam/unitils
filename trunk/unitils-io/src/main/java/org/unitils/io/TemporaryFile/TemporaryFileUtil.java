package org.unitils.io.TemporaryFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.thirdparty.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class TemporaryFileUtil {

    private static Log logger = LogFactory.getLog(TemporaryFileUtil.class);


    File temporaryRootFolder;

    public TemporaryFileUtil(File temporaryRootFolder) {
        this.temporaryRootFolder = temporaryRootFolder;
    }

    public File createTemporaryFolder(String fileName) {
        File result = new File(temporaryRootFolder, fileName);
        Boolean create = result.mkdirs();
        if (create) {
            logger.warn("Folder " + fileName + " in folder " + temporaryRootFolder.getAbsolutePath() + " could not be created, it could be that it already exists");
        }
        return result;

    }

    public File createTemporaryFile(String fileName) {
        try {
            File result = new File(temporaryRootFolder, fileName);
            Boolean create = result.createNewFile();
            if (create) {
                logger.warn("File " + fileName + " in folder " + temporaryRootFolder.getAbsolutePath() + " already exists");
            }
            return result;
        } catch (IOException e) {
            throw new UnitilsException("Could not create file " + fileName + " in folder " + temporaryRootFolder.getAbsolutePath(), e);
        }

    }


    public void removeTemporaryFile(File file) {

        try {
            if (file.isDirectory()) {
                FileUtils.cleanDirectory(file);
            }
            file.delete();
        } catch (IOException e) {
            throw new UnitilsException(file.getAbsolutePath() + " could not be removed", e);
        }
    }

}
