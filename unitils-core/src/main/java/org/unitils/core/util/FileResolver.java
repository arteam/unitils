/*
 * Copyright 2011,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.core.util;

import org.unitils.core.UnitilsException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Resolves the location for a file with a certain name.
 * <p/>
 * By default, the file name is prefixed with the package name (. replaced by /).<br/>
 * E.g. MyFile.xml becomes com/myPackage/MyFile.xml
 * <p/>
 * If a file name starts with a / it will not be prefixed with the package name.<br/>
 * E.g. /MyFile.xml remains /MyFile.xml
 * <p/>
 * Package name prefixing can be disabled using the prefixWithPackageName property.<br/>
 * prefixWithPackageName=false => MyFile.xml remains MyFile.xml
 * <p/>
 * If a path prefix is specified using the pathPrefix property it is added to the file name.<br/>
 * Examples:<br/>
 * <p/>
 * pathPrefix=myPathPrefix: MyFile.xml becomes myPathPrefix/com/myPackage/MyFile.xml<br/>
 * pathPrefix=myPathPrefix: /MyFile.xml becomes myPathPrefix/MyFile.xml<br/>
 * <p/>
 * If the path prefix starts with '/', the file name is treated absolute, else it will be treated relative to the classpath.
 * <p/>
 * Examples:
 * <p/>
 * path prefix /c:/testfiles  --> looks for c:/testfiles/MyFile.xml on the file system
 * path prefix testfiles      --> looks for testfiles/MyFile.xml on the classpath
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Tuomas Jormola
 */
public class FileResolver {

    /* True if the file name should be prefixed with the package name of the test class */
    protected boolean prefixWithPackageName;
    /* An optional path prefix for the file name */
    protected String pathPrefix;


    /**
     * Creates a resolver with package prefixing enabled and no path prefix.
     */
    public FileResolver() {
        this(true, null);
    }

    /**
     * Creates a resolver.
     *
     * @param prefixWithPackageName True to enable to prefixing of the file name with the package name of the test class
     * @param pathPrefix            path prefix to add to the file name, null if there is no prefix
     */
    public FileResolver(boolean prefixWithPackageName, String pathPrefix) {
        this.prefixWithPackageName = prefixWithPackageName;
        this.pathPrefix = pathPrefix;
    }


    /**
     * Resolves the location for a file with the default name: 'classname'.'extension'.
     * An exception is raised if the file could not be found.
     *
     * @param extension The extension of the file, not null
     * @param testClass The test class, not null
     * @return The file, not null
     */
    public URI resolveDefaultFileName(String extension, Class<?> testClass) {
        String fileName = getDefaultFileName(extension, testClass);
        return resolveFileName(fileName, testClass);
    }

    /**
     * Resolves the location for a file with a certain name.
     * An exception is raised if the file could not be found.
     *
     * @param fileName  The name of the file, not null
     * @param testClass The test class, not null
     * @return The file, not null
     */
    public URI resolveFileName(String fileName, Class<?> testClass) {
        // construct file name
        String fullFileName = constructFullFileName(fileName, testClass);

        // if name starts with / treat it as absolute path
        if (fullFileName.startsWith("/")) {
            File file = new File(fullFileName);
            if (!file.exists()) {
                throw new UnitilsException("File with name " + fullFileName + " cannot be found.");
            }
            return file.toURI();
        }

        // find file in classpath
        URL fileUrl = testClass.getResource('/' + fullFileName);
        if (fileUrl == null) {
            throw new UnitilsException("File with name " + fullFileName + " cannot be found.");
        }
        try {
            return fileUrl.toURI();
        } catch (URISyntaxException e) {
            throw new UnitilsException("File with name " + fullFileName + " cannot be found.", e);
        }
    }

    public boolean isPrefixWithPackageName() {
        return prefixWithPackageName;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    /**
     * Get the full file name depending on the package prefixing and path prefix.
     *
     * @param fileName  The file name, not null
     * @param testClass The test class, not null
     * @return The file name, not null
     */
    protected String constructFullFileName(String fileName, Class<?> testClass) {
        // prefix with package name if name does not start with /
        if (prefixWithPackageName && !fileName.startsWith("/")) {
            fileName = prefixPackageNameFilePath(fileName, testClass);
        }
        // remove first char if it's a /
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        // add configured prefix
        if (pathPrefix != null) {
            fileName = pathPrefix + '/' + fileName;
        }
        return fileName;
    }

    /**
     * Prefix the package name of the test to the name of the file (replacing . with /).
     *
     * @param fileName  The file name, not null
     * @param testClass The test, not null
     * @return The file name with the package name prefix, not null
     */
    protected String prefixPackageNameFilePath(String fileName, Class<?> testClass) {
        String className = testClass.getName();
        int indexOfLastDot = className.lastIndexOf('.');
        if (indexOfLastDot == -1) {
            return fileName;
        }

        String packageName = className.substring(0, indexOfLastDot).replace('.', '/');
        return packageName + '/' + fileName;
    }

    /**
     * The default name is constructed as follows: 'classname without packagename'.'extension'
     *
     * @param extension The extension of the file
     * @param testClass The test class, not null
     * @return The default filename, not null
     */
    protected String getDefaultFileName(String extension, Class<?> testClass) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + '.' + extension;
    }

}
