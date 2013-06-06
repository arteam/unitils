/*
 * Copyright 2013,  Unitils.org
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
import org.unitilsnew.core.annotation.Property;

import java.io.File;

/**
 * Resolves the location for a data set with a certain name.
 * <p/>
 * By default, the data set name is prefixed with the package name (. replaced by /).<br/>
 * E.g. MyDataSet.xml becomes com/myPackage/MyDataSet.xml
 * <p/>
 * If a data set name starts with a / it will not be prefixed with the package name.<br/>
 * E.g. /MyDataSet.xml remains /MyDataSet.xml
 * <p/>
 * Package name prefixing can be disabled using the 'dbunit.prefixWithPackageName' property.<br/>
 * prefixWithPackageName=false => MyDataSet.xml remains MyDataSet.xml
 * <p/>
 * If a path prefix is specified using the 'dbunit.pathPrefix" property it is added to the file name.<br/>
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

    /* The actual file resolver */
    protected FileResolver fileResolver;


    /**
     * Initializes the resolver with the given configuration.
     *
     * @param prefixWithPackageName True to enable to prefixing of the file name with the package name of the test class
     * @param pathPrefix            path prefix to add to the file name, null if there is no prefix
     */
    public DefaultDataSetResolver(@Property("dbunit.prefixWithPackageName") boolean prefixWithPackageName,
                                  @Property(value = "dbunit.pathPrefix", optional = true) String pathPrefix) {
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
