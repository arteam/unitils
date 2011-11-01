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

package org.unitils.io.reader.impl;

import org.unitils.core.util.FileResolver;
import org.unitils.io.reader.FileResolvingStrategy;
import org.unitils.io.reader.FileResolvingStrategyFactory;

import java.util.Properties;

import static org.unitils.util.PropertyUtils.getBoolean;
import static org.unitils.util.PropertyUtils.getString;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultFileResolvingStrategyFactory implements FileResolvingStrategyFactory {

    /* Property key for the path prefix */
    public static final String PREFIX_WITH_PACKAGE_NAME_PROPERTY = "IoModule.file.prefixWithPackageName";
    /* Property key for the path prefix */
    public static final String PATH_PREFIX_PROPERTY = "IoModule.file.pathPrefix";


    public FileResolvingStrategy createFileResolvingStrategy(Properties configuration) {
        boolean prefixWithPackageName = getBoolean(PREFIX_WITH_PACKAGE_NAME_PROPERTY, configuration);
        String pathPrefix = getString(PATH_PREFIX_PROPERTY, null, configuration);

        FileResolver fileResolver = new FileResolver(prefixWithPackageName, pathPrefix);
        return new DefaultFileResolvingStrategy(fileResolver);
    }
}
