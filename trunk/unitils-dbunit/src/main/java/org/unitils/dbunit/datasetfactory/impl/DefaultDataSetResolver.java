/*
 * Copyright 2008,  Unitils.org
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

import org.unitils.core.util.FileResolver;
import org.unitils.dbunit.datasetfactory.DataSetResolver;

import java.io.File;
import java.util.Properties;

import static org.unitils.util.PropertyUtils.getBoolean;
import static org.unitils.util.PropertyUtils.getString;

/**
 * Resolves the location for a data set with a certain name.
 * <p/>
 * By default, the data set name is prefixed with the package name (. replaced by /).<br/>
 * E.g. MyDataSet.xml becomes com/myPackage/MyDataSet.xml
 * <p/>
 * If a data set name starts with a / it will not be prefixed with the package name.<br/>
 * E.g. /MyDataSet.xml remains /MyDataSet.xml
 * <p/>
 * Package name prefixing can be disabled using the {@link #PROPKEY_PREFIX_WITH_PACKAGE_NAME} property.<br/>
 * prefixWithPackageName=false => MyDataSet.xml remains MyDataSet.xml
 * <p/>
 * If a path prefix is specified using the {@link #PROPKEY_DATA_SET_PATH_PREFIX} property it is added to the file name.<br/>
 * Examples:<br/>
 * <p/>
 * pathPrefix=myPathPrefix: MyDataSet.xml becomes myPathPrefix/org/unitils/test/MyDataSet.xml<br/>
 * pathPrefix=myPathPrefix: /MyDataSet.xml becomes myPathPrefix/MyDataSet.xml<br/>
 * <p/>
 * If the path prefix with '/', the file name is treated absolute, else it will be treated relative to the classpath.
 * <p/>
 * Examples:
 * <p/>
 * path prefix /c:/datasets  --> looks for c:/datasets/myDataSet.xml on the file system
 * path prefix datasets      --> looks for datasets/myDataSet.xml on the classpath
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Tuomas Jormola
 */
public class DefaultDataSetResolver implements DataSetResolver {

    /* Property key for the path prefix */
    public static final String PROPKEY_PREFIX_WITH_PACKAGE_NAME = "dbUnit.datasetresolver.prefixWithPackageName";
    /* Property key for the path prefix */
    public static final String PROPKEY_DATA_SET_PATH_PREFIX = "dbUnit.datasetresolver.pathPrefix";

    /* The actual file resolver */
    protected FileResolver fileResolver;


    /**
     * Initializes the resolver with the given configuration.
     *
     * @param configuration The configuration, not null
     */
    public void init(Properties configuration) {
        boolean prefixWithPackageName = getBoolean(PROPKEY_PREFIX_WITH_PACKAGE_NAME, configuration);
        String pathPrefix = getString(PROPKEY_DATA_SET_PATH_PREFIX, null, configuration);

        this.fileResolver = new FileResolver(prefixWithPackageName, pathPrefix);
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
        return new File(fileResolver.resolveFileName(dataSetName, testClass));
    }

}
