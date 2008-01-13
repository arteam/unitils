/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbunit.datasetfactory.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetResolver;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;
import static org.unitils.util.PropertyUtils.getBoolean;
import static org.unitils.util.PropertyUtils.getString;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * Resolves the location for a data set with a certain name.
 * <p/>
 * If needed, the data set is prefixed with the package name (. replaced by /)
 * If a path prefix is specified it is added to the file name.
 * Package name and prefix can be specified with the {@link #PROPKEY_PREFIX_WITH_PACKAGE_NAME} and
 * {@link #PROPKEY_DATA_SET_PATH_PREFIX} properties.
 * <p/>
 * Examples:
 * <p/>
 * prefixWithPackageName=true, pathPrefix=myPathPrefix:  myPathPrefix/org/unitils/test/myDataSet.xml
 * prefixWithPackageName=false, pathPrefix=myPathPrefix: myPathPrefix/myDataSet.xml
 * prefixWithPackageName=true, no pathPrefix:            org/unitils/test/myDataSet.xml
 * prefixWithPackageName=false, no pathPrefix:           myDataSet.xml
 * <p/>
 * If the path prefix starts with '/', the file name is treated absolute, else it will be treated relative to the
 * classpath.
 * <p/>
 * Examples:
 * <p/>
 * path prefix /c:/datasets  --> looks for c:/datasets/myDataSet.xml on the file system
 * path prefix datasets      --> looks for datasets/myDataSet.xml on the classpath
 * <p/>
 * Special thanks to Tuomas Jormola for the input and code contribution for resolving data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Tuomas Jormola
 */
public class DefaultDataSetResolver implements DataSetResolver {

    /**
     * Property key for the path prefix
     */
    public static final String PROPKEY_PREFIX_WITH_PACKAGE_NAME = "dbUnit.datasetresolver.prefixWithPackageName";

    /**
     * Property key for the path prefix
     */
    public static final String PROPKEY_DATA_SET_PATH_PREFIX = "dbUnit.datasetresolver.pathPrefix";

    /**
     * True if the file name should be prefixed with the package name of the test class.
     */
    protected boolean prefixWithPackageName;

    /**
     * An optional path prefix for the file name.
     */
    protected String pathPrefix;


    /**
     * Initializes the resolver with the given configuration.
     *
     * @param configuration The configuration, not null
     */
    public void init(Properties configuration) {
        this.prefixWithPackageName = getBoolean(PROPKEY_PREFIX_WITH_PACKAGE_NAME, configuration);
        this.pathPrefix = getString(PROPKEY_DATA_SET_PATH_PREFIX, null, configuration);
    }


    /**
     * Resolves the location for a data set with a certain name.
     * An exception is raised if the file could not be found.
     *
     * @param testClass   The test class, not null
     * @param dataSetName The name of the data set, not null
     * @return The data set file, not null
     */
    public File resolve(Class<?> testClass, String dataSetName) {
        // construct file name
        String dataSetFileName = getDataSetFileName(testClass, dataSetName);

        // if name starts with / treat it as absolute path
        if (dataSetFileName.startsWith("/")) {
            File dataSetFile = new File(dataSetFileName);
            if (!dataSetFile.exists()) {
                throw new UnitilsException("DataSet file with name " + dataSetName + " cannot be found");
            }
            return dataSetFile;
        }

        // find file in classpath
        URL dataSetUrl = testClass.getResource('/' + dataSetFileName);
        if (dataSetUrl == null) {
            throw new UnitilsException("DataSet file with name " + dataSetName + " cannot be found");
        }
        return toFile(dataSetUrl);
    }


    /**
     * Get the file name for the data set.
     *
     * @param testClass   The test class, not null
     * @param dataSetName The data set name, not null
     * @return The file name, not null
     */
    protected String getDataSetFileName(Class<?> testClass, String dataSetName) {
        if (prefixWithPackageName) {
            dataSetName = prefixPackageNameFilePath(testClass, dataSetName);
        }

        if (pathPrefix != null) {
            dataSetName = pathPrefix + '/' + dataSetName;
        }
        return dataSetName;
    }


    protected String prefixPackageNameFilePath(Class<?> testClass, String dataSetName) {
        String className = testClass.getName();
        int indexOfLastDot = className.lastIndexOf('.');
        if (indexOfLastDot == -1) {
        	return dataSetName;
        }
    
        String packageName = indexOfLastDot == -1 ? "" : className.substring(0, indexOfLastDot).replace('.', '/');
        return packageName + '/' + dataSetName;
    }

}
