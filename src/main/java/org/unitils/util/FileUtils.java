package org.unitils.util;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.*;


// todo javadoc
public class FileUtils {

    public static void copyClassPathResource(String classPathResourceName, String fileSystemDirectoryName) {
        InputStream resourceInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            resourceInputStream = FileUtils.class.getResourceAsStream(classPathResourceName);
            String fileName = StringUtils.substringAfterLast(classPathResourceName, "/");
            File fileSystemDirectory = new File(fileSystemDirectoryName);
            fileSystemDirectory.mkdirs();
            fileOutputStream = new FileOutputStream(fileSystemDirectoryName + "/" + fileName);
            IOUtils.copy(resourceInputStream, fileOutputStream);
        } catch (IOException e) {
            throw new UnitilsException(e);
        } finally {
            IOUtils.closeQuietly(resourceInputStream);
            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}
