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

package org.unitils.io;

import org.unitils.core.Unitils;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class IOUnitils {


    public static <T> T readFileContent(Class<T> targetType, Object testInstance) {
        return readFileContent(null, targetType, null, testInstance);
    }

    public static <T> T readFileContent(Class<T> targetType, String encoding, Object testInstance) {
        return readFileContent(null, targetType, encoding, testInstance);
    }


    public static <T> T readFileContent(String fileName, Class<T> targetType, Object testInstance) {
        return readFileContent(fileName, targetType, null, testInstance);
    }

    public static <T> T readFileContent(String fileName, Class<T> targetType, String encoding, Object testInstance) {
        Class<?> testClass = testInstance.getClass();
        return getIOModule().readFileContent(fileName, targetType, encoding, testClass);
    }


    private static IOModule getIOModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(IOModule.class);
    }
}
