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

package org.unitils.inject.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitilsnew.core.reflect.ClassWrapper;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * @author Tim Ducheyne
 */
public class TestedObjectService {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(TestedObjectService.class);


    /**
     * Creates an instance of the tested object with the given type.
     *
     * @param testedObjectType The type of the tested object, not null
     * @return The instance, null if it could not be created
     */
    public Object createTestedObject(Class<?> testedObjectType) {
        if (testedObjectType.isInterface()) {
            logger.warn("Tested object could not be created. Tested object type is an interface: " + testedObjectType);
            return null;
        }
        if (isAbstract(testedObjectType.getModifiers())) {
            logger.warn("Tested object is not automatically created. Tested object type is an abstract class: " + testedObjectType);
            return null;
        }
        try {
            ClassWrapper classWrapper = new ClassWrapper(testedObjectType);
            return classWrapper.createInstance();

        } catch (Exception e) {
            logger.warn("Tested object could not be created. " + e.getMessage());
            return null;
        }
    }
}
