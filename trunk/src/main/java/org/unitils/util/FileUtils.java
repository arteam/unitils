package org.unitils.util;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.write;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


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
            closeQuietly(resourceInputStream);
            closeQuietly(fileOutputStream);
        }
    }


    /**
     * Creates an URL that points to the given file.
     *
     * @param file The file, not null
     * @return The URL to the file, not null
     */
    public static URL getUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new UnitilsException("Unable to create URL for file " + file.getName(), e);
        }
    }

    /**
     * Writes the given string to the given file
     *
     * @param file   the file to write, not null
     * @param string the string, not null
     */
    public static void writeStringToFile(File file, String string) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            write(string, out, null);
        } finally {
            closeQuietly(out);
        }
    }
}
