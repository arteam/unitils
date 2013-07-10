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
package org.unitils.dbunit.datasetfactory;

import org.unitils.core.util.FileResolver;
import org.unitils.dbunit.datasetfactory.impl.DefaultDataSetResolvingStrategy;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.annotation.Property;

/**
 * @author Tim Ducheyne
 */
public class DataSetResolvingStrategyFactory implements Factory<DataSetResolvingStrategy> {

    public static final String PREFIX_WITH_PACKAGE_NAME_PROPERTY = "dbunit.dataSet.prefixWithPackageName";
    public static final String PATH_PREFIX_PROPERTY = "dbunit.dataSet.pathPrefix";

    protected Boolean prefixWithPackageName;
    protected String pathPrefix;


    public DataSetResolvingStrategyFactory(@Property(PREFIX_WITH_PACKAGE_NAME_PROPERTY) String prefixWithPackageName, @Property(value = PATH_PREFIX_PROPERTY, optional = true) String pathPrefix) {
        this.prefixWithPackageName = Boolean.valueOf(prefixWithPackageName);
        this.pathPrefix = pathPrefix;
    }


    public DataSetResolvingStrategy create() {
        FileResolver fileResolver = new FileResolver(prefixWithPackageName, pathPrefix);
        return new DefaultDataSetResolvingStrategy(fileResolver);
    }
}
