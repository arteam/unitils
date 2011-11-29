package org.unitils.io;

import org.unitils.core.UnitilsException;
import org.unitils.io.TemporaryFile.TemporaryFileUtil;
import org.unitils.io.annotation.handler.TemporaryFileAnnotationHandler;

import java.io.File;
import java.util.Properties;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */

public class TemporaryFileListenerFactory {

    protected static final String DEFAULT_ROOT_DIRECTORY = " IOModule.temp.directory";
    protected static final String DEFAULT_REMOVE_AFTER_TEST = "IOModule.temp.cleanup";

    public static TemporaryFileAnnotationHandler createTemporaryFileListener(
            Properties properties) {

        Boolean defaultRemoveAterTest = figureOutRemove(properties);
        File defaultRootDirectory = figureOutRootDir(properties);

        TemporaryFileUtil fileUtil = new TemporaryFileUtil(defaultRootDirectory);

        return new TemporaryFileAnnotationHandler(fileUtil, defaultRootDirectory,
                defaultRemoveAterTest);
    }

    private static File figureOutRootDir(Properties properties) {
        String stringRootDirProperty = properties
                .getProperty(DEFAULT_ROOT_DIRECTORY);
        if (stringRootDirProperty == null || stringRootDirProperty.isEmpty()) {
            stringRootDirProperty = System.getProperty("java.io.tmpdir");
        }

        File result = new File(stringRootDirProperty);
        if (result.isFile()) {
            throw new UnitilsException(stringRootDirProperty
                    + " is not a directory, fill in a directory for property :"
                    + DEFAULT_ROOT_DIRECTORY);
        }
        result.mkdirs();

        return result;
    }

    private static Boolean figureOutRemove(Properties properties) {
        String defaultRemoveStringValue = properties
                .getProperty(DEFAULT_REMOVE_AFTER_TEST);

        if ("true".equals(defaultRemoveStringValue)) {
            return true;
        } else if ("false".equals(defaultRemoveStringValue)) {
            return false;
        } else {
            throw new UnitilsException(DEFAULT_REMOVE_AFTER_TEST
                    + " should be true or false not "
                    + defaultRemoveStringValue);
        }
    }

}
